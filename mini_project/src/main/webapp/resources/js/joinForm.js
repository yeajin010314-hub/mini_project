let idChecked = false;
let nickChecked = false;

/* 입력 변경 시 중복확인 상태 초기화 */
document.addEventListener("DOMContentLoaded", () => {
  const idInput = document.getElementById("userId");
  const nickInput = document.getElementById("nick");

  if (idInput) {
    idInput.addEventListener("input", () => {
      idChecked = false;
      const msg = document.getElementById("idMsg");
      if (msg) msg.innerText = "";
    });
  }

  if (nickInput) {
    nickInput.addEventListener("input", () => {
      nickChecked = false;
      const msg = document.getElementById("nickMsg");
      if (msg) msg.innerText = "";
    });
  }
});

/* 아이디 중복확인 */
function checkId() {
  const idInput = document.getElementById("userId");
  const msg = document.getElementById("idMsg");
  if (!idInput || !msg) return;

  const id = idInput.value.trim();
  const idRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{5,15}$/;

  if (!idRegex.test(id)) {
    msg.style.color = "red";
    msg.innerText = "영문+숫자 5~15자로 입력하세요.";
    idChecked = false;
    return;
  }
  
  const url = ctx+"/member/check?type=memberId&value="+encodeURIComponent(id)
	console.log("ID CHECK URL =", url);
  
	fetch(url)	  
    .then(res => res.text())
    .then(data => {
      data = data.trim().toUpperCase();
      if (data === "DUP") {
        msg.style.color = "red";
        msg.innerText = "이미 사용 중인 아이디입니다.";
        idChecked = false;
      } else {
        msg.style.color = "green";
        msg.innerText = "사용 가능한 아이디입니다.";
        idChecked = true;
      }
    })
    .catch(() => {
      msg.style.color = "red";
      msg.innerText = "중복확인 통신 오류";
      idChecked = false;
    });
}

/* 닉네임 중복확인 */
function checkNick() {
  const nickInput = document.getElementById("nick");
  const msg = document.getElementById("nickMsg");
  if (!nickInput || !msg) return;
  
  const nick = nickInput.value.trim();
  console.log("NICK =", nick);
  if (nick.length < 2 || nick.length > 6) {
    msg.style.color = "red";
    msg.innerText = "닉네임은 2~6자로 입력하세요.";
    nickChecked = false;
    return;
  }
	const url = ctx+ "/member/check?type=nickNm&value="+encodeURIComponent(nick)
	console.log("NICK CHECK URL =",url);		
 
	fetch(url)
    .then(res => res.text())
    .then(data => {
      data = data.trim().toUpperCase();
      if (data === "DUP") {
        msg.style.color = "red";
        msg.innerText = "이미 사용 중인 닉네임입니다.";
        nickChecked = false;
      } else {
        msg.style.color = "green";
        msg.innerText = "사용 가능한 닉네임입니다.";
        nickChecked = true;
      }
    })
    .catch(() => {
      msg.style.color = "red";
      msg.innerText = "중복확인 통신 오류";
      nickChecked = false;
    });
}

/* 전화번호: 숫자 + 하이픈 허용 + 자동 하이픈 */
function onlyNumber(input) {
  const phoneMsg = document.getElementById("phoneMsg");
  const raw = input.value || "";

  //허용 문자: 숫자 + 하이픈만
  const allowedRegex = /^[0-9-]*$/;

  //하이픈은 OK니까, "허용문자 아닌게 들어왔을 때만" 메시지
  if (phoneMsg) {
    phoneMsg.style.display = allowedRegex.test(raw) ? "none" : "block";
  }

  // 숫자만 추출해서 포맷 만들기
  let digits = raw.replace(/[^0-9]/g, "");
  if (digits.length > 11) digits = digits.slice(0, 11);

  let formatted = "";
  if (digits.length < 4) {
    formatted = digits;
  } else if (digits.length < 8) {
    formatted = digits.slice(0, 3) + "-" + digits.slice(3);
  } else {
    formatted =
      digits.slice(0, 3) + "-" +
      digits.slice(3, 7) + "-" +
      digits.slice(7);
  }

  input.value = formatted;
}



/* 비밀번호 확인 + 영문+숫자 4~20자 */
function checkPassword() {
  const pwEl = document.getElementById("pw");
  const pw2El = document.getElementById("pwCheck");
  const msg = document.getElementById("pwMsg");
  if (!pwEl || !pw2El || !msg) return;

  const pw = (pwEl.value || "").trim();
  const pw2 = (pw2El.value || "").trim();

  // 정규식: 영문 최소1 + 숫자 최소1 + 4~20자
  const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{4,20}$/;

  // 둘 다 비었으면 메시지 숨김
  if (pw.length === 0 && pw2.length === 0) {
    msg.style.display = "none";
    msg.textContent = "";
    return;
  }

  // 1) 형식 검사
  if (pw.length > 0 && !pwRegex.test(pw)) {
    msg.style.display = "block";
    msg.textContent = "비밀번호는 영문+숫자를 포함한 4~20자로 입력하세요.";
    return;
  }

  // 2) 확인칸이 비었으면 메시지 숨김
  if (pw2.length === 0) {
    msg.style.display = "none";
    msg.textContent = "";
    return;
  }

  // 3) 일치 여부
  if (pw !== pw2) {
    msg.style.display = "block";
    msg.textContent = "비밀번호가 일치하지 않습니다.";
    return;
  }

  // 4) 통과
  msg.style.display = "none";
  msg.textContent = "";
}

/* 이메일 도메인 직접입력 토글 */
function toggleDomainInput() {
  const domainSel = document.getElementById("emailDomain").value;
  const custom = document.getElementById("customDomain");
  if (!domainSel || !custom) return;

  if (domainSel === "custom") {
    custom.style.display = "block";
    custom.focus();
  } else {
    custom.style.display = "none";
    custom.value = "";
  }
}

/* 가입 버튼 검증 */
function join() {
  const emailIdEl = document.getElementById("emailId");
  const domainSelEl = document.getElementById("emailDomain");
  const customDomainEl = document.getElementById("customDomain");
  const fullEmailEl = document.getElementById("fullEmail");

  const emailId = emailIdEl ? emailIdEl.value.trim() : "";
  const domainSel = domainSelEl ? domainSelEl.value : "";
  const customDomain = customDomainEl ? customDomainEl.value.trim() : "";
  const domain = (domainSel === "custom") ? customDomain : domainSel;

  const full = (emailId && domain) ? (emailId + "@" + domain) : "";
  
  const pw = (document.getElementById("pw").value || "").trim();
  const pw2 = (document.getElementById("pwCheck").value || "").trim();
  const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{4,20}$/;

  if (!pwRegex.test(pw)) {
    alert("비밀번호는 영문+숫자를 포함한 4~20자로 입력하세요.");
    return false;
  }
  if (pw !== pw2) {
    alert("비밀번호가 일치하지 않습니다.");
    return false;
  }


  if (fullEmailEl) {
    fullEmailEl.value = full;
  }

  if (!full) {
    alert("이메일을 입력하세요.");
    return false;
  }
  if (!idChecked) {
    alert("아이디 중복확인을 해주세요.");
    return false;
  }
  if (!nickChecked) {
    alert("닉네임 중복확인을 해주세요.");
    return false;
  }
  return true;
}