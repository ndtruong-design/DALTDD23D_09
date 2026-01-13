<?php
header("Content-Type: application/json; charset=UTF-8");

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

$sqladmin = "SELECT * FROM khachhang WHERE TenDangNhap=:TenDangNhap";
$stmtadmin = $conn->prepare($sqladmin);
$stmtadmin->execute([":TenDangNhap" => $username]);
$useradmin = $stmtadmin->fetch(PDO::FETCH_ASSOC);
if (!$useradmin ||
    
        !password_verify($password, $useradmin['MatKhau']) &&
        $password !== $useradmin['MatKhau']
    ) {
    http_response_code(401);
    echo json_encode([
        "success" => false,
        "message" => "Sai tên đăng nhập hoặc mật khẩu"
    ]);
    exit;
}

$sql = "SELECT * FROM khachhang WHERE TenDangNhap=:TenDangNhap";
$stmt = $conn->prepare($sql);
$stmt->execute([":TenDangNhap" => $username]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if (
    !$user ||
    (
        !password_verify($password, $user['MatKhau']) &&
        $password !== $user['MatKhau']
    )
) {
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
        "TenDangNhap" => $user['TenDangNhap'],
        "MaKhachHang" => $user['MaKhachHang'],
    ]
];

$token = JWT::encode($payload, $JWT_SECRET, 'HS256');

echo json_encode([
    "success" => true,
    "token" => $token,
    "MaKhachHang" => (int)$user['MaKhachHang'], 
    "message" => "Đăng nhập thành công"
]);
?>
