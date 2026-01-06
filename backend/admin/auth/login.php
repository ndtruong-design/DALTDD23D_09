<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Content-Type: application/json; charset=UTF-8");

$data = json_decode(file_get_contents("php://input"), true);
$username = $data['TenDangNhap'] ?? '';
$password = $data['MatKhau'] ?? '';

if (empty($username) || empty($password)) {
    echo json_encode(["error" => "Vui lòng nhập đầy đủ thông tin"]);
    exit;
}

require_once "../../config/db_connect.php";
require_once "../../vendor/autoload.php";
require_once "../../config/jwt.php";

use Firebase\JWT\JWT;

$sql = "SELECT * FROM Admin WHERE TenDangNhap = :username";
$stmt = $conn->prepare($sql);
$stmt->execute([':username' => $username]);
$admin = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$admin) {
    echo json_encode(["error" => "Tên đăng nhập hoặc mật khẩu không đúng"]);
    exit;
}

$isPasswordValid = password_verify($password, $admin['MatKhau']);

if (!$isPasswordValid && $password === $admin['MatKhau']) {
    $isPasswordValid = true;
}

if (!$isPasswordValid) {
    echo json_encode(["error" => "Tên đăng nhập hoặc mật khẩu không đúng"]);
    exit;
}

$currentTime = time();
$expireTime = $currentTime + (24 * 60 * 60);

$payload = [
    "iss" => $JWT_ISSUER,
    "iat" => $currentTime,
    "exp" => $expireTime,
    "data" => [
        "MaAdmin" => $admin['MaAdmin'],
        "TenDangNhap" => $admin['TenDangNhap'],
        "role" => "admin"
    ]
];

$token = JWT::encode($payload, $JWT_SECRET, 'HS256');

echo json_encode([
    "success" => true,
    "token" => $token,
    "admin" => [
        "MaAdmin" => $admin['MaAdmin'],
        "TenDangNhap" => $admin['TenDangNhap']
    ]
]);
?>
