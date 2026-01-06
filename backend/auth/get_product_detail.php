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
    // Get product info
    $stmt = $conn->prepare("
        SELECT * FROM SanPham
        WHERE MaSanPham = :maSanPham AND TrangThai = 1
    ");
    $stmt->execute([':maSanPham' => $maSanPham]);
    $product = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$product) {
        echo json_encode(['error' => 'Sản phẩm không tồn tại']);
        exit;
    }

    // Get images
    $stmt3 = $conn->prepare("
        SELECT * FROM HinhAnh
        WHERE MaSanPham = :maSanPham
    ");
    $stmt3->execute([':maSanPham' => $maSanPham]);
    $images = $stmt3->fetchAll(PDO::FETCH_ASSOC);

    $product['images'] = $images;

    // Get variants
    $stmt2 = $conn->prepare("
        SELECT ct.*, ms.TenMau
        FROM ChiTietSanPham ct
        JOIN MauSac ms ON ct.MaMau = ms.MaMau
        WHERE ct.MaSanPham = :maSanPham
    ");
    $stmt2->execute([':maSanPham' => $maSanPham]);
    $variants = $stmt2->fetchAll(PDO::FETCH_ASSOC);

    $product['variants'] = $variants;

    echo json_encode($product);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>