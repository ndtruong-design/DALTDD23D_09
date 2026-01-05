<?php
header("Content-Type: application/json");

require_once "../vendor/autoload.php";
require_once "../config/jwt.php";

use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$headers = getallheaders();
$auth = $headers['Authorization'] ?? '';

if (!$auth) {
    echo json_encode(["error" => "Thiếu token"]);
    exit;
}

$token = str_replace("Bearer ", "", $auth);

try {
    $decoded = JWT::decode($token, new Key($JWT_SECRET, 'HS256'));
    $user = $decoded->data; 
} catch (Exception $e) {
    echo json_encode(["error" => "Token không hợp lệ"]);
    exit;
}
?>
