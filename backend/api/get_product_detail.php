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
    // Get product info with specs
    $stmt = $conn->prepare("
        SELECT sp.*, tst.ManHinh, tst.KichThuoc, tst.CameraSau, tst.CameraTruoc, tst.Pin, tst.HeDieuHanh, tst.CPU, tst.GPU, tst.RAM
        FROM SanPham sp
        LEFT JOIN ThongSoKyThuat tst ON sp.MaThongSo = tst.MaThongSo
        WHERE sp.MaSanPham = :maSanPham AND sp.TrangThai = 1
    ");
    $stmt->execute([':maSanPham' => $maSanPham]);
    $product = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$product) {
        echo json_encode(['error' => 'Sản phẩm không tồn tại']);
        exit;
    }

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