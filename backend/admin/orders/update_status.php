<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once '../../config/db_connect.php';

try {
    $data = json_decode(file_get_contents("php://input"), true);
    
    if (!isset($data['MaDonHang']) || !isset($data['TrangThai'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Thiếu thông tin MaDonHang hoặc TrangThai'], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    $maDonHang = intval($data['MaDonHang']);
    $trangThaiMoi = intval($data['TrangThai']);
    
    if ($trangThaiMoi < 0 || $trangThaiMoi > 3) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Trạng thái không hợp lệ (0-3)'], JSON_UNESCAPED_UNICODE);
        exit();
    }
    

    $checkSql = "SELECT TrangThai FROM DonHang WHERE MaDonHang = ?";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([$maDonHang]);
    $order = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$order) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Không tìm thấy đơn hàng'], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
    $trangThaiCu = intval($order['TrangThai']);
    

    if ($trangThaiCu == 2) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Không thể thay đổi trạng thái đơn hàng đã giao'], JSON_UNESCAPED_UNICODE);
        exit();
    }
    
 
    if ($trangThaiCu == 3 && $trangThaiMoi != 3) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Đơn hàng đã hủy, không thể khôi phục lại!'], JSON_UNESCAPED_UNICODE);
        exit();
    }

    if ($trangThaiMoi < $trangThaiCu && $trangThaiMoi != 3) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Không thể chuyển về trạng thái cũ'], JSON_UNESCAPED_UNICODE);
        exit();
    }
    

    $conn->beginTransaction();
    

    $updateSql = "UPDATE DonHang SET TrangThai = ? WHERE MaDonHang = ?";
    $stmt = $conn->prepare($updateSql);
    $stmt->execute([$trangThaiMoi, $maDonHang]);
    

    if ($trangThaiMoi == 3 && $trangThaiCu != 3) {

        $getItemsSql = "SELECT MaChiTietSP, SoLuong FROM ChiTietDonHang WHERE MaDonHang = ?";
        $getItemsStmt = $conn->prepare($getItemsSql);
        $getItemsStmt->execute([$maDonHang]);
        $items = $getItemsStmt->fetchAll(PDO::FETCH_ASSOC);
        
 
        $restoreSql = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaChiTietSP = ?";
        $restoreStmt = $conn->prepare($restoreSql);
        
        foreach ($items as $item) {
            $restoreStmt->execute([$item['SoLuong'], $item['MaChiTietSP']]);
        }
    }
    

    $conn->commit();
    
    $statusText = '';
    switch ($trangThaiMoi) {
        case 0: $statusText = 'Chờ duyệt'; break;
        case 1: $statusText = 'Đang giao'; break;
        case 2: $statusText = 'Đã giao'; break;
        case 3: $statusText = 'Đã hủy'; break;
    }
    
    echo json_encode([
        'success' => true,
        'message' => "Cập nhật thành công: {$statusText}"
    ], JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    if ($conn->inTransaction()) {
        $conn->rollBack();
    }
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Lỗi server: ' . $e->getMessage()], JSON_UNESCAPED_UNICODE);
}
?>