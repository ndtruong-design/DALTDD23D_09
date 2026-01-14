<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php";

$input = file_get_contents("php://input");
$data = json_decode($input, true);

$maKhachHang = intval($data['MaKhachHang'] ?? 0);
$items = $data['items'] ?? [];

if ($maKhachHang <= 0 || empty($items)) {
    echo json_encode([
        "success" => false,
        "message" => "Vui lòng chọn sản phẩm cần thanh toán"
    ]);
    exit;
}

try {
    $maCT_list = array_column($items, 'MaChiTietSP');
    $placeholders = implode(',', array_fill(0, count($maCT_list), '?'));

    $sqlCheck = "
        SELECT MaChiTietSP, Gia, SoLuongTon
        FROM ChiTietSanPham
        WHERE MaChiTietSP IN ($placeholders)
    ";
    $stmtCheck = $conn->prepare($sqlCheck);
    $stmtCheck->execute($maCT_list);
    $dbProducts = $stmtCheck->fetchAll(PDO::FETCH_ASSOC);

    if (count($dbProducts) !== count($items)) {
        echo json_encode([
            "success" => false,
            "message" => "Có sản phẩm không tồn tại"
        ]);
        exit;
    }

    $priceMap = [];
    $stockMap = [];
    foreach ($dbProducts as $p) {
        $priceMap[$p['MaChiTietSP']] = $p['Gia'];
        $stockMap[$p['MaChiTietSP']] = $p['SoLuongTon'];
    }

    $totalAmount = 0;
    foreach ($items as $item) {
        $id  = intval($item['MaChiTietSP']);
        $qty = intval($item['SoLuong']);

        if ($qty > ($stockMap[$id] ?? 0)) {
            echo json_encode([
                "success" => false,
                "message" => "Sản phẩm ID $id không đủ tồn kho"
            ]);
            exit;
        }

        $totalAmount += $priceMap[$id] * $qty;
    }

    echo json_encode([
        "success" => true,
        "TongTien" => $totalAmount
    ]);

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => "Lỗi: " . $e->getMessage()
    ]);
}
