
<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=UTF-8');
require_once '../../config/db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200); exit();
}
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(array(
        "success" => false,
        "message" => "Method not allowed",
        "data" => null
    ), JSON_UNESCAPED_UNICODE); exit();
}

$status = isset($_GET['status']) ? intval($_GET['status']) : null;
if ($status !== null) {
    $sql = "SELECT dh.MaDonHang, dh.NgayDatHang, dh.TongTien, dh.TrangThai, kh.HoTen, kh.SoDienThoai
        FROM DonHang dh
        JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang
        WHERE dh.TrangThai = ?
        ORDER BY dh.NgayDatHang DESC";
    $stmt = $conn->prepare($sql);
    $stmt->execute(array($status));
} else {
    $sql = "SELECT dh.MaDonHang, dh.NgayDatHang, dh.TongTien, dh.TrangThai, kh.HoTen, kh.SoDienThoai
        FROM DonHang dh
        JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang
        ORDER BY dh.NgayDatHang DESC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
}

$orders = array();
while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
    $statusText = '';
    if ($row['TrangThai'] == 0) $statusText = 'Chờ duyệt';
    elseif ($row['TrangThai'] == 1) $statusText = 'Đang giao';
    elseif ($row['TrangThai'] == 2) $statusText = 'Đã giao';
    elseif ($row['TrangThai'] == 3) $statusText = 'Đã hủy';

    $orders[] = array(
        'MaDonHang' => intval($row['MaDonHang']),
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
?>
