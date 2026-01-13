<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

// Thay đổi tham số nhận vào từ MaChiTietSP sang MaSanPham và BoNho
$maSanPham = $_GET['MaSanPham'] ?? null;
$boNho = $_GET['BoNho'] ?? null;

if (!$maSanPham || !$boNho) {
    echo json_encode(['error' => 'Thiếu tham số MaSanPham hoặc BoNho']);
    exit;
}

try {
    // Truy vấn lấy tất cả các màu (MaMau) có tồn tại trong bảng ChiTietSanPham 
    // tương ứng với điện thoại và bộ nhớ đó
    $stmt = $conn->prepare("
        SELECT DISTINCT ms.MaMau, ms.TenMau, ms.MaHex 
        FROM Mausac ms
        INNER JOIN ChiTietSanPham ct ON ms.MaMau = ct.MaMau
        WHERE ct.MaSanPham = :maSanPham AND ct.BoNho = :boNho
    ");
    
    $stmt->execute([
        ':maSanPham' => $maSanPham,
        ':boNho' => $boNho
    ]);
    
    $colors = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Nếu không có màu nào (có thể do sai MaSanPham hoặc BoNho)
    if (!$colors) {
        echo json_encode([]); // Trả về mảng rỗng thay vì báo lỗi để App dễ xử lý
        exit;
    }

    echo json_encode($colors);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>