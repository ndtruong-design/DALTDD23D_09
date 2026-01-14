<?php
header("Content-Type: application/json; charset=UTF-8");

require_once "../config/db_connect.php";
require_once "../vendor/autoload.php";
require_once "../config/jwt.php";

use Firebase\JWT\JWT;

$data = json_decode(file_get_contents("php://input"), true);

$username = $data['TenDangNhap'] ?? '';
$password = $data['MatKhau'] ?? '';

if (!$username || !$password) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Thiếu dữ liệu"
    ]);
    exit;
}


$sqlAdmin = "SELECT * FROM admin WHERE TenDangNhap=:TenDangNhap LIMIT 1";
$stmtAdmin = $conn->prepare($sqlAdmin);
$stmtAdmin->execute([":TenDangNhap" => $username]);
$userAdmin = $stmtAdmin->fetch(PDO::FETCH_ASSOC);

if ($userAdmin) {
    if (password_verify($password, $userAdmin['MatKhau']) || $password === $userAdmin['MatKhau']) {
        $accountType = "admin";
        $userId = (int)$userAdmin['MaAdmin']; 
        $userData = $userAdmin;
    } else {
        http_response_code(401);
        echo json_encode([
            "success" => false,
            "message" => "Sai tên đăng nhập hoặc mật khẩu"
        ]);
        exit;
    }
} else {
    
    $sqlUser = "SELECT * FROM khachhang WHERE TenDangNhap=:TenDangNhap LIMIT 1";
    $stmtUser = $conn->prepare($sqlUser);
    $stmtUser->execute([":TenDangNhap" => $username]);
    $user = $stmtUser->fetch(PDO::FETCH_ASSOC);

    if (!$user || (!password_verify($password, $user['MatKhau']) && $password !== $user['MatKhau'])) {
        http_response_code(401);
        echo json_encode([
            "success" => false,
            "message" => "Sai tên đăng nhập hoặc mật khẩu"
        ]);
        exit;
    }

    $accountType = "user";
    $userId = (int)$user['MaKhachHang'];
    $userData = $user;
}


$payload = [
    "iss" => $JWT_ISSUER,
    "iat" => time(),
    "exp" => time() + $JWT_EXPIRE,
    "data" => [
        "TenDangNhap" => $userData['TenDangNhap'],
        "MaKhachHang" => $userId,
        "accountType" => $accountType
    ]
];

$token = JWT::encode($payload, $JWT_SECRET, 'HS256');


echo json_encode([
    "success" => true,
    "token" => $token,
    "accountType" => $accountType,
    "MaKhachHang" => $userId,   
    "message" => "Đăng nhập thành công"
]);
?>