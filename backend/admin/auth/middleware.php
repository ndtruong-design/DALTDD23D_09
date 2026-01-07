<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Authorization, Content-Type");

require_once "../../vendor/autoload.php";
require_once "../../config/jwt.php";

use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$headers = getallheaders();
$auth = $headers['Authorization'] ?? '';

if (!$auth) {
    http_response_code(401);
    echo json_encode(["error" => "Thiếu token"]);
    exit;
}

$token = str_replace("Bearer ", "", $auth);

try {
    $decoded = JWT::decode($token, new Key($JWT_SECRET, 'HS256'));
    $admin = $decoded->data;
    
    if (!isset($admin->role) || $admin->role !== 'admin') {
        http_response_code(403);
        echo json_encode(["error" => "Bạn không có quyền admin"]);
        exit;
    }
} catch (Exception $e) {
    http_response_code(401);
    echo json_encode(["error" => "Token không hợp lệ"]);
    exit;
}
?>
