<?php
require_once "../config/db_connect.php";
require_once "../vendor/autoload.php";
require_once "../config/jwt.php";

use Firebase\JWT\JWT;

$data = json_decode(file_get_contents("php://input"), true);

$username    = $data['TenDangNhap'] ?? '';
$password = $data['MatKhau'] ?? '';

if (!$username || !$password) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Thiếu dữ liệu"
    ]);
    exit;
}

$sql = "SELECT * FROM KhachHang WHERE TenDangNhap=:username";
$stmt = $conn->prepare($sql);
$stmt->execute([":username" => $username]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$user || !password_verify($password, $user['MatKhau'])) {
    http_response_code(401);
   
    echo json_encode([
        "success" => false,
        "message" => "Sai tên đăng nhập hoặc mật khẩu"
    ]);
    exit;
}

$payload = [
    "iss" => $JWT_ISSUER,
    "iat" => time(),
    "exp" => time()+$JWT_EXPIRE,
    "data" => [
        "MaKhachHang" => $user['MaKhachHang'],
        "TenDangNhap" => $user['TenDangNhap'],
       
    ]
];

$token = JWT::encode($payload, $JWT_SECRET, 'HS256');

echo json_encode([
    "success" => true,
    "token" => $token,
    "message" => "Đăng nhập thành công"
]);?>