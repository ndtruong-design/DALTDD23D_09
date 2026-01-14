<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");

require_once "../config/db_connect.php"; 

// 1. Kiểm tra đầu vào
if (!isset($_GET['MaKhachHang'])) {
    echo json_encode(["success" => false, "message" => "Thiếu MaKhachHang (user_id)"]);
    exit();
}

$user_id = intval($_GET['MaKhachHang']);

try {
    // 2. Câu truy vấn SQL 
    // Lưu ý: Nếu dùng SQL Server, thay 'LIMIT 1' bằng 'TOP 1' bên trong Subquery
    $sql = "
        SELECT 
            dh.MaDonHang, 
            dh.NgayDatHang, 
            dh.NgayDuKien,
            dh.TrangThai AS TrangThaiDonHang, 
            dh.TrangThaiThanhToan,
            dh.TongTien, 
            dh.DiaChiGiaoHang,
            pt.TenPhuongThuc,
            sp.TenSanPham,
            ctsp.BoNho,
            ctsp.RAM,
            ms.TenMau,
            ctdh.SoLuong,
            ctdh.DonGia,
            (SELECT DuongLinkAnh FROM HinhAnh ha 
             WHERE ha.MaMau = ctsp.MaMau 
             AND ha.LaAnhDaiDien = 1 
             LIMIT 1) AS HinhAnhSanPham
        FROM DonHang dh
        JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang
        JOIN ChiTietSanPham ctsp ON ctdh.MaChiTietSP = ctsp.MaChiTietSP
        JOIN SanPham sp ON ctsp.MaSanPham = sp.MaSanPham
        LEFT JOIN MauSac ms ON ctsp.MaMau = ms.MaMau
        LEFT JOIN PhuongThucThanhToan pt ON dh.MaPTTT = pt.MaPTTT
        WHERE dh.MaKhachHang = ?
        ORDER BY dh.NgayDatHang DESC
    ";

    // 3. Thực thi bằng PDO
    $stmt = $conn->prepare($sql);
    $stmt->execute([$user_id]);
    
    // Lấy toàn bộ dữ liệu dưới dạng mảng kết hợp
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $orders = [];

    // 4. Xử lý dữ liệu
    foreach ($rows as $row) {
        $maDonHang = $row['MaDonHang'];

        if (!isset($orders[$maDonHang])) {
            $orders[$maDonHang] = [
                'order_id' => $row['MaDonHang'],
                'date_ordered' => $row['NgayDatHang'],
                'date_expected' => $row['NgayDuKien'],
                'total_price' => (float)$row['TongTien'],
                'total_price_formatted' => number_format($row['TongTien'], 0, ',', '.') . ' đ',
                'status_code' => $row['TrangThaiDonHang'],
                'status_text' => getStatusText($row['TrangThaiDonHang']),
                'payment_status_text' => getPaymentStatusText($row['TrangThaiThanhToan']),
                'payment_method' => $row['TenPhuongThuc'],
                'address' => $row['DiaChiGiaoHang'],
                'items' => []
            ];
        }

        $orders[$maDonHang]['items'][] = [
            'product_name' => $row['TenSanPham'],
            'variant_info' => trim($row['BoNho'] . " " . $row['RAM']),
            'color' => $row['TenMau'],
            'quantity' => (int)$row['SoLuong'],
            'price' => (float)$row['DonGia'],
            'price_formatted' => number_format($row['DonGia'], 0, ',', '.') . ' đ',
            'image' => $row['HinhAnhSanPham'] ?? 'https://via.placeholder.com/150'
        ];
    }

    echo json_encode([
        "success" => true,
        "data" => array_values($orders)
    ]);

} catch (PDOException $e) {
    // Trả về lỗi nếu có vấn đề trong quá trình truy vấn
    echo json_encode([
        "success" => false,
        "message" => "Lỗi truy vấn: " . $e->getMessage()
    ]);
}

// Helper functions
function getStatusText($status_code) {
    $status_map = [0 => "Chờ duyệt", 1 => "Đang giao hàng", 2 => "Giao thành công", 3 => "Đã hủy"];
    return $status_map[$status_code] ?? "Không xác định";
}

function getPaymentStatusText($status_code) {
    $payment_map = [0 => "Chưa thanh toán", 1 => "Đã thanh toán", 2 => "Đã hoàn tiền"];
    return $payment_map[$status_code] ?? "Khác";
}
?>