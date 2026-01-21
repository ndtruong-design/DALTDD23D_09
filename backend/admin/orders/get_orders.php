<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
        'message' => 'Chỉ hỗ trợ phương thức GET'
    ], JSON_UNESCAPED_UNICODE);
    exit();
}

require_once '../../config/db_connect.php';

try {
    $status = isset($_GET['status']) ? intval($_GET['status']) : null;

    $sql = "SELECT 
                dh.MaDonHang, 
                dh.NgayDatHang, 
                dh.TongTien, 
                dh.TrangThai, 
                kh.HoTen, 
                kh.SoDienThoai
            FROM DonHang dh
            JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang";

    if ($status !== null) {
        $sql .= " WHERE dh.TrangThai = ?";
        $stmt = $conn->prepare($sql);
        $stmt->execute([$status]);
    } else {
        $stmt = $conn->prepare($sql);
        $stmt->execute();
    }

    $orders = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $statusText = match (intval($row['TrangThai'])) {
            0 => 'Chờ duyệt',
            1 => 'Đang giao',
            2 => 'Đã giao',
            3 => 'Đã hủy',
            default => 'Không xác định'
        };

        $orders[] = [
            'MaDonHang' => intval($row['MaDonHang']),
            'NgayDatHang' => $row['NgayDatHang'],
            'TongTien' => floatval($row['TongTien']),
            'TrangThai' => intval($row['TrangThai']),
            'TrangThaiText' => $statusText,
            'HoTen' => $row['HoTen'],
            'SoDienThoai' => $row['SoDienThoai']
        ];
    }

    echo json_encode([
        'success' => true,
        'orders' => $orders,
        'total' => count($orders),
        'message' => ''
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Lỗi hệ thống'
    ], JSON_UNESCAPED_UNICODE);
}
?>