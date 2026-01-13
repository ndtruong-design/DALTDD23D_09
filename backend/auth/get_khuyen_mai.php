<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php"; 
$code = $_GET['MaKhuyenMai'] ?? '';
$totalOrder = isset($_GET['total']) ? (float)$_GET['total'] : 0; 

if (empty($code)) {
    echo json_encode(["success" => false, "message" => "Vui lòng nhập mã"]);
    exit;
}

try {
    $sql = "SELECT TiLeGiam, GiaToiThieu, SoLuong FROM KhuyenMai WHERE MaKhuyenMai = ? AND TrangThai = 1";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$code]);
    $promo = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($promo) {
        $soLuong = (int)$promo['SoLuong'];
        $giaToiThieu = (float)$promo['GiaToiThieu'];
        $tiLeGiam = (float)$promo['TiLeGiam'];

        if ($soLuong <= 0) {
            echo json_encode(["success" => false, "message" => "Mã đã hết lượt dùng"]);
        } 
        else if ($totalOrder < $giaToiThieu) {
        if (ob_get_length()) ob_clean();    
        echo json_encode([
                "success" => false, 
                "message" => "Đơn hàng tối thiểu từ " . number_format($giaToiThieu) . "đ",
                "GiaToiThieu" => $giaToiThieu
            ]);
        } 
        else {
            echo json_encode([
                "success" => true,
                "message" => "Áp dụng thành công",
                "TiLeGiam" => $tiLeGiam
            ], JSON_NUMERIC_CHECK); 
        }
    } else {
        echo json_encode(["success" => false, "message" => "Mã không tồn tại hoặc hết hạn"]);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Lỗi server: " . $e->getMessage()]);
}
?>