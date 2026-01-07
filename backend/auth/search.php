<?php
header("Content-Type: application/json; charset=UTF-8");

require_once "../../config/db_connect.php";

$ten  = $_GET['ten']  ?? '';
$min  = $_GET['min']  ?? '';
$max  = $_GET['max']  ?? '';
$hang = $_GET['hang'] ?? '';

$sql = "
SELECT 
    sp.MaSanPham,
    sp.TenSanPham,
    sp.Hang,
    sp.AnhDienThoai,
    cts.Gia,
    cts.BoNho
FROM SanPham sp
JOIN ChiTietSanPham cts ON sp.MaSanPham = cts.MaSanPham
WHERE sp.TrangThai = 1
";

$params = [];


if (!empty($ten)) {
    $sql .= " AND sp.TenSanPham LIKE :ten";
    $params[':ten'] = '%' . $ten . '%';
}


if ($min !== '') {
    $sql .= " AND cts.Gia >= :min";
    $params[':min'] = $min;
}


if ($max !== '') {
    $sql .= " AND cts.Gia <= :max";
    $params[':max'] = $max;
}

if (!empty($hang)) {
    $hangs = explode(',', $hang);

    $placeholders = [];
    foreach ($hangs as $index => $value) {
        $key = ":hang" . $index;
        $placeholders[] = $key;
        $params[$key] = trim($value);
    }

    $sql .= " AND sp.Hang IN (" . implode(',', $placeholders) . ")";
}


$stmt = $conn->prepare($sql);
$stmt->execute($params);

$data = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "count" => count($data),
    "data" => $data
]);
