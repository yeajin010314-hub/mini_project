<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ERROR</title>

<style>
    body {
        margin: 0;
        font-family: Arial, sans-serif;
        background: #f5f6f8;
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .error-container {
        background: #ffffff;
        width: 420px;
        padding: 50px 40px;
        text-align: center;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.12);
    }

    .error-code {
        font-size: 80px;
        font-weight: bold;
        color: #1e88e5;   /* 메인페이지 파란색 계열 */
        margin-bottom: 10px;
    }

    .error-title {
        font-size: 22px;
        font-weight: bold;
        margin-bottom: 15px;
        color: #333;
    }

    .error-desc {
        font-size: 14px;
        color: #777;
        line-height: 1.6;
        margin-bottom: 30px;
    }

    .btn-home {
        display: inline-block;
        padding: 12px 26px;
        background: #1e88e5;
        color: #fff;
        text-decoration: none;
        border-radius: 30px;
        font-size: 14px;
        transition: 0.3s;
    }

    .btn-home:hover {
        background: #1565c0;
    }
</style>
</head>
<body>

<div class="error-container">
    <div class="error-code">ERROR</div>
    <div class="error-title">페이지를 찾을 수 없습니다</div>
    <div class="error-desc">
        요청하신 페이지가 존재하지 않거나<br>
        주소가 잘못 입력되었습니다.
    </div>
    <a href="/mini_project/main" class="btn-home">메인으로 이동</a>
</div>

</body>
</html>
