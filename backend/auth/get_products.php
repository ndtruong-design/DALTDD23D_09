<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

try {
    // JOIN bảng SanPham với bảng AnhSanPham (lấy ảnh có LaAnhDaiDien = 1)
    $sql = "SELECT sp.*, p.DuongLinkAnh 
            FROM SanPham sp 
            LEFT JOIN HinhAnh p ON sp.MaSanPham = p.MaSanPham AND p.LaAnhDaiDien = 1 
            WHERE sp.TrangThai = 1";
            
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $products = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($products);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>