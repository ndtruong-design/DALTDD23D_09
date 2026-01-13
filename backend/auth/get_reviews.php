<?php

require_once "../config/db_connect.php";


// Lấy phương thức gửi request (GET, POST, PUT, DELETE...)
$method = $_SERVER['REQUEST_METHOD'];

// --- XỬ LÝ DỰA TRÊN METHOD ---
switch ($method) {
    case 'GET':
        handleGetReviews($conn);
        break;
        
    case 'POST':
        handleSaveReview($conn);
        break;

    default:
        http_response_code(405); // Method Not Allowed
        echo json_encode(["status" => "error", "message" => "Phương thức không được hỗ trợ. Chỉ dùng GET hoặc POST."]);
        break;
}
        
// =========================================================================
// HÀM 1: LẤY DANH SÁCH ĐÁNH GIÁ (KHI METHOD LÀ GET)
// =========================================================================
function handleGetReviews($conn) {
    // Kiểm tra tham số sp_id trên URL
    if (!isset($_GET['sp_id']) || empty($_GET['sp_id'])) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Thiếu mã sản phẩm (sp_id)."]);
        return;
    }

    $maSanPham = $_GET['sp_id'];

    try {
        // Query: Lấy đánh giá của các đơn hàng có chứa sản phẩm này
        $query = "
            SELECT DISTINCT 
                dg.MaDanhGia,
                dg.NoiDung,
                kh.HoTen,
                kh.AnhDaiDien,
                dg.TrangThai AS TrangThaiDanhGia,
                dh.NgayDatHang
            FROM DanhGia dg
            JOIN DonHang dh ON dg.MaDonHang = dh.MaDonHang
            JOIN KhachHang kh ON dh.MaKhachHang = kh.MaKhachHang
            JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang
            JOIN ChiTietSanPham ctsp ON ctdh.MaChiTietSP = ctsp.MaChiTietSP
            WHERE ctsp.MaSanPham = :maSanPham 
              AND dg.TrangThai = 1
            ORDER BY dh.NgayDatHang DESC
        ";

        $stmt = $conn->prepare($query);
        $stmt->bindParam(':maSanPham', $maSanPham, PDO::PARAM_INT);
        $stmt->execute();
        $reviews = $stmt->fetchAll(PDO::FETCH_ASSOC);

        if (count($reviews) > 0) {
            http_response_code(200);
            echo json_encode(["status" => "success", "data" => $reviews]);
        } else {
            echo json_encode(["status" => "success", "message" => "Chưa có đánh giá nào.", "data" => []]);
        }

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Lỗi server: " . $e->getMessage()]);
    }
}

// =========================================================================
// HÀM 2: LƯU ĐÁNH GIÁ MỚI (KHI METHOD LÀ POST)
// =========================================================================
function handleSaveReview($conn) {
    // Lấy dữ liệu JSON từ body
    $data = json_decode(file_get_contents("php://input"));

    // Validate dữ liệu
    if (!isset($data->order_id) || !isset($data->content) || empty(trim($data->content))) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Thiếu order_id hoặc content."]);
        return;
    }

    $maDonHang = $data->order_id;
    $noiDung = htmlspecialchars(strip_tags($data->content));

    try {
        // 1. Kiểm tra đơn hàng có tồn tại và đã hoàn thành (TrangThai = 2) chưa?
        // Lưu ý: Dữ liệu mẫu bạn cung cấp đơn hàng đang có TrangThai = 1 (Đang giao).
        // Để test thành công, bạn cần sửa trạng thái đơn hàng trong DB thành 2.
        $checkOrder = $conn->prepare("SELECT MaDonHang FROM DonHang WHERE MaDonHang = :maDonHang AND TrangThai = 2");
        $checkOrder->bindParam(':maDonHang', $maDonHang);
        $checkOrder->execute();

        if ($checkOrder->rowCount() == 0) {
            http_response_code(400);
            echo json_encode(["status" => "error", "message" => "Đơn hàng không tồn tại hoặc chưa hoàn thành (chỉ được đánh giá khi đã nhận hàng)."]);
            return;
        }

        // 2. Kiểm tra đã đánh giá chưa
        $checkExist = $conn->prepare("SELECT MaDanhGia FROM DanhGia WHERE MaDonHang = :maDonHang");
        $checkExist->bindParam(':maDonHang', $maDonHang);
        $checkExist->execute();

        if ($checkExist->rowCount() > 0) {
            http_response_code(400);
            echo json_encode(["status" => "error", "message" => "Bạn đã đánh giá đơn hàng này rồi."]);
            return;
        }

        // 3. Insert
        $stmt = $conn->prepare("INSERT INTO DanhGia (NoiDung, MaDonHang, TrangThai) VALUES (:noiDung, :maDonHang, 1)");
        $stmt->bindParam(':noiDung', $noiDung);
        $stmt->bindParam(':maDonHang', $maDonHang);

        if ($stmt->execute()) {
            http_response_code(201);
            echo json_encode(["status" => "success", "message" => "Đánh giá thành công!"]);
        } else {
            http_response_code(503);
            echo json_encode(["status" => "error", "message" => "Lỗi khi lưu đánh giá."]);
        }

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Lỗi CSDL: " . $e->getMessage()]);
    }
}
?>