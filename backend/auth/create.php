<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../config/db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);

$ten         = $data['ten'] ?? '';
$sdt         = $data['sodienthoai'] ?? '';
$diachi      = $data['diachi'] ?? '';
$phuongthuc  = $data['phuongthuc'] ?? 'COD';
$items       = $data['items'] ?? [];

if (empty($ten) || empty($sdt) || empty($diachi) || empty($items)) {
    echo json_encode([
        "success" => false,
        "message" => "Thiếu thông tin đặt hàng"
    ]);
    exit;
}

try {
    $conn->beginTransaction();


    $stmt = $conn->prepare("
        INSERT INTO donhang (TenNguoiNhan, SoDienThoai, DiaChi, PhuongThucThanhToan, TongTien)
        VALUES (:ten, :sdt, :diachi, :pttt, 0)
    ");
    $stmt->execute([
        ':ten' => $ten,
        ':sdt' => $sdt,
        ':diachi' => $diachi,
        ':pttt' => $phuongthuc
    ]);

    $maDonHang = $conn->lastInsertId();
    $tongTien = 0;


    $stmtGia = $conn->prepare("
        SELECT Gia 
        FROM chitietsanpham
        WHERE MaSanPham = :masp
          AND MaMau = :mamau
          AND BoNho = :bonho
        LIMIT 1
    ");

    $stmtCT = $conn->prepare("
        INSERT INTO chitietdonhang
        (MaDonHang, MaSanPham, MaMau, BoNho, Gia, SoLuong)
        VALUES (:madon, :masp, :mamau, :bonho, :gia, :soluong)
    ");

    foreach ($items as $item) {
        $stmtGia->execute([
            ':masp' => $item['MaSanPham'],
            ':mamau' => $item['MaMau'],
            ':bonho' => $item['BoNho']
        ]);

        $gia = $stmtGia->fetchColumn();
        if (!$gia) throw new Exception("Không tìm thấy sản phẩm");

        $thanhTien = $gia * $item['SoLuong'];
        $tongTien += $thanhTien;

        $stmtCT->execute([
            ':madon' => $maDonHang,
            ':masp' => $item['MaSanPham'],
            ':mamau' => $item['MaMau'],
            ':bonho' => $item['BoNho'],
            ':gia' => $gia,
            ':soluong' => $item['SoLuong']
        ]);
    }


    $stmt = $conn->prepare("
        UPDATE donhang SET TongTien = :tong WHERE MaDonHang = :madon
    ");
    $stmt->execute([
        ':tong' => $tongTien,
        ':madon' => $maDonHang
    ]);

    $conn->commit();

    echo json_encode([
        "success" => true,
        "MaDonHang" => $maDonHang,
        "TongTien" => $tongTien
    ]);

} catch (Exception $e) {
    $conn->rollBack();
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
