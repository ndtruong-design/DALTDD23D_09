<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php"; 

$data = json_decode(file_get_contents("php://input"), true);

$maKhachHang = $data['MaKhachHang'] ?? 0;
$maChiTietSP = $data['MaChiTietSP'] ?? 0;
$soLuong = $data['SoLuong'] ?? 1;

if ($maKhachHang > 0 && $maChiTietSP > 0) {

    $sql = "UPDATE giohang SET SoLuong = ? WHERE MaKhachHang = ? AND MaChiTietSP = ?";
    $stmt = $conn->prepare($sql);
    
    if ($stmt->execute([$soLuong, $maKhachHang, $maChiTietSP])) {
        echo json_encode(["success" => true, "message" => "Cập nhật thành công"]);
    } else {
        echo json_encode(["success" => false, "message" => "Lỗi database"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Dữ liệu không hợp lệ"]);
}
?>