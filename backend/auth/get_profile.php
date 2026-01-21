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

require_once '../config/db_connect.php';

try {
    if (!isset($_GET['MaKhachHang'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Thiếu MaKhachHang'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $maKhachHang = intval($_GET['MaKhachHang']);

    $sql = "SELECT 
                MaKhachHang,
                TenDangNhap,
                HoTen,
                SoDienThoai,
                Email,
                NgaySinh,
                DiaChi,
                AnhDaiDien,
                TrangThai
            FROM KhachHang
            WHERE MaKhachHang = ?";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute([$maKhachHang]);
    
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Thông tin không hợp lệ'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    echo json_encode([
        'success' => true,
        'user' => [
            'MaKhachHang' => intval($user['MaKhachHang']),
            'TenDangNhap' => $user['TenDangNhap'],
            'HoTen' => $user['HoTen'] ?? '',
            'SoDienThoai' => $user['SoDienThoai'] ?? '',
            'Email' => $user['Email'] ?? '',
            'NgaySinh' => $user['NgaySinh'] ?? '',
            'DiaChi' => $user['DiaChi'] ?? '',
            'AnhDaiDien' => $user['AnhDaiDien'] ?? '',
            'TrangThai' => intval($user['TrangThai'])
        ],
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