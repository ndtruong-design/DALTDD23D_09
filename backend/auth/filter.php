<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php";

$min  = isset($_GET['min']) ? intval($_GET['min']) : 0;
$max  = isset($_GET['max']) ? intval($_GET['max']) : 0;
$hang = $_GET['hang'] ?? '';

$sql = "
SELECT
    sp.MaSanPham,
    sp.TenSanPham,
    sp.Hang,
    ct.MaMau,
    ct.BoNho,
    ct.Gia,
    ha.DuongLinkAnh
FROM chitietsanpham ct
JOIN sanpham sp 
    ON sp.MaSanPham = ct.MaSanPham
LEFT JOIN hinhanh ha 
    ON ha.MaSanPham = sp.MaSanPham
    AND ha.LaAnhDaiDien = 1
WHERE 1=1
";

$params = [];

if ($min > 0) {
    $sql .= " AND ct.Gia >= :min";
    $params[':min'] = $min;
}
if ($max > 0) {
    $sql .= " AND ct.Gia <= :max";
    $params[':max'] = $max;
}
if (!empty($hang)) {
    $sql .= " AND sp.Hang = :hang";
    $params[':hang'] = $hang;
}

$sql .= "
ORDER BY sp.MaSanPham, ct.MaMau, ct.BoNho
";

$stmt = $conn->prepare($sql);
$stmt->execute($params);

echo json_encode([
    "success" => true,
    "data" => $stmt->fetchAll(PDO::FETCH_ASSOC)
]);
