<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php"; 

$userId = $_GET['user_id'] ?? 0;

try {
    $sql = "SELECT HoTen, SoDienThoai, DiaChi FROM KhachHang WHERE MaKhachHang = ?";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$userId]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user) {
        echo json_encode([
            "success" => true,
            "data" => $user
        ]);
    } else {
        echo json_encode(["success" => false, "message" => "Không tìm thấy người dùng"]);
    }
} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>