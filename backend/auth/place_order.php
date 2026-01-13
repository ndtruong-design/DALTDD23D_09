<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php"; 

$input = file_get_contents("php://input");
$data = json_decode($input, true);

$maKhachHang = intval($data['MaKhachHang'] ?? 0);
$items = $data['items'] ?? [];
$method = $data['MaPTTT'] ?? 'COD';

if ($maKhachHang <= 0 || empty($items)) {
    echo json_encode(["success" => false, "message" => "Dữ liệu không hợp lệ"]);
    exit;
}

try {
    $conn->beginTransaction();
    $sqlExpired = "SELECT MaDonHang FROM DonHang 
                   WHERE TrangThaiThanhToan = 0 
                   AND NgayDatHang < DATE_SUB(NOW(), INTERVAL 5 DAY)";
    $stmtExpired = $conn->query($sqlExpired);
    $expiredOrders = $stmtExpired->fetchAll(PDO::FETCH_COLUMN);

    if (!empty($expiredOrders)) {
        foreach ($expiredOrders as $oldOrderId) {
         
            $stmtOldItems = $conn->prepare("SELECT MaChiTietSP, SoLuong FROM ChiTietDonHang WHERE MaDonHang = ?");
            $stmtOldItems->execute([$oldOrderId]);
            $oldItems = $stmtOldItems->fetchAll(PDO::FETCH_ASSOC);

            foreach ($oldItems as $oldItem) {
                $idSP = $oldItem['MaChiTietSP'];
                $qty = $oldItem['SoLuong'];
                $stmtRestoreStock = $conn->prepare("UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaChiTietSP = ?");
                $stmtRestoreStock->execute([$qty, $idSP]);
                $stmtBackToCart = $conn->prepare("INSERT INTO GioHang (MaKhachHang, MaChiTietSP, SoLuong) 
                                                  VALUES (?, ?, ?) 
                                                  ON DUPLICATE KEY UPDATE SoLuong = SoLuong + VALUES(SoLuong)");
                $stmtBackToCart->execute([$maKhachHang, $idSP, $qty]);
            }


            $conn->prepare("DELETE FROM ChiTietDonHang WHERE MaDonHang = ?")->execute([$oldOrderId]);
            $conn->prepare("DELETE FROM DonHang WHERE MaDonHang = ?")->execute([$oldOrderId]);
        }
    }

    $maCT_list = array_column($items, 'MaChiTietSP');
    $placeholders = implode(',', array_fill(0, count($maCT_list), '?'));
    $stmtCheck = $conn->prepare("SELECT MaChiTietSP, Gia, SoLuongTon FROM ChiTietSanPham WHERE MaChiTietSP IN ($placeholders)");
    $stmtCheck->execute($maCT_list);
    $products = $stmtCheck->fetchAll(PDO::FETCH_ASSOC);
    
    $priceMap = [];
    $stockMap = [];
    foreach ($products as $p) {
        $priceMap[$p['MaChiTietSP']] = $p['Gia'];
        $stockMap[$p['MaChiTietSP']] = $p['SoLuongTon'];
    }

    $totalAmount = 0;
    foreach ($items as $item) {
        if ($item['SoLuong'] > ($stockMap[$item['MaChiTietSP']] ?? 0)) {
            throw new Exception("Sản phẩm ID " . $item['MaChiTietSP'] . " không đủ số lượng trong kho.");
        }
        $totalAmount += $priceMap[$item['MaChiTietSP']] * $item['SoLuong'];
    }


    $trangThaiTT = ($method === "COD") ? 0 : 1;
    $maPTTT_ID = ($method === "BANK") ? 2 : ($method === "WALLET" ? 3 : 1);
    $fullAddress = "Tên: " . ($data['HoTen'] ?? '') . " | SĐT: " . ($data['SoDienThoai'] ?? '') . " | ĐC: " . ($data['DiaChi'] ?? '');

  
    $sqlDonHang = "INSERT INTO DonHang (MaKhachHang, NgayDatHang, NgayDuKien, TongTien, TrangThai, TrangThaiThanhToan, MaPTTT, DiaChiGiaoHang) 
                   VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), ?, 0, ?, ?, ?)";
    $stmtDonHang = $conn->prepare($sqlDonHang);
    $stmtDonHang->execute([$maKhachHang, $totalAmount, $trangThaiTT, $maPTTT_ID, $fullAddress]);
    $maDonHang = $conn->lastInsertId();


    $stmtInsertCT = $conn->prepare("INSERT INTO ChiTietDonHang (MaDonHang, MaChiTietSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)");
    $stmtMinusStock = $conn->prepare("UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaChiTietSP = ?");
    
    foreach ($items as $item) {
        $id = $item['MaChiTietSP'];
        $qty = $item['SoLuong'];
        $stmtInsertCT->execute([$maDonHang, $id, $qty, $priceMap[$id]]);
        $stmtMinusStock->execute([$qty, $id]);
    }

  
    $conn->prepare("DELETE FROM GioHang WHERE MaKhachHang = ?")->execute([$maKhachHang]);

    $conn->commit();
    echo json_encode(["success" => true, "MaDonHang" => (int)$maDonHang]);

} catch (Exception $e) {
    if ($conn->inTransaction()) $conn->rollBack();
    echo json_encode(["success" => false, "message" => "Lỗi: " . $e->getMessage()]);
}
?>