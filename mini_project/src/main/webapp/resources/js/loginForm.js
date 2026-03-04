/**
 * 
 */

function begin() {
    document.getElementById("loginId").focus();
}

function checkIt() {
    if (!loginId.value) {
        alert("아이디를 입력하세요.");
        loginId.focus();
        return false;
    }
    if (!loginPw.value) {
        alert("비밀번호를 입력하세요.");
        loginPw.focus();
        return false;
    }
    return true;
	
}

function openFindModal(which){
  document.getElementById("findModal").style.display = "block";
  switchFindTab(which);
  // 메시지 초기화
  document.getElementById("fi_msg").textContent = "";
  document.getElementById("rp_msg").textContent = "";
}

function closeFindModal(){
  document.getElementById("findModal").style.display = "none";
}

function switchFindTab(which){
  const tabId = document.getElementById("tab_find_id");
  const tabPw = document.getElementById("tab_find_pw");
  const pId = document.getElementById("panel_find_id");
  const pPw = document.getElementById("panel_find_pw");
  const title = document.getElementById("findModalTitle");

  if(which === "id"){
    tabId.classList.add("active");
    tabPw.classList.remove("active");
    pId.style.display = "block";
    pPw.style.display = "none";
    title.textContent = "아이디 찾기";
  }else{
    tabPw.classList.add("active");
    tabId.classList.remove("active");
    pPw.style.display = "block";
    pId.style.display = "none";
    title.textContent = "비밀번호 재설정";
  }
}

function normalizeBirth(v){
  return (v||"").replaceAll("-","").trim(); // 1999-01-01 -> 19990101
}

function setMsg(el, text, ok){
  el.className = "msg " + (ok ? "ok" : "");
  el.textContent = text;
}

/* ========== 아이디 찾기 ========== */
function resetFindId(){
  document.getElementById("fi_name").value = "";
  document.getElementById("fi_birth").value = "";
  document.getElementById("fi_email").value = "";
  document.getElementById("fi_msg").textContent = "";
}

function findId(){
  const name = (document.getElementById("fi_name").value||"").trim();
  const birth = normalizeBirth(document.getElementById("fi_birth").value);
  const email = (document.getElementById("fi_email").value||"").trim();
  const msg = document.getElementById("fi_msg");

  if(!name || !birth || !email){ setMsg(msg,"필수 항목을 모두 입력하세요.",false); return; }
  if(birth.length !== 8){ setMsg(msg,"생년월일은 8자리(YYYYMMDD)로 입력하세요.",false); return; }
  if(!email.includes("@")){ setMsg(msg,"이메일 형식이 올바르지 않습니다.",false); return; }

  fetch(ctx + "/member/findId", {
    method:"POST",
    headers:{"Content-Type":"application/x-www-form-urlencoded"},
    body:"name="+encodeURIComponent(name)+"&birth="+encodeURIComponent(birth)+"&email="+encodeURIComponent(email)
  })
  .then(res => res.text().then(t => ({ok:res.ok, text:(t||"").trim()})))
  .then(({ok, text})=>{
    if(!ok){ setMsg(msg,"요청 실패: "+text,false); return; }
    if(text.startsWith("OK|")){
      const masked = text.split("|")[1] || "";
      setMsg(msg,"아이디는 "+masked+" 입니다.",true);
      return;
    }
    if(text==="NOTFOUND"){ setMsg(msg,"입력 정보가 일치하지 않습니다.",false); return; }
    setMsg(msg,"처리 결과: "+text,false);
  })
  .catch(()=>setMsg(msg,"요청 처리 중 오류가 발생했습니다.",false));
}

/* ========== 비밀번호 재설정 ========== */
function resetFindPw(){
  document.getElementById("rp_id").value = "";
  document.getElementById("rp_email").value = "";
  document.getElementById("rp_pw").value = "";
  document.getElementById("rp_pw2").value = "";
  document.getElementById("rp_msg").textContent = "";
}

function resetPassword(){
  const id = (document.getElementById("rp_id").value||"").trim();
  const email = (document.getElementById("rp_email").value||"").trim();
  const pw = (document.getElementById("rp_pw").value||"").trim();
  const pw2 = (document.getElementById("rp_pw2").value||"").trim();
  const msg = document.getElementById("rp_msg");

  if(!id || !email || !pw || !pw2){ setMsg(msg,"필수 항목을 모두 입력하세요.",false); return; }
  if(!email.includes("@")){ setMsg(msg,"이메일 형식이 올바르지 않습니다.",false); return; }
  if(pw.length < 4 || pw.length > 20){ setMsg(msg,"새 비밀번호는 4~20자입니다.",false); return; }
  if(pw !== pw2){ setMsg(msg,"새 비밀번호 확인이 일치하지 않습니다.",false); return; }

  fetch(ctx + "/member/resetPw", {
    method:"POST",
    headers:{"Content-Type":"application/x-www-form-urlencoded"},
    body:"memberId="+encodeURIComponent(id)+"&email="+encodeURIComponent(email)+"&newPw="+encodeURIComponent(pw)+"&newPw2="+encodeURIComponent(pw2)
  })
  .then(res => res.text().then(t => ({ok:res.ok, text:(t||"").trim()})))
  .then(({ok, text})=>{
    if(!ok){ setMsg(msg,"요청 실패: "+text,false); return; }
    if(text==="OK"){
      setMsg(msg,"비밀번호가 재설정되었습니다. 로그인 해주세요.",true);
      // 선택: 아이디 입력칸에 자동 채워주기
      const loginId = document.getElementById("loginId");
      if(loginId) loginId.value = id;
      return;
    }
    if(text==="NOTFOUND"){ setMsg(msg,"아이디 또는 이메일이 일치하지 않습니다.",false); return; }
    if(text==="INVALID_LEN"){ setMsg(msg,"새 비밀번호는 4~20자입니다.",false); return; }
    if(text==="MISMATCH"){ setMsg(msg,"새 비밀번호 확인이 일치하지 않습니다.",false); return; }
    setMsg(msg,"처리 결과: "+text,false);
  })
  .catch(()=>setMsg(msg,"요청 처리 중 오류가 발생했습니다.",false));
}
