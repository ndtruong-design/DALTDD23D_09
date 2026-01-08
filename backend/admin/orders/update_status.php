<?php
// API cập nhật trạng thái đơn hàng cho admin
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}
require_once '../../auth/middleware.php';
require_once '../../config/db_connect.php';
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(array(
        'success' => false,
        'message' => 'Chỉ chấp nhận phương thức POST'
    ), JSON_UNESCAPED_UNICODE);
    exit();
}

try {
   
    $data = json_decode(file_get_contents("php://input"), true); 
    if (!isset($data['MaDonHang']) || !isset($data['TrangThai'])) {
        http_response_code(400);
        echo json_encode(array(
            'success' => false,
            'message' => 'Thiếu thông tin MaDonHang hoặc TrangThai'
        ), JSON_UNESCAPED_UNICODE);
        exit();
    }
    $maDonHang = intval($data['MaDonHang']);
    $trangThai = intval($data['TrangThai']);
    if (!in_array($trangThai, [0, 1, 2, 3])) {
        http_response_code(400);
        echo json_encode(array(
            'success' => false,
            'message' => 'Trạng thái không hợp lệ. Chỉ chấp nhận: 0 (Chờ duyệt), 1 (Đang giao), 2 (Đã giao), 3 (Đã hủy)'
        ), JSON_UNESCAPED_UNICODE);
        exit();
    }
    $checkSql = "SELECT MaDonHang FROM DonHang WHERE MaDonHang = :maDonHang";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->bindParam(':maDonHang', $maDonHang, PDO::PARAM_INT);
    $checkStmt->execute();
    
    if ($checkStmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode(array(
            'success' => false,
            'message' => 'Không tìm thấy đơn hàng'
        ), JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    $updateSql = "UPDATE DonHang 
                  SET TrangThai = :trangThai, NgayXuLy = NOW() 
                  WHERE MaDonHang = :maDonHang";
    
    $stmt = $conn->prepare($updateSql);
    $stmt->bindParam(':trangThai', $trangThai, PDO::PARAM_INT);
    $stmt->bindParam(':maDonHang', $maDonHang, PDO::PARAM_INT);
    
    if ($stmt->execute()) {
        $statusText = '';
        switch ($trangThai) {
            case 0: $statusText = 'Chờ duyệt'; break;
            case 1: $statusText = 'Đang giao'; break;
            case 2: $statusText = 'Đã giao'; break;
            case 3: $statusText = 'Đã hủy'; break;
        }
        
        echo json_encode(array(
            'success' => true,
            'message' => "Cập nhật trạng thái đơn hàng thành công: {$statusText}"
        ), JSON_UNESCAPED_UNICODE);
    } else {
        throw new Exception('Không thể cập nhật trạng thái đơn hàng');
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'success' => false,
        'message' => 'Lỗi server: ' . $e->getMessage()
    ), JSON_UNESCAPED_UNICODE);
}
?>
