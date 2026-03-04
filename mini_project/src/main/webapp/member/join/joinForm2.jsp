<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- 회원가입
	주요 기능: 메인페이지 이동, 내 정보 입력을 통한 회원가입 기능, 로그인페이지 이동-->


<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>TRAVEL+ | 회원가입</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/joinForm.css">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>

<body>
<form action="<%=request.getContextPath()%>/member/join" method="post" onsubmit="return join();">

<!-- 상단 -->
<div class="topbar">
    <a href="<%=request.getContextPath()%>/main" class="logo">TRAVEL+</a>
</div>

<div class="main">
    <div class="title">회원가입</div>

    <div class="form-group">
        <label>회원명<span class="required">*</span></label>
        <input type="text" name="memberNm" placeholder="이름입력" required>
    </div>

    <div class="form-group">
        <label>닉네임<span class="required">*</span></label>
        
        <div class="id-wrap">
        <input type="text" id="nick" name="nickNm" placeholder="닉네임 입력" required>
        <button type="button" onclick="checkNick()">중복확인</button>
        </div>
        
        <div id="nickMsg" class="msg">
    </div>

    <div class="form-group">
        <label>전화번호<span class="required">*</span></label>
        <input type="text" id="phone" name="telNo" placeholder="010-0000-0000"
               oninput="onlyNumber(this)" maxlength="13" inputmode="numeric" required>
        <div id="phoneMsg" class="msg" style="color:red; display:none;">
            *숫자만 입력 가능합니다
        </div>
    </div>

    <!-- 생년월일 -->
    <div class="form-group">
        <label>생년월일<span class="required">*</span></label>
        <div class="birth-wrap">
            <input type="date" name="birth" id="birth" required>
        </div>
    </div>

    <!-- 아이디 -->
    <div class="form-group">
        <label>회원아이디<span class="required">*</span></label>
        <div class="id-wrap">
            <input type="text" id="userId" name="memberId" placeholder="아이디 입력" required>
            <button type="button" onclick="checkId()">중복확인</button>
        </div>
        <div id="idMsg" class="msg"></div>
    </div>

    <!-- 비밀번호 -->
    <div class="form-group">
        <label>비밀번호<span class="required">*</span></label>
        <input type="password" id="pw" name="passwd" placeholder="비밀번호 입력" required>
      <div id="pwMsg" class="msg" style="color:red; display:none;">
            *비밀번호는 영문+숫자 포함 4~20자로 입력해주세요.
            </div>
    </div>

    <div class="form-group">
        <label>비밀번호 확인<span class="required">*</span></label>
        <input type="password" id="pwCheck" placeholder="비밀번호 다시 입력" onkeyup="checkPassword()" required>
        <div id="pwMsg2" class="msg" style="color:red; display:none;">
            *비밀번호가 일치하지 않습니다
        </div>
    </div>

    <!-- 이메일 -->
    <div class="form-group">
        <label>이메일<span class="required">*</span></label>
        <div class="email-wrap">
            <input type="text" id="emailId" placeholder="이메일 아이디">
            @
            <select id="emailDomain" onchange="toggleDomainInput()">
                <option value="naver.com">naver.com</option>
                <option value="daum.net">daum.net</option>
                <option value="nate.com">nate.com</option>
                <option value="gmail.com">gmail.com</option>
                <option value="outlook.com">outlook.com</option>
                <option value="custom">--직접입력--</option>
            </select>
            <input type="text" id="customDomain" placeholder="직접입력" style="display:none;">
        </div>
    </div>

    <!-- 서버로 보낼 이메일 -->
    <input type="hidden" name="email" id="fullEmail">

    <button class="submit-btn" type="submit">가입하기</button>

    <div class="bottom-text">
        이미 계정이 있나요?
        <a href="<%=request.getContextPath()%>/member/login/loginForm2.jsp">로그인</a>
    </div>
</div>

</form>
<script>
const ctx = "<%=request.getContextPath()%>";
</script>
<script src = "<%=request.getContextPath()%>/resources/js/joinForm.js"></script>

</body>
</html>