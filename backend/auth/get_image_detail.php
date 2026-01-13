<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

$maSanPham = $_GET['MaSanPham'] ?? null;
$maMau=$_GET['MaMau'] ?? null;

if (!$maSanPham || !$maMau) {
    echo json_encode(['error' => 'Thiếu MaSanPham hoặc MaMau']);
    exit;
}

try {
    // Get product info
    $stmt = $conn->prepare("
        SELECT ha.* FROM HinhAnh ha, SanPham sp, ChiTietSanPham ct
        WHERE sp.MaSanPham = :maSanPham AND laAnhDaiDien=0 AND ct.MaMau = :maMau AND sp.MaSanPham = ct.MaSanPham AND ct.MaChiTietSP = ha.MaChiTietSP
    ");
    $stmt->execute([':maSanPham' => $maSanPham, ':maMau' => $maMau]);
    $image = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if (!$image) {
        echo json_encode(['error' => 'Hình ảnh không tồn tại']);
        exit;
    }
    echo json_encode($image );
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>