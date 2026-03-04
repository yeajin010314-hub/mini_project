function goHome() { location.href = ctx + "/main"; }
function logout() { location.href = ctx + "/member/logout"; }

/* =======================
   공통: 사용자용 오류 메시지
   ======================= */
function showReqError() {
  alert("요청 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
}

/* =======================
   프로필 업로드(실제 동작)
   ======================= */
function openProfilePicker() {
  document.getElementById("profileFile").click();
}

document.getElementById("profileFile").addEventListener("change", function () {
  const file = this.files && this.files[0];
  if (!file) return;

  const fd = new FormData();
  fd.append("profileFile", file);

  fetch(ctx + "/member/updateProfileImg", {
    method: "POST",
    body: fd
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok || !text.startsWith("OK|")) {
        alert("프로필 변경에 실패했습니다. 잠시 후 다시 시도해주세요.");
        return;
      }
      // OK|/resources/upload/profile/xxx.jpg
      const path = text.split("|")[1] || "";
      document.getElementById("profileImg").src = ctx + path + "?v=" + Date.now();
      alert("프로필이 변경되었습니다.");
    })
    .catch(showReqError);
});


/* =======================
   닉네임 변경(실제 동작)
   - 서버 응답: OK / DUP / NOLOGIN / INVALID_LEN / EMPTY
   ======================= */
function startNickEdit() {
  const area = document.getElementById("nickEditArea");
  const input = document.getElementById("nickInput");
  const view = document.getElementById("nicknameView");
  if (!area || !input || !view) return;

  area.style.display = "flex";
  input.value = (view.textContent || "").trim();
  input.focus();
}

function cancelNickEdit() {
  const area = document.getElementById("nickEditArea");
  if (area) area.style.display = "none";
}

function saveNick() {
  const input = document.getElementById("nickInput");
  const view = document.getElementById("nicknameView");
  if (!input || !view) return;

  const nick = (input.value || "").trim();

  // 프론트 1차 검증(서버도 같은 검증 함)
  if (nick.length < 2 || nick.length > 6) {
    alert("닉네임은 2~6자로 입력하세요.");
    return;
  }

  fetch(ctx + "/member/update", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "nickNm=" + encodeURIComponent(nick)
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok) {
        if (text === "NOLOGIN") {
          alert("로그인이 필요합니다.");
          location.href = ctx + "/member/login/loginForm2.jsp";
          return;
        }
        if (text === "DUP") {
          alert("이미 사용 중인 닉네임입니다.");
          return;
        }
        if (text === "INVALID_LEN") {
          alert("닉네임은 2~6자로 입력하세요.");
          return;
        }
        if (text === "EMPTY") {
          alert("닉네임을 입력하세요.");
          return;
        }
        alert("닉네임 변경에 실패했습니다. 잠시 후 다시 시도해주세요.");
        return;
      }

      // OK
      if (text !== "OK") {
        // 혹시 서버가 다른 문자열을 준 경우 대비
        alert("처리 결과: " + text);
        return;
      }

      view.textContent = nick;
      cancelNickEdit();
      alert("닉네임이 변경되었습니다.");
    })
    .catch(showReqError);
}


/* ===== 이메일 편집 ===== */
function startEmailEdit() {
  const emailView = document.getElementById("emailView");
  const area = document.getElementById("emailEditArea");
  if (!emailView || !area) return;

  const cur = (emailView.textContent || "").trim();
  let idPart = "", domainPart = "";
  if (cur.includes("@")) {
    const parts = cur.split("@");
    idPart = parts[0] || "";
    domainPart = parts[1] || "";
  }

  document.getElementById("emailId").value = idPart;

  const domainSel = document.getElementById("emailDomain");
  const custom = document.getElementById("customDomain");

  let found = false;
  for (let i = 0; i < domainSel.options.length; i++) {
    if (domainSel.options[i].value === domainPart) {
      domainSel.value = domainPart;
      found = true;
      break;
    }
  }
  if (!found) {
    domainSel.value = "custom";
    custom.style.display = "block";
    custom.value = domainPart;
  } else {
    custom.style.display = "none";
    custom.value = "";
  }

  area.style.display = "flex";
}

function cancelEmailEdit() {
  const area = document.getElementById("emailEditArea");
  if (area) area.style.display = "none";
}

function toggleDomainInput() {
  const domainEl = document.getElementById("emailDomain");
  const custom = document.getElementById("customDomain");
  if (!domainEl || !custom) return;

  if (domainEl.value === "custom") {
    custom.style.display = "block";
    custom.focus();
  } else {
    custom.style.display = "none";
    custom.value = "";
  }
}

function saveEmail() {
  const emailId = (document.getElementById("emailId").value || "").trim();
  const domainSel = document.getElementById("emailDomain").value;
  const customDomain = (document.getElementById("customDomain").value || "").trim();
  const domain = (domainSel === "custom") ? customDomain : domainSel;

  const full = (emailId && domain) ? (emailId + "@" + domain) : "";
  if (!full || !full.includes("@")) {
    alert("이메일을 올바르게 입력하세요.");
    return;
  }


  fetch(ctx + "/member/updateProfile", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "email=" + encodeURIComponent(full)
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok) {
        if (text === "NOLOGIN") {
          alert("로그인이 필요합니다.");
          location.href = ctx + "/member/login/loginForm2.jsp";
          return;
        }
        alert("이메일 변경에 실패했습니다. 잠시 후 다시 시도해주세요.");
        return;
      }

      // OK면 화면 반영
      document.getElementById("emailView").textContent = full;
      cancelEmailEdit();
      alert("이메일이 변경되었습니다.");
    })
    .catch(showReqError);
}

/* =========================
   비밀번호 변경 모달 (복붙용)
   - endpoint: ctx + "/member/changePw"
   - mode=check : 현재비번 검증만
   - mode=change: 현재비번 검증 + 새비번 변경
   ========================= */

function openPwModal() {
  const modal = document.getElementById("pwModal");
  const step1 = document.getElementById("pwStep1");
  const step2 = document.getElementById("pwStep2");

  if (!modal || !step1 || !step2) return;

  modal.style.display = "block";
  step1.style.display = "block";
  step2.style.display = "none";

  const curPw = document.getElementById("curPw");
  const newPw = document.getElementById("newPw");
  const newPw2 = document.getElementById("newPw2");
  const msg1 = document.getElementById("pwMsg1");
  const msg2 = document.getElementById("pwMsg2");

  if (curPw) curPw.value = "";
  if (newPw) newPw.value = "";
  if (newPw2) newPw2.value = "";
  if (msg1) msg1.textContent = "";
  if (msg2) msg2.textContent = "";

  if (curPw) curPw.focus();
}

function closePwModal() {
  const modal = document.getElementById("pwModal");
  if (modal) modal.style.display = "none";
}

function backToStep1() {
  const step1 = document.getElementById("pwStep1");
  const step2 = document.getElementById("pwStep2");
  const msg2 = document.getElementById("pwMsg2");
  const curPw = document.getElementById("curPw");

  if (msg2) msg2.textContent = "";
  if (step2) step2.style.display = "none";
  if (step1) step1.style.display = "block";
  if (curPw) curPw.focus();
}

/* 1단계: 현재 비번 검증 */
function verifyCurrentPw() { 
  const curPw = (document.getElementById("curPw").value || "").trim();
  const msg = document.getElementById("pwMsg1");
  if (msg) msg.textContent = "";

  if (!curPw) {
    if (msg) msg.textContent = "현재 비밀번호를 입력하세요.";
    return;
  }

  fetch(ctx + "/member/changePw", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "mode=check&currentPw=" + encodeURIComponent(curPw)
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok) {
        if (msg) msg.textContent = "요청 실패: " + text;
        return;
      }

      if (text === "NOLOGIN") {
        alert("로그인이 필요합니다.");
        location.href = ctx + "/member/login/loginForm2.jsp";
        return;
      }

      if (text === "OK") {
        document.getElementById("pwStep1").style.display = "none";
        document.getElementById("pwStep2").style.display = "block";
        document.getElementById("newPw").focus();
        return;
      }

      if (text === "WRONG") {
        if (msg) msg.textContent = "현재 비밀번호가 일치하지 않습니다.";
        return;
      }

      if (msg) msg.textContent = "처리 결과: " + text;
    })
    .catch(() => {
      if (msg) msg.textContent = "요청 처리 중 오류가 발생했습니다.";
    });
}

/* 2단계: 새 비번 변경 (현재비번/새비번/새비번확인 3개 전송) */
function changePassword() {
  const curPw = (document.getElementById("curPw").value || "").trim();
  const newPw = (document.getElementById("newPw").value || "").trim();
  const newPw2 = (document.getElementById("newPw2").value || "").trim();
  const msg = document.getElementById("pwMsg2");
  if (msg) msg.textContent = "";

  if (!curPw) {
    if (msg) msg.textContent = "현재 비밀번호가 비어있습니다. 뒤로 가서 다시 확인하세요.";
    return;
  }

  // 미니프로젝트용 룰
  if (newPw.length < 4 || newPw.length > 20) {
    if (msg) msg.textContent = "새 비밀번호는 4~20자로 입력하세요.";
    return;
  }

  if (newPw !== newPw2) {
    if (msg) msg.textContent = "새 비밀번호 확인이 일치하지 않습니다.";
    return;
  }

  fetch(ctx + "/member/changePw", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body:
      "mode=change" +
      "&currentPw=" + encodeURIComponent(curPw) +
      "&newPw=" + encodeURIComponent(newPw) +
      "&newPw2=" + encodeURIComponent(newPw2)
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok) {
        if (msg) msg.textContent = "요청 실패: " + text;
        return;
      }

      if (text === "NOLOGIN") {
        alert("로그인이 필요합니다.");
        location.href = ctx + "/member/login/loginForm2.jsp";
        return;
      }

      if (text === "WRONG") {
        if (msg) msg.textContent = "현재 비밀번호가 일치하지 않습니다.";
        backToStep1();
        return;
      }

      if (text === "INVALID_LEN") {
        if (msg) msg.textContent = "새 비밀번호는 4~20자로 입력하세요.";
        return;
      }

      if (text === "MISMATCH") {
        if (msg) msg.textContent = "새 비밀번호 확인이 일치하지 않습니다.";
        return;
      }

      if (text === "OK") {
        alert("비밀번호가 변경되었습니다.");
        closePwModal();
        return;
      }

      if (msg) msg.textContent = "처리 결과: " + text;
    })
    .catch(() => {
      if (msg) msg.textContent = "요청 처리 중 오류가 발생했습니다.";
    });
}



// ✅ 왼쪽 '회원 탈퇴' 클릭 시: confirm → 모달 오픈
function openWithdrawFlow() {
  if (!confirm("정말 탈퇴하시겠습니까?")) return;
  openWithdrawModal(); // 너가 이미 만들어둔 모달 오픈 함수
}
function openWithdrawModal(){
  const modal = document.getElementById("withdrawModal");
  const pwEl = document.getElementById("withdrawPw");
  const msg = document.getElementById("withdrawMsg");

  if (!modal || !pwEl || !msg) {
    console.log("withdraw elements missing:", { modal, pwEl, msg });
    alert("탈퇴 모달 요소(id) 확인 필요");
    return;
  }

  pwEl.value = "";
  msg.style.display = "none";
  msg.innerText = "";
  modal.style.display = "block";
  pwEl.focus();
}

function closeWithdrawModal(){
  const modal = document.getElementById("withdrawModal");
  if (modal) modal.style.display = "none";
}
function withdrawConfirm() {
  const pw = (document.getElementById("withdrawPw").value || "").trim();
  const msg = document.getElementById("withdrawMsg");

  if (!pw) {
    msg.style.display = "block";
    msg.innerText = "비밀번호를 입력해주세요.";
    return;
  }

  const body = new URLSearchParams();
  body.append("passwd", pw);

  fetch(ctx + "/member/delete", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
    body: body.toString()
  })
  .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
  .then(({ ok, text }) => {
    if (!ok) {
      msg.style.display = "block";
      msg.innerText = "탈퇴 처리 중 오류가 발생했습니다.";
      return;
    }

    if (text === "NOLOGIN") {
      alert("로그인이 필요합니다.");
      location.href = ctx + "/member/login/loginForm2.jsp";
      return;
    }
    if (text === "EMPTY_PW") {
      msg.style.display = "block";
      msg.innerText = "비밀번호를 입력해주세요.";
      return;
    }
    if (text === "WRONG_PW") {
      msg.style.display = "block";
      msg.innerText = "비밀번호가 일치하지 않습니다.";
      return;
    }
    if (text === "OK") {
      alert("탈퇴 처리되었습니다.");
      location.href = ctx + "/main";
      return;
    }

    msg.style.display = "block";
    msg.innerText = "처리 실패: " + text;
  })
  .catch(() => {
    msg.style.display = "block";
    msg.innerText = "요청 중 오류가 발생했습니다.";
  });
}


/* ===== 기자 신청 ===== */
function applyJournal() {
  const reason = (document.getElementById("reason").value || "").trim();
  if (!reason) {
    alert("신청 사유를 입력하세요.");
    return;
  }
  if (!confirm("신청하시겠습니까?")) return;

  fetch(ctx + "/member/reporterApply.do", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "reason=" + encodeURIComponent(reason)
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok) {
        alert("신청 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return;
      }

      // 로그인 필요
      if (text === "NOLOGIN") {
        alert("로그인이 필요합니다.");
        location.href = ctx + "/member/login/loginForm2.jsp";
        return;
      }

      // 7일 제한(서버가 TOOEARLY 주면)
      if (text === "TOOEARLY") {
        alert("가입 후 7일이 지나야 신청할 수 있습니다.");
        return;
      }

      // 이미 신청중/기자
      if (text === "ALREADY_APPLIED" || text === "ALREADY_REPORTER") {
        alert("이미 신청 처리 중이거나 기자 회원입니다.");
        return;
      }
	  
	  if (text === "ON_HOLD") {
	    alert("현재 보류 상태입니다. 관리자 처리 후 다시 시도해주세요.");
	    return;
	  }


      // 신청 성공
      if (text === "APPLY_OK") {
        const box = document.getElementById("applyResult");
        if (box) {
          box.textContent = `신청이 접수되었습니다.
3~5일 정도 소요됩니다.
기자 등급 심사중...`;
        }
        setTimeout(() => location.reload(), 300);
        return;
      }

      alert("처리 결과: " + text);
    })
    .catch(showReqError);
}

/* ===== 기자 신청 취소 ===== */
function cancelJournal() {
  if (!confirm("취소하겠습니까?")) return;

  fetch(ctx + "/member/reporterApply.do", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "mode=cancel"
  })
    .then(res => res.text().then(t => ({ ok: res.ok, text: (t || "").trim() })))
    .then(({ ok, text }) => {
      if (!ok) {
        alert("취소 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return;
      }

      if (text === "NOLOGIN") {
        alert("로그인이 필요합니다.");
        location.href = ctx + "/member/login/loginForm2.jsp";
        return;
      }

      if (text === "CANCEL_OK") {
        alert("기자 신청이 취소되었습니다.");
        location.reload();
        return;
      }

      alert("처리 결과: " + text);
    })
    .catch(showReqError);
}
/* =========================================================
   기록: 좋아요 / 작성글 모달 + 프론트 페이징 (통합본)
   ========================================================= */

// 팀원 게시글 상세로 이동 경로 (ctx가 /mini_project 라면 자동으로 맞음)
const articleViewUrl = ctx + "/articleboard/article/articleView.jsp";

// 페이지 사이즈
const LIKED_PAGE_SIZE = 10;
const MY_ARTICLES_PAGE_SIZE = 10;

// 데이터 보관
let likedAll = [];
let likedPage = 1;

let myArticlesAll = [];
let myArticlesPage = 1;

// DOM 로드 후 클릭 이벤트 연결
document.addEventListener("DOMContentLoaded", () => {
  const btnLiked = document.getElementById("btnLiked");
  const btnMyArticles = document.getElementById("btnMyArticles");

  if (btnLiked) btnLiked.addEventListener("click", openLikedModal);
  if (btnMyArticles) btnMyArticles.addEventListener("click", openMyArticlesModal);
});

/* =========================
   좋아요 모달 열기
   ========================= */
function openLikedModal() {
  const modal = document.getElementById("likedModal");
  const tbody = document.getElementById("likedTbody");
  const empty = document.getElementById("likedEmpty");
  const pager = document.getElementById("likedPager");

  if (!modal || !tbody || !empty || !pager) {
    alert("좋아요 모달 요소(id) 확인 필요");
    return;
  }

  modal.style.display = "block";
  tbody.innerHTML = "";
  empty.style.display = "none";
  pager.innerHTML = "";

  fetch(ctx + "/member/likedArticles", { method: "GET" })
    .then(res => res.text().then(t => ({ ok: res.ok, status: res.status, text: (t || "").trim() })))
    .then(({ ok, status, text }) => {
      if (!ok) {
        if (text === "NOLOGIN" || status === 401) {
          alert("로그인이 필요합니다.");
          location.href = ctx + "/member/login/loginForm2.jsp";
          return;
        }
		console.log("좋아요 목록 조회 실패:", status, text);
		      likedAll = [];
		      likedPage = 1;
		      renderLikedPage(1);
		      return;
      }

      let data = [];
      try { data = text ? JSON.parse(text) : []; }
      catch (e) {
        console.log("liked JSON parse error:", e, text);
        alert("응답 형식 오류(JSON). 콘솔 확인");
        return;
      }

      likedAll = data || [];
      likedPage = 1;
      renderLikedPage(1);
    })
    .catch(showReqError);
}

function closeLikedModal() {
  const modal = document.getElementById("likedModal");
  if (modal) modal.style.display = "none";
}

/* =========================
   좋아요 페이지 렌더
   ========================= */
function renderLikedPage(page) {
  const tbody = document.getElementById("likedTbody");
  const empty = document.getElementById("likedEmpty");
  const pager = document.getElementById("likedPager");
  if (!tbody || !empty || !pager) return;

  const total = likedAll.length;
  const pageCount = Math.max(1, Math.ceil(total / LIKED_PAGE_SIZE));
  likedPage = Math.min(Math.max(1, page), pageCount);

  tbody.innerHTML = "";
  empty.style.display = (total === 0) ? "block" : "none";

  if (total === 0) {
    pager.innerHTML = "";
    return;
  }

  const start = (likedPage - 1) * LIKED_PAGE_SIZE;
  const end = Math.min(start + LIKED_PAGE_SIZE, total);
  const slice = likedAll.slice(start, end);

  slice.forEach(row => {
    const tr = document.createElement("tr");
    tr.style.cursor = "pointer";

    tr.innerHTML = `
      <td class="col-article">${row.articleNo}</td>
      <td class="col-writer">${escapeHtml(row.memberId)}</td>
      <td class="td-subject">${escapeHtml(row.subject)}</td>
	  <td class="col-date td-date">${escapeHtml(formatDateOnly(row.regDt))}</td>

    `;

    tr.addEventListener("click", () => {
      location.href = articleViewUrl + "?articleNo=" + encodeURIComponent(row.articleNo);
    });

    tbody.appendChild(tr);
  });

  renderPager(pager, likedPage, pageCount, (p) => renderLikedPage(p));
}

/* =========================
   작성글 모달 열기
   ========================= */
function openMyArticlesModal() {
  // 프론트 1차 차단 (서버도 403 처리)
  if (memberStatCd !== "2" && memberStatCd !== "3") {
    alert("기자 등급만 확인할 수 있습니다.");
    return;
  }

  const modal = document.getElementById("myArticlesModal");
  const tbody = document.getElementById("myArticlesTbody");
  const empty = document.getElementById("myArticlesEmpty");
  const pager = document.getElementById("myArticlesPager");

  if (!modal || !tbody || !empty || !pager) {
    alert("작성글 모달 요소(id) 확인 필요");
    return;
  }

  modal.style.display = "block";
  tbody.innerHTML = "";
  empty.style.display = "none";
  pager.innerHTML = "";

  fetch(ctx + "/member/myArticles", { method: "GET" })
    .then(res => res.text().then(t => ({ ok: res.ok, status: res.status, text: (t || "").trim() })))
    .then(({ ok, status, text }) => {
      if (!ok) {
        if (text === "NOLOGIN" || status === 401) {
          alert("로그인이 필요합니다.");
          location.href = ctx + "/member/login/loginForm2.jsp";
          return;
        }
        if (text === "FORBIDDEN" || status === 403) {
          alert("기자 등급만 확인할 수 있습니다.");
          return;
        }
		console.log("작성글 목록 조회 실패:", status, text);
		      myArticlesAll = [];
		      myArticlesPage = 1;
		      renderMyArticlesPage(1);  // "작성한 글이 없습니다." 띄움
		      return;
      }

      let data = [];
      try { data = text ? JSON.parse(text) : []; }
      catch (e) {
        console.log("myArticles JSON parse error:", e, text);
        alert("응답 형식 오류(JSON). 콘솔 확인");
        return;
      }

      myArticlesAll = data || [];
      myArticlesPage = 1;
      renderMyArticlesPage(1);
    })
    .catch(showReqError);
}

function closeMyArticlesModal() {
  const modal = document.getElementById("myArticlesModal");
  if (modal) modal.style.display = "none";
}

/* =========================
   작성글 페이지 렌더
   ========================= */
function renderMyArticlesPage(page) {
  const tbody = document.getElementById("myArticlesTbody");
  const empty = document.getElementById("myArticlesEmpty");
  const pager = document.getElementById("myArticlesPager");
  if (!tbody || !empty || !pager) return;

  const total = myArticlesAll.length;
  const pageCount = Math.max(1, Math.ceil(total / MY_ARTICLES_PAGE_SIZE));
  myArticlesPage = Math.min(Math.max(1, page), pageCount);

  tbody.innerHTML = "";
  empty.style.display = (total === 0) ? "block" : "none";

  if (total === 0) {
    pager.innerHTML = "";
    return;
  }

  const start = (myArticlesPage - 1) * MY_ARTICLES_PAGE_SIZE;
  const end = Math.min(start + MY_ARTICLES_PAGE_SIZE, total);
  const slice = myArticlesAll.slice(start, end);

  slice.forEach(row => {
    const tr = document.createElement("tr");
    tr.style.cursor = "pointer";

	tr.innerHTML = `
	  <td class="col-article">${row.articleNo}</td>
	  <td class="td-subject">${escapeHtml(row.subject)}</td>
	  <td class="col-date td-date">${escapeHtml(formatDateOnly(row.regDt))}</td>
	  <td class="col-cnt">${row.cnt != null ? row.cnt : ""}</td>
	`;


    tr.addEventListener("click", () => {
      location.href = articleViewUrl + "?articleNo=" + encodeURIComponent(row.articleNo);
    });

    tbody.appendChild(tr);
  });

  renderPager(pager, myArticlesPage, pageCount, (p) => renderMyArticlesPage(p));
}

/* =========================
   공용 페이저 렌더
   ========================= */
function renderPager(container, current, totalPages, onMove) {
  container.innerHTML = "";

  const mkBtn = (label, page, disabled = false, active = false) => {
    const b = document.createElement("button");
    b.type = "button";
    b.textContent = label;
    b.disabled = disabled;

    b.style.padding = "6px 10px";
    b.style.border = "1px solid #ddd";
    b.style.borderRadius = "8px";
    b.style.background = active ? "#111" : "#fff";
    b.style.color = active ? "#fff" : "#111";
    b.style.cursor = disabled ? "not-allowed" : "pointer";

    if (!disabled) b.addEventListener("click", () => onMove(page));
    return b;
  };

  container.appendChild(mkBtn("이전", current - 1, current === 1));

  let start = Math.max(1, current - 2);
  let end = Math.min(totalPages, start + 4);
  start = Math.max(1, end - 4);

  if (start > 1) {
    container.appendChild(mkBtn("1", 1, false, current === 1));
    if (start > 2) {
      const dots = document.createElement("span");
      dots.textContent = "...";
      dots.style.padding = "6px 6px";
      container.appendChild(dots);
    }
  }

  for (let p = start; p <= end; p++) {
    container.appendChild(mkBtn(String(p), p, false, p === current));
  }

  if (end < totalPages) {
    if (end < totalPages - 1) {
      const dots = document.createElement("span");
      dots.textContent = "...";
      dots.style.padding = "6px 6px";
      container.appendChild(dots);
    }
    container.appendChild(mkBtn(String(totalPages), totalPages, false, current === totalPages));
  }

  container.appendChild(mkBtn("다음", current + 1, current === totalPages));
}

/* =========================
   XSS 방지
   ========================= */
function escapeHtml(s) {
  return String(s != null ? s : "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatDateOnly(v) {
  if (!v) return "";
  const s = String(v).trim();

  // 1) "2026-02-12 09:47:10.0" / "2026-02-12T09:47:10" 같은 경우
  const m1 = s.match(/^(\d{4}-\d{2}-\d{2})/);
  if (m1) return m1[1];

  // 2) "20260212" 같은 경우
  const m2 = s.match(/^(\d{4})(\d{2})(\d{2})$/);
  if (m2) return `${m2[1]}-${m2[2]}-${m2[3]}`;

  return s; // 못 맞추면 원본
}

