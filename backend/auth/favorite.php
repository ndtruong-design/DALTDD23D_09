<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once "../config/db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['MaKhachHang']) || !isset($data['MaSanPham'])) {
    echo json_encode(["success" => false, "message" => "Thiếu dữ liệu đầu vào"]);
    exit();
}

$user_id = intval($data['MaKhachHang']);
$product_id = intval($data['MaSanPham']);

try {
    // 1. Kiểm tra xem đã thích chưa
    $checkSql = "SELECT count(*) FROM DanhSachYeuThich WHERE MaKhachHang = ? AND MaSanPham = ?";
    $stmtCheck = $conn->prepare($checkSql);
    $stmtCheck->execute([$user_id, $product_id]);
    $exists = $stmtCheck->fetchColumn() > 0;

    if ($exists) {
        // 2a. Nếu đã tồn tại -> Xóa (Bỏ thích)
        $deleteSql = "DELETE FROM DanhSachYeuThich WHERE MaKhachHang = ? AND MaSanPham = ?";
        $stmtDelete = $conn->prepare($deleteSql);
        $stmtDelete->execute([$user_id, $product_id]);
        
        echo json_encode([
            "success" => true,
            "is_favorite" => false,
            "message" => "Đã xóa khỏi danh sách yêu thích"
        ]);
    } else {
        // 2b. Nếu chưa tồn tại -> Thêm mới
        $insertSql = "INSERT INTO DanhSachYeuThich (MaKhachHang, MaSanPham) VALUES (?, ?)";
        $stmtInsert = $conn->prepare($insertSql);
        $stmtInsert->execute([$user_id, $product_id]);
        
        echo json_encode([
            "success" => true,
            "is_favorite" => true,
            "message" => "Đã thêm vào danh sách yêu thích"
        ]);
    }

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Lỗi: " . $e->getMessage()]);
}
?>