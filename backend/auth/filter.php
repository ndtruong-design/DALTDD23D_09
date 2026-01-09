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
    (
        SELECT ha.DuongLinkAnh
        FROM hinhanh ha
        WHERE ha.MaSanPham = sp.MaSanPham
        LIMIT 1
    ) AS DuongLinkAnh
FROM chitietsanpham ct
JOIN sanpham sp ON sp.MaSanPham = ct.MaSanPham
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
    $sql .= " AND LOWER(TRIM(sp.Hang)) LIKE LOWER(:hang)";
    $params[':hang'] = "%$hang%";
}

$sql .= "
GROUP BY ct.MaSanPham, ct.MaMau, ct.BoNho,ct.MaMau
ORDER BY sp.MaSanPham
";



$stmt = $conn->prepare($sql);
$stmt->execute($params);
$data = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "data" => $data
]);

