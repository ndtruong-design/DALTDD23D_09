<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once '../config/db_connect.php';

try {
    $data = json_decode(file_get_contents("php://input"), true);
    
    if (!isset($data['MaKhachHang'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Thiếu MaKhachHang'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    $maKhachHang = intval($data['MaKhachHang']);
    
    $checkSql = "SELECT MaKhachHang FROM KhachHang WHERE MaKhachHang = ?";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([$maKhachHang]);
    
    if ($checkStmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Không tìm thấy khách hàng'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    if (isset($data['SoDienThoai']) && !empty($data['SoDienThoai'])) {
        $checkPhone = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ? AND MaKhachHang != ?";
        $checkStmt = $conn->prepare($checkPhone);
        $checkStmt->execute([$data['SoDienThoai'], $maKhachHang]);
        
        if ($checkStmt->rowCount() > 0) {
            echo json_encode([
                'success' => false,
                'message' => 'Số điện thoại đã được sử dụng'
            ], JSON_UNESCAPED_UNICODE);
            exit();
        }
    }
    
    $updateFields = [];
    $bindValues = [];
    
    if (isset($data['HoTen'])) {
        $updateFields[] = "HoTen = ?";
        $bindValues[] = $data['HoTen'];
    }
    
    if (isset($data['SoDienThoai'])) {
        $updateFields[] = "SoDienThoai = ?";
        $bindValues[] = $data['SoDienThoai'];
    }
    
    if (isset($data['Email'])) {
        $updateFields[] = "Email = ?";
        $bindValues[] = $data['Email'];
    }
    
    if (isset($data['NgaySinh'])) {
        $updateFields[] = "NgaySinh = ?";
        $bindValues[] = $data['NgaySinh'];
    }
    
    if (isset($data['DiaChi'])) {
        $updateFields[] = "DiaChi = ?";
        $bindValues[] = $data['DiaChi'];
    }
    
    if (isset($data['AnhDaiDien'])) {
        $updateFields[] = "AnhDaiDien = ?";
        $bindValues[] = $data['AnhDaiDien'];
    }
    
    if (empty($updateFields)) {
        echo json_encode([
            'success' => false,
            'message' => 'Không có thông tin để cập nhật'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    $bindValues[] = $maKhachHang;
    $sql = "UPDATE KhachHang SET " . implode(', ', $updateFields) . " WHERE MaKhachHang = ?";
    $stmt = $conn->prepare($sql);
    
    if ($stmt->execute($bindValues)) {
       
        $getSql = "SELECT MaKhachHang, TenDangNhap, HoTen, SoDienThoai, Email, NgaySinh, DiaChi, AnhDaiDien 
                   FROM KhachHang WHERE MaKhachHang = ?";
        $getStmt = $conn->prepare($getSql);
        $getStmt->execute([$maKhachHang]);
        $user = $getStmt->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'success' => true,
            'message' => 'Cập nhật thông tin thành công',
            'user' => [
                'MaKhachHang' => intval($user['MaKhachHang']),
                'TenDangNhap' => $user['TenDangNhap'],
                'HoTen' => $user['HoTen'],
                'SoDienThoai' => $user['SoDienThoai'],
                'Email' => $user['Email'] ?? '',
                'NgaySinh' => $user['NgaySinh'] ?? '',
                'DiaChi' => $user['DiaChi'] ?? '',
                'AnhDaiDien' => $user['AnhDaiDien'] ?? ''
            ]
        ], JSON_UNESCAPED_UNICODE);
    } else {
        throw new Exception('Không thể cập nhật thông tin');
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Lỗi server: ' . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}
?>