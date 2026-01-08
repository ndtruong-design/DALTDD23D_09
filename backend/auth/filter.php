<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php";

$min  = isset($_GET['min']) ? (int)$_GET['min'] : 0;
$max  = isset($_GET['max']) ? (int)$_GET['max'] : 0;
$hang = $_GET['hang'] ?? ''; 


$brandSql = "SELECT DISTINCT Hang FROM sanpham WHERE Hang IS NOT NULL AND Hang <> ''";
$brandStmt = $conn->prepare($brandSql);
$brandStmt->execute();
$brands = $brandStmt->fetchAll(PDO::FETCH_COLUMN);


$sql = "SELECT TenSanPham, MoTa, Hang FROM sanpham WHERE 1=1";
$params = [];

if ($min > 0) {
    $sql .= " AND Gia >= :min";
    $params[':min'] = $min;
}

if ($max > 0) {
    $sql .= " AND Gia <= :max";
    $params[':max'] = $max;
}

if (!empty($hang)) {
    $brandArr = explode(',', $hang);
    $placeholders = [];

    foreach ($brandArr as $i => $b) {
        $key = ":hang$i";
        $placeholders[] = $key;
        $params[$key] = trim($b);
    }

    $sql .= " AND Hang IN (" . implode(',', $placeholders) . ")";
}

$stmt = $conn->prepare($sql);
$stmt->execute($params);
$data = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "brands"  => $brands,
    "data"    => $data
]);
