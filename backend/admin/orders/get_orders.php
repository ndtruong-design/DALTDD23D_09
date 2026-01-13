<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');
require_once '../../config/db_connect.php';

try {
    
    $status = isset($_GET['status']) ? intval($_GET['status']) : null;
    
    $sql = "SELECT 
                dh.MaDonHang, 
                dh.NgayDatHang, 
                dh.TongTien, 
                dh.TrangThai,
                kh.HoTen, 
                kh.SoDienThoai
            FROM DonHang dh
            JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang";
    if ($status !== null) {
        $sql .= " WHERE dh.TrangThai = :status";
    }
    $sql .= " ORDER BY dh.NgayDatHang DESC";
    $stmt = $conn->prepare($sql);
    if ($status !== null) {
        $stmt->bindParam(':status', $status, PDO::PARAM_INT);
    }  
    $stmt->execute();
    $orders = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $statusText = '';
        switch ($row['TrangThai']) {
            case 0:
                $statusText = 'Chờ duyệt';
                break;
            case 1:
                $statusText = 'Đang giao';
                break;
            case 2:
                $statusText = 'Đã giao';
                break;
            case 3:
                $statusText = 'Đã hủy';
                break;
            default:
                $statusText = '';
        }
        
        $orders[] = array(
            'MaDonHang' => $row['MaDonHang'],
            'NgayDatHang' => $row['NgayDatHang'],
            'TongTien' => floatval($row['TongTien']),
            'TrangThai' => intval($row['TrangThai']),
            'TrangThaiText' => $statusText,
            'HoTen' => $row['HoTen'],
            'SoDienThoai' => $row['SoDienThoai']
        );
    }
    echo json_encode(array(
        'success' => true,
        'orders' => $orders,
        'total' => count($orders)
    ), JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        'success' => false,
        'message' => 'Lỗi server: ' . $e->getMessage()
    ), JSON_UNESCAPED_UNICODE);
}
?>