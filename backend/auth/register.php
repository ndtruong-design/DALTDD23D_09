<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);

$username = $data['TenDangNhap'] ?? '';
$phone    = $data['SoDienThoai'] ?? '';
$password = $data['MatKhau'] ?? '';

if (empty($username) || empty($phone) || empty($password)) {
    echo json_encode([
        "success" => false,
        "message" => "Vui lòng nhập đầy đủ thông tin"
    ]);
    exit;
}

$sql = "SELECT MaKhachHang FROM khachhang 
        WHERE SoDienThoai = :SoDienThoai 
           OR TenDangNhap = :TenDangNhap";
$stmt = $conn->prepare($sql);
$stmt->execute([
    ":SoDienThoai" => $phone,
    ":TenDangNhap" => $username
]);

if ($stmt->rowCount() > 0) {
    echo json_encode([
        "success" => false,
        "message" => "Tài khoản đã tồn tại"
    ]);
    exit;
}

$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

$sql = "INSERT INTO khachhang (TenDangNhap, SoDienThoai, MatKhau)
        VALUES (:TenDangNhap, :SoDienThoai, :MatKhau)";
$stmt = $conn->prepare($sql);
$stmt->execute([
    ":TenDangNhap" => $username,
    ":SoDienThoai" => $phone,
    ":MatKhau"     => $hashedPassword
]);

echo json_encode([
    "success" => true,
    "message" => "Đăng ký thành công"
]);
