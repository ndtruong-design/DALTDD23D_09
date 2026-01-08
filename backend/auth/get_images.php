<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

$maSanPham = $_GET['MaSanPham'] ?? null;

if (!$maSanPham) {
    echo json_encode(['error' => 'Thiếu MaSanPham']);
    exit;
}

try {
    $stmt = $conn->prepare("
        SELECT MaAnh, DuongLinkAnh, LaAnhDaiDien
        FROM HinhAnh
        WHERE MaSanPham = :maSanPham
        ORDER BY LaAnhDaiDien DESC, MaAnh ASC
    ");
    $stmt->execute([':maSanPham' => $maSanPham]);
    $images = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($images);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>