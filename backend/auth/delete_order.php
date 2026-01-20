<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once "../config/db_connect.php"; 


$data = json_decode(file_get_contents("php://input"), true);


if (is_null($data)) {
    $data = $_POST;
}


if (!isset($data['MaDonHang']) || !isset($data['MaKhachHang'])) {
    echo json_encode(["success" => false, "message" => "Thiếu MaDonHang hoặc MaKhachHang"]);
    exit();
}

$order_id = intval($data['MaDonHang']);
$user_id  = intval($data['MaKhachHang']);

try {

    $conn->beginTransaction();


    $checkSql = "SELECT TrangThai FROM DonHang WHERE MaDonHang = ? AND MaKhachHang = ? FOR UPDATE";
    $stmtCheck = $conn->prepare($checkSql);
    $stmtCheck->execute([$order_id, $user_id]);
    $order = $stmtCheck->fetch(PDO::FETCH_ASSOC);

    if (!$order) {
        $conn->rollBack();
        echo json_encode(["success" => false, "message" => "Đơn hàng không tồn tại hoặc không thuộc về khách hàng này."]);
        exit();
    }

   
    if ($order['TrangThai'] != 0) {
        $conn->rollBack();
        $statusText = getStatusText($order['TrangThai']);
        echo json_encode(["success" => false, "message" => "Không thể hủy đơn hàng đang ở trạng thái: " . $statusText]);
        exit();
    }


    $sqlGetItems = "SELECT MaChiTietSP, SoLuong FROM ChiTietDonHang WHERE MaDonHang = ?";
    $stmtGetItems = $conn->prepare($sqlGetItems);
    $stmtGetItems->execute([$order_id]);
    $items = $stmtGetItems->fetchAll(PDO::FETCH_ASSOC);


    $sqlUpdateStock = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaChiTietSP = ?";
    $stmtUpdateStock = $conn->prepare($sqlUpdateStock);

    foreach ($items as $item) {
        $stmtUpdateStock->execute([$item['SoLuong'], $item['MaChiTietSP']]);
    }

    $sqlCancel = "UPDATE DonHang SET TrangThai = 3 WHERE MaDonHang = ?";
    $stmtCancel = $conn->prepare($sqlCancel);
    $stmtCancel->execute([$order_id]);

 
    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "Hủy đơn hàng thành công.",
        "order_id" => $order_id
    ]);

} catch (PDOException $e) {
    if ($conn->inTransaction()) {
        $conn->rollBack();
    }
    echo json_encode([
        "success" => false,
        "message" => "Lỗi hệ thống: " . $e->getMessage()
    ]);
}


function getStatusText($status_code) {
    $status_map = [0 => "Chờ duyệt", 1 => "Đang giao hàng", 2 => "Giao thành công", 3 => "Đã hủy"];
    return $status_map[$status_code] ?? "Không xác định";
}
?>