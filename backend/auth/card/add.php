<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php";

$sql = "SELECT * FROM giohang";
$stmt = $conn->prepare($sql);
$stmt->execute();

echo json_encode([
    "success" => true,
    "data" => $stmt->fetchAll(PDO::FETCH_ASSOC)
]);
