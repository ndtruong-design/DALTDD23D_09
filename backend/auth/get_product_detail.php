<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

$maSanPham = $_GET['MaSanPham'] ?? null;
$boNho=$_GET['BoNho'] ?? null;

if (!$maSanPham || !$boNho) {
    echo json_encode(['error' => 'Thiếu MaSanPham hoặc BoNho']);
    exit;
}

try {
$stmt = $conn->prepare("
    SELECT sp.TenSanPham,sp.Hang,sp.MoTa, ct.*
    FROM SanPham sp
    JOIN ChiTietSanPham ct ON sp.MaSanPham = ct.MaSanPham
    WHERE sp.MaSanPham = :maSanPham AND ct.BoNho = :boNho AND sp.TrangThai = 1
");
$stmt->execute([':maSanPham' => $maSanPham, ':boNho' => $boNho]);
    $product = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$product) {
        echo json_encode(['error' => 'Sản phẩm không tồn tại']);
        exit;
    }
    echo json_encode($product);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>