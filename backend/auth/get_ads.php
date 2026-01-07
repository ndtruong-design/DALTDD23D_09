<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../config/db_connect.php';

try {
    $stmt = $conn->prepare("SELECT MaQuangCao, HinhAnh FROM QuangCao WHERE TrangThai = 1");
    $stmt->execute();
    $ads = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($ads);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>