<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        'success' => false,
        'message' => 'Chỉ hỗ trợ phương thức POST'
    ], JSON_UNESCAPED_UNICODE);
    exit();
}

require_once '../../config/db_connect.php';

try {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['MaDonHang']) || !isset($data['TrangThai'])) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Thiếu thông tin MaDonHang hoặc TrangThai'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $maDonHang = intval($data['MaDonHang']);
    $trangThaiMoi = intval($data['TrangThai']);

    if ($trangThaiMoi < 0 || $trangThaiMoi > 3) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Trạng thái không hợp lệ (0-3)'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $checkSql = "SELECT TrangThai, TrangThaiThanhToan FROM DonHang WHERE MaDonHang = ?";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([$maDonHang]);
    $order = $checkStmt->fetch(PDO::FETCH_ASSOC);

    if (!$order) {
        http_response_code(404);
        echo json_encode([
            'success' => false,
            'message' => 'Thông tin không hợp lệ'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $trangThaiCu = intval($order['TrangThai']);
    $trangThaiThanhToanHienTai = intval($order['TrangThaiThanhToan']);

    // Không cho sửa đơn đã giao
    if ($trangThaiCu == 2) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Không thể thay đổi trạng thái đơn hàng đã giao'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    // Không cho khôi phục đơn đã hủy
    if ($trangThaiCu == 3 && $trangThaiMoi != 3) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Đơn hàng đã hủy, không thể khôi phục lại'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    // Không cho chuyển ngược về trạng thái cũ (trừ hủy)
    if ($trangThaiMoi < $trangThaiCu && $trangThaiMoi != 3) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Không thể chuyển về trạng thái cũ'
        ], JSON_UNESCAPED_UNICODE);
        exit();
    }

    $conn->beginTransaction();

    // Xác định trạng thái thanh toán mới
    $trangThaiThanhToanMoi = $trangThaiThanhToanHienTai; // Mặc định giữ nguyên
    
    if ($trangThaiMoi == 2) {
        // Khi chuyển sang "Đã giao" -> Tự động cập nhật "Đã thanh toán"
        $trangThaiThanhToanMoi = 1;
    } elseif ($trangThaiMoi == 3 && $trangThaiThanhToanHienTai == 1) {
        // Khi hủy đơn mà đã thanh toán -> Chuyển sang "Đã hoàn tiền"
        $trangThaiThanhToanMoi = 2;
    }

    // Cập nhật trạng thái đơn hàng VÀ trạng thái thanh toán
    $updateSql = "UPDATE DonHang SET TrangThai = ?, TrangThaiThanhToan = ? WHERE MaDonHang = ?";
    $stmt = $conn->prepare($updateSql);
    $stmt->execute([$trangThaiMoi, $trangThaiThanhToanMoi, $maDonHang]);

    // Xử lý tồn kho khi hủy đơn
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

    $statusText = match ($trangThaiMoi) {
        0 => 'Chờ duyệt',
        1 => 'Đang giao',
        2 => 'Đã giao',
        3 => 'Đã hủy',
        default => 'Không xác định'
    };

    $paymentStatusText = match ($trangThaiThanhToanMoi) {
        0 => 'Chưa thanh toán',
        1 => 'Đã thanh toán',
        2 => 'Đã hoàn tiền',
        default => 'Không xác định'
    };

    // Thông báo có bao gồm cả trạng thái thanh toán nếu có thay đổi
    $message = "Cập nhật thành công: $statusText";
    if ($trangThaiThanhToanMoi != $trangThaiThanhToanHienTai) {
        $message .= " | $paymentStatusText";
    }

    echo json_encode([
        'success' => true,
        'message' => $message,
        'data' => [
            'TrangThai' => $trangThaiMoi,
            'TrangThaiThanhToan' => $trangThaiThanhToanMoi
        ]
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    if ($conn->inTransaction()) {
        $conn->rollBack();
    }
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Lỗi hệ thống'
    ], JSON_UNESCAPED_UNICODE);
}
?>