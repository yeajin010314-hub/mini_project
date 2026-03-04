<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String err = request.getParameter("err"); // email_empty, email_wrong
%>


<!-- 탈퇴회원 계정 복구
탈퇴일로부터 3개월 이내로 복구 신청->가입시 기재한 이메일 입력->복구
-->

<!DOCTYPE html>
<html lang="ko">
<head>
<style>
/* restoreConfirm 전용 */
.restore-desc{
  margin:-10px 0 14px;
  font-size:13px;
  color:#555;
  line-height:1.5;
  text-align:center;
}
</style>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/loginForm.css">
<meta charset="UTF-8">
<title>여기서놀자|계정 복구</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body>
<div class="header">
    <div class="logo">
        <a href="<%=request.getContextPath()%>/main">여기서놀자</a>
    </div>
</div>

<div class="container">
  <div class="login-card">
      <h2>계정 복구</h2>

      <div class="restore-desc">
          탈퇴 계정입니다.<br>
          복구하려면 <b>가입 시 등록한 이메일</b>을 입력하세요.
      </div>

      <% if ("email_empty".equals(err)) { %>
          <div class="msg">이메일을 입력해주세요.</div>
      <% } else if ("email_wrong".equals(err)) { %>
          <div class="msg">이메일이 일치하지 않습니다.</div>
      <% } %>

      <form action="<%=request.getContextPath()%>/member/restoreDo" method="post">
          <div class="input-group">
              <label>가입 이메일</label>
              <input type="email" name="email" placeholder="예) abcd123@naver.com" required>
          </div>

          <input type="submit" value="복구하기" class="login-btn">
      </form>

      <div class="find-links">
          <a href="<%=request.getContextPath()%>/member/login/loginForm2.jsp">취소</a>
      </div>
  </div>
</div>

</body>
</html>

