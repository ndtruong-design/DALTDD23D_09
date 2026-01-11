
<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

try {
    $sql = "SELECT
    sp.*,
    ct.Gia,
    ct.BoNho,
    ha.DuongLinkAnh
FROM sanpham sp
LEFT JOIN chitietsanpham ct ON sp.MaSanPham = ct.MaSanPham
LEFT JOIN hinhanh ha ON ct.MaChiTietSP = ha.MaChiTietSP AND ha.LaAnhDaiDien = 1 
WHERE sp.TrangThai = 1";        

 
            
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $products = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($products);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>

