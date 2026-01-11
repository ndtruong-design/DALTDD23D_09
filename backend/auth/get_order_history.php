<?php
// get_order_history.php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *"); // Cho phép gọi từ mọi nguồn (CORS)
header("Access-Control-Allow-Methods: GET");

require_once 'db_connect.php';

// Kiểm tra xem có truyền MaKhachHang không
if (!isset($_GET['user_id'])) {
    echo json_encode(["success" => false, "message" => "Thiếu MaKhachHang (user_id)"]);
    exit();
}

$user_id = intval($_GET['user_id']);

// SQL Query: Join các bảng để lấy thông tin đơn hàng + chi tiết sản phẩm + hình ảnh
// Lưu ý: Logic lấy ảnh đại diện phải khớp với Màu của sản phẩm trong chi tiết đơn hàng
$sql = "
    SELECT 
        dh.MaDonHang, 
        dh.NgayDatHang, 
        dh.NgayDuKien,
        dh.TrangThai AS TrangThaiDonHang, 
        dh.TrangThaiThanhToan,
        dh.TongTien, 
        dh.DiaChiGiaoHang,
        pt.TenPhuongThuc,
        
        -- Thông tin chi tiết sản phẩm trong đơn
        sp.TenSanPham,
        ctsp.BoNho,
        ctsp.RAM,
        ms.TenMau,
        ctdh.SoLuong,
        ctdh.DonGia,
        
        -- Lấy ảnh đại diện khớp với màu sắc
        (SELECT DuongLinkAnh FROM HinhAnh ha 
         WHERE ha.MaSanPham = sp.MaSanPham 
         AND ha.MaMau = ctsp.MaMau 
         AND ha.LaAnhDaiDien = 1 
         LIMIT 1) AS HinhAnhSanPham

    FROM DonHang dh
    JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang
    JOIN ChiTietSanPham ctsp ON ctdh.MaChiTietSP = ctsp.MaChiTietSP
    JOIN SanPham sp ON ctsp.MaSanPham = sp.MaSanPham
    LEFT JOIN MauSac ms ON ctsp.MaMau = ms.MaMau
    LEFT JOIN PhuongThucThanhToan pt ON dh.MaPTTT = pt.MaPTTT
    
    WHERE dh.MaKhachHang = ?
    ORDER BY dh.NgayDatHang DESC
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];

// Hàm helper để map trạng thái đơn hàng sang text tiếng Việt
function getStatusText($status_code) {
    switch ($status_code) {
        case 0: return "Chờ duyệt";
        case 1: return "Đang giao hàng";
        case 2: return "Giao thành công";
        case 3: return "Đã hủy";
        default: return "Không xác định";
    }
}

// Hàm helper map trạng thái thanh toán
function getPaymentStatusText($status_code) {
    switch ($status_code) {
        case 0: return "Chưa thanh toán";
        case 1: return "Đã thanh toán";
        case 2: return "Đã hoàn tiền";
        default: return "Khác";
    }
}

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $maDonHang = $row['MaDonHang'];

        // Nếu đơn hàng chưa tồn tại trong mảng kết quả, tạo mới
        if (!isset($orders[$maDonHang])) {
            $orders[$maDonHang] = [
                'order_id' => $row['MaDonHang'],
                'date_ordered' => $row['NgayDatHang'],
                'date_expected' => $row['NgayDuKien'],
                'total_price' => (float)$row['TongTien'],
                'total_price_formatted' => number_format($row['TongTien'], 0, ',', '.') . ' đ',
                'status_code' => $row['TrangThaiDonHang'],
                'status_text' => getStatusText($row['TrangThaiDonHang']),
                'payment_status_text' => getPaymentStatusText($row['TrangThaiThanhToan']),
                'payment_method' => $row['TenPhuongThuc'],
                'address' => $row['DiaChiGiaoHang'],
                'items' => [] // Khởi tạo mảng chứa các sản phẩm
            ];
        }

        // Thêm sản phẩm vào danh sách items của đơn hàng đó
        $orders[$maDonHang]['items'][] = [
            'product_name' => $row['TenSanPham'],
            'variant_info' => trim($row['BoNho'] . " " . $row['RAM']), // VD: 256GB 8GB
            'color' => $row['TenMau'],
            'quantity' => (int)$row['SoLuong'],
            'price' => (float)$row['DonGia'],
            'price_formatted' => number_format($row['DonGia'], 0, ',', '.') . ' đ',
            'image' => $row['HinhAnhSanPham'] ?? 'https://via.placeholder.com/150' // Ảnh fallback nếu null
        ];
    }
    
    // Reset keys của array để trả về JSON dạng mảng [{}, {}] thay vì object {"1": {}, "5": {}}
    $response_data = array_values($orders);
    
    echo json_encode([
        "success" => true,
        "data" => $response_data
    ]);

} else {
    // Không có đơn hàng nào
    echo json_encode([
        "success" => true,
        "data" => [] 
    ]);
}

$stmt->close();
$conn->close();
?>