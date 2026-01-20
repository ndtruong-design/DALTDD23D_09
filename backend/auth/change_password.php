<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once '../config/db_connect.php';

try {
    $data = json_decode(file_get_contents("php://input"), true);
    if (!isset($data['MaKhachHang']) || !isset($data['MatKhauCu']) || !isset($data['MatKhauMoi'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Thiếu thông tin'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $maKhachHang = intval($data['MaKhachHang']);
    $matKhauCu = $data['MatKhauCu'];
    $matKhauMoi = $data['MatKhauMoi']; 


    $sql = "SELECT MatKhau FROM KhachHang WHERE MaKhachHang = ?";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$maKhachHang]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        echo json_encode([
            'success' => false,
            'message' => 'Không tìm thấy khách hàng'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    if (!password_verify($matKhauCu, $user['MatKhau'])) {
        echo json_encode([
            'success' => false,
            'message' => 'Mật khẩu cũ không đúng'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }
    $matKhauMoiHash = password_hash($matKhauMoi, PASSWORD_DEFAULT);
    $sqlUpdate = "UPDATE KhachHang SET MatKhau = ? WHERE MaKhachHang = ?";
    $stmtUpdate = $conn->prepare($sqlUpdate);
    
    if ($stmtUpdate->execute([$matKhauMoiHash, $maKhachHang])) {
        echo json_encode([
            'success' => true,
            'message' => 'Đổi mật khẩu thành công'
        ], JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Đổi mật khẩu thất bại'
        ], JSON_UNESCAPED_UNICODE);
    }

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Lỗi server: ' . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}
?>