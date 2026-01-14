<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once '../../config/db_connect.php';

try {
   
    if (!isset($_GET['MaDonHang'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Thiếu mã đơn hàng'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    $orderId = intval($_GET['MaDonHang']);

    $sql = "SELECT 
                dh.MaDonHang, 
                dh.NgayDatHang, 
                dh.TongTien, 
                dh.TrangThai,
                dh.DiaChiGiaoHang,
                kh.HoTen, 
                kh.SoDienThoai
            FROM DonHang dh
            JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang
            WHERE dh.MaDonHang = ?";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$orderId]);
    $order = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$order) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Không tìm thấy đơn hàng'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $sqlItems = "SELECT 
                    sp.TenSanPham,
                    ctsp.BoNho,
                    ms.TenMau,
                    ctdh.SoLuong,
                    ctdh.DonGia,
                    (ctdh.SoLuong * ctdh.DonGia) AS ThanhTien,
                    ha.DuongLinkAnh
                FROM ChiTietDonHang ctdh
                JOIN ChiTietSanPham ctsp ON ctdh.MaChiTietSP = ctsp.MaChiTietSP
                JOIN SanPham sp ON ctsp.MaSanPham = sp.MaSanPham
                JOIN MauSac ms ON ctsp.MaMau = ms.MaMau
                LEFT JOIN HinhAnh ha ON ctsp.MaChiTietSP = ha.MaChiTietSP AND ha.LaAnhDaiDien = TRUE
                WHERE ctdh.MaDonHang = ?";
    $stmtItems = $conn->prepare($sqlItems);
    $stmtItems->execute([$orderId]);
    $items = $stmtItems->fetchAll(PDO::FETCH_ASSOC);
    $orderItems = [];
    foreach ($items as $item) {
        $orderItems[] = [
            'TenSanPham' => $item['TenSanPham'],
            'SoLuong' => intval($item['SoLuong']),
            'DonGia' => floatval($item['DonGia']),
            'DuongLinkAnh' => $item['DuongLinkAnh'],
            'BoNho' => $item['BoNho'],
            'TenMau' => $item['TenMau']
        ];
    }

    $orderDetail = [
        'MaDonHang' => intval($order['MaDonHang']),
        'NgayDat' => $order['NgayDatHang'],
        'TongTien' => floatval($order['TongTien']),
        'TrangThai' => intval($order['TrangThai']),
        'DiaChiGiaoHang' => $order['DiaChiGiaoHang'] ?? '',
        'KhachHang' => [
            'HoTen' => $order['HoTen'],
            'SoDienThoai' => $order['SoDienThoai']
        ]
    ];

    echo json_encode([
        'success' => true,
        'order' => $orderDetail,
        'items' => $orderItems,
        'message' => ''
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Lỗi server: ' . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}
?>