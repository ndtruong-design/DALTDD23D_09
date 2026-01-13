<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php"; 

$input = file_get_contents("php://input");
$data = json_decode($input, true);

$maKhachHang = intval($data['MaKhachHang'] ?? 0);
$items = $data['items'] ?? [];

if ($maKhachHang <= 0 || empty($items)) {
    echo json_encode(["success" => false, "message" => "Vui lòng chọn sản phẩm cần thanh toán"]);
    exit;
}

try {
    $conn->beginTransaction();


    $maCT_list = array_column($items, 'MaChiTietSP');
    $placeholders = implode(',', array_fill(0, count($maCT_list), '?'));

  
    $totalAmount = 0;
    $sqlCheck = "SELECT MaChiTietSP, Gia, SoLuongTon FROM ChiTietSanPham WHERE MaChiTietSP IN ($placeholders)";
    $stmtCheck = $conn->prepare($sqlCheck);
    $stmtCheck->execute($maCT_list);
    $dbProducts = $stmtCheck->fetchAll(PDO::FETCH_ASSOC);


    $priceMap = [];
    foreach ($dbProducts as $p) {
        $priceMap[$p['MaChiTietSP']] = $p['Gia'];

    }

    foreach ($items as $item) {
        $id = intval($item['MaChiTietSP']);
        $qty = intval($item['SoLuong']);
        if (isset($priceMap[$id])) {
            $totalAmount += $priceMap[$id] * $qty;
        }
    }


    $sqlDonHang = "INSERT INTO DonHang (MaKhachHang, NgayDatHang, TongTien, TrangThai, TrangThaiThanhToan) 
                   VALUES (?, CURRENT_TIMESTAMP, ?, 0, 0)";
    $stmtDonHang = $conn->prepare($sqlDonHang);
    $stmtDonHang->execute([$maKhachHang, $totalAmount]);
    $maDonHang = $conn->lastInsertId();


    $sqlChiTiet = "INSERT INTO ChiTietDonHang (MaDonHang, MaChiTietSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
    $stmtChiTiet = $conn->prepare($sqlChiTiet);

    foreach ($items as $item) {
        $id = intval($item['MaChiTietSP']);
        $qty = intval($item['SoLuong']);
        $stmtChiTiet->execute([$maDonHang, $id, $qty, $priceMap[$id]]);
    }

    $conn->commit();
    echo json_encode([
        "success" => true,
        "message" => "Đơn hàng đã được khởi tạo.",
        "MaDonHang" => $maDonHang,
        "TongTien" => $totalAmount
    ]);

} catch (Exception $e) {
    if ($conn->inTransaction()) $conn->rollBack();
    echo json_encode(["success" => false, "message" => "Lỗi: " . $e->getMessage()]);
}
?>