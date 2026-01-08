<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);

$phone = $data['SoDienThoai'] ?? '';
$newPassword = $data['MatKhau'] ?? '';

if (empty($phone)) {
    echo json_encode([
        "success" => false,
        "message" => "Vui lòng nhập số điện thoại"
    ]);
    exit;
}

$sql = "SELECT MaKhachHang FROM khachhang WHERE SoDienThoai = :SoDienThoai";
$stmt = $conn->prepare($sql);
$stmt->execute([":SoDienThoai" => $phone]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$user) {
    echo json_encode([
        "success" => false,
        "message" => "Không tìm thấy số điện thoại này"
    ]);
    exit;
}

if (empty($newPassword)) {
    echo json_encode([
        "success" => true,
        "message" => "Số điện thoại hợp lệ"
    ]);
    exit;
}

$hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);

$updateSql = "UPDATE khachhang 
              SET MatKhau = :MatKhau 
              WHERE SoDienThoai = :SoDienThoai";
$updateStmt = $conn->prepare($updateSql);
$updateStmt->execute([
    ":MatKhau" => $hashedPassword,
    ":SoDienThoai" => $phone
]);

if ($updateStmt->rowCount() > 0) {
    echo json_encode([
        "success" => true,
        "message" => "Đổi mật khẩu thành công"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Mật khẩu mới trùng mật khẩu cũ"
    ]);
}
