<?php
header("Content-Type: application/json");

require_once "../config/db_connect.php";
require_once "../vendor/autoload.php";
require_once "../config/jwt.php";

use Firebase\JWT\JWT;

$data = json_decode(file_get_contents("php://input"), true);

$username    = $data['TenDangNhap'] ?? '';
$password = $data['MatKhau'] ?? '';

if (!$username || !$password) {
    echo json_encode(["error" => "Thiếu dữ liệu"]);
    exit;
}


$sql = "SELECT * FROM khachhang WHERE TenDangNhap=:TenDangNhap";
$stmt = $conn->prepare($sql);
$stmt->execute([":TenDangNhap" => $username]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$user || !password_verify($password, $user['MatKhau'])) {
    echo json_encode(["error" => "Sai tên đăng nhập hoặc mật khẩu"]);
    exit;
}

$payload = [
    "iss" => $JWT_ISSUER,
    "iat" => time(),
    "exp" => $JWT_EXPIRE,
    "data" => [
        "MaKhachHang" => $user['MaKhachHang'],
        "TenDangNhap" => $user['TenDangNhap'],
        "email" => $user['email']
    ]
];

$token = JWT::encode($payload, $JWT_SECRET, 'HS256');

echo json_encode([
    "success" => true,
    "token" => $token
]);
?>
