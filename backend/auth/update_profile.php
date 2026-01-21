<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
        'message' => 'Chỉ hỗ trợ phương thức POST'
    ], JSON_UNESCAPED_UNICODE);
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
            'message' => 'Thông tin không hợp lệ'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $updateFields = [];
    $bindValues = [];

    if (isset($data['HoTen'])) {
        $updateFields[] = "HoTen = ?";
        $bindValues[] = trim($data['HoTen']);
    }

    if (isset($data['SoDienThoai'])) {
        $sdt = trim($data['SoDienThoai']);
        if (!preg_match('/^0[0-9]{9,10}$/', $sdt)) {
            echo json_encode([
                'success' => false,
                'message' => 'Số điện thoại không hợp lệ (10-11 số, bắt đầu bằng 0)'
            ], JSON_UNESCAPED_UNICODE);
            exit();
        }

        $checkPhone = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ? AND MaKhachHang != ?";
        $checkStmt = $conn->prepare($checkPhone);
        $checkStmt->execute([$sdt, $maKhachHang]);

        if ($checkStmt->rowCount() > 0) {
            echo json_encode([
                'success' => false,
                'message' => 'Số điện thoại đã được sử dụng'
            ], JSON_UNESCAPED_UNICODE);
            exit();
        }

        $updateFields[] = "SoDienThoai = ?";
        $bindValues[] = $sdt;
    }

    if (isset($data['Email'])) {
        $email = trim($data['Email']);
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            echo json_encode([
                'success' => false,
                'message' => 'Email không hợp lệ'
            ], JSON_UNESCAPED_UNICODE);
            exit();
        }

        $checkEmail = "SELECT MaKhachHang FROM KhachHang WHERE Email = ? AND MaKhachHang != ?";
        $checkStmt = $conn->prepare($checkEmail);
        $checkStmt->execute([$email, $maKhachHang]);

        if ($checkStmt->rowCount() > 0) {
            echo json_encode([
                'success' => false,
                'message' => 'Email đã được sử dụng'
            ], JSON_UNESCAPED_UNICODE);
            exit();
        }

        $updateFields[] = "Email = ?";
        $bindValues[] = $email;
    }

    if (isset($data['NgaySinh'])) {
        $ngaySinh = trim($data['NgaySinh']);
        if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $ngaySinh) || strtotime($ngaySinh) > time()) {
            echo json_encode([
                'success' => false,
                'message' => 'Ngày sinh không hợp lệ hoặc trong tương lai'
            ], JSON_UNESCAPED_UNICODE);
            exit();
        }
        $updateFields[] = "NgaySinh = ?";
        $bindValues[] = $ngaySinh;
    }

    if (isset($data['DiaChi'])) {
        $updateFields[] = "DiaChi = ?";
        $bindValues[] = trim($data['DiaChi']);
    }

    if (isset($data['AnhDaiDien'])) {
        $updateFields[] = "AnhDaiDien = ?";
        $bindValues[] = trim($data['AnhDaiDien']);
    }

    if (empty($updateFields)) {
        echo json_encode([
            'success' => false,
            'message' => 'Không có thông tin nào để cập nhật'
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
                'HoTen' => $user['HoTen'] ?? '',
                'SoDienThoai' => $user['SoDienThoai'] ?? '',
                'Email' => $user['Email'] ?? '',
                'NgaySinh' => $user['NgaySinh'] ?? '',
                'DiaChi' => $user['DiaChi'] ?? '',
                'AnhDaiDien' => $user['AnhDaiDien'] ?? ''
            ]
        ], JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Cập nhật thất bại'
        ], JSON_UNESCAPED_UNICODE);
    }

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Lỗi hệ thống'
    ], JSON_UNESCAPED_UNICODE);
}
?>