<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);

$maKhachHang = $data['MaKhachHang'] ?? 0;
$maChiTietSP = $data['MaChiTietSP'] ?? 0;

if ($maKhachHang > 0 && $maChiTietSP > 0) {
    $sql = "DELETE FROM giohang WHERE MaKhachHang = ? AND MaChiTietSP = ?";
    $stmt = $conn->prepare($sql);
    
    if ($stmt->execute([$maKhachHang, $maChiTietSP])) {
        echo json_encode(["success" => true, "message" => "Đã xóa khỏi giỏ hàng"]);
    } else {
        echo json_encode(["success" => false, "message" => "Không thể xóa"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Dữ liệu không hợp lệ"]);
}
?>