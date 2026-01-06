<?php
require_once '../config/db_connect.php';

$maAnh = $_GET['MaAnh'] ?? null;

if (!$maAnh) {
    http_response_code(400);
    echo 'Thiếu MaAnh';
    exit;
}

try {
    $stmt = $conn->prepare("SELECT DuongLinkAnh FROM HinhAnh WHERE MaAnh = :maAnh");
    $stmt->execute([':maAnh' => $maAnh]);
    $image = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$image) {
        http_response_code(404);
        echo 'Hình ảnh không tồn tại';
        exit;
    }

    $filePath = $image['DuongLinkAnh'];

    // Kiểm tra nếu file tồn tại
    if (!file_exists($filePath)) {
        http_response_code(404);
        echo 'File không tồn tại trên server';
        exit;
    }

    // Lấy loại file từ extension
    $extension = strtolower(pathinfo($filePath, PATHINFO_EXTENSION));
    $mimeTypes = [
        'jpg' => 'image/jpeg',
        'jpeg' => 'image/jpeg',
        'png' => 'image/png',
        'gif' => 'image/gif',
        'webp' => 'image/webp'
    ];

    $mimeType = $mimeTypes[$extension] ?? 'application/octet-stream';

    header('Content-Type: ' . $mimeType);
    header('Cache-Control: public, max-age=31536000'); // Cache 1 năm
    readfile($filePath);
} catch (PDOException $e) {
    http_response_code(500);
    echo 'Database error: ' . $e->getMessage();
}
?>