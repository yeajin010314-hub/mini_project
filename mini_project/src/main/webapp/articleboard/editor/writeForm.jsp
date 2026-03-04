<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dto.ArticleDTO" %>
<%@ page import="dto.ArticleDtlDTO"%>
<%@ page import="dao.ArticleDtlDAO"%>
<%@ page import="dao.ArticleDAO"%>
<%@ page import="dao.MypageDAO"%>
<%@ page import="dao.CategoryDAO"%>
<%@ page import="java.util.*" %>

<%
	String memberId = (String)session.getAttribute("memberId");
	ArticleDAO dao = new ArticleDAO();
	ArticleDtlDAO dtdao = new ArticleDtlDAO();
	MypageDAO mdao = MypageDAO.getInstance();
	CategoryDAO cdao = new CategoryDAO();
	
	String tempNoStr = request.getParameter("temp_no");
	String articleNoStr = request.getParameter("articleNo");
	ArticleDTO dto = null;
	ArticleDtlDTO dtl = null;
	
	String memberStatCd = mdao.getMemberStatCd(memberId);
	if(!"2".equals(memberStatCd) && !"3".equals(memberStatCd)){
%>	
	<script>
		alert("접근 권한이 없습니다.");
		history.go(-1);
	</script>
<% 
	return;
	}

	if (tempNoStr != null && !tempNoStr.equals("")) {
	    int tempNo = Integer.parseInt(tempNoStr);
	    dto = dao.getArticle(tempNo);
	    dtl = dtdao.getArticleDtl(tempNo);
	} else if (articleNoStr != null && !articleNoStr.equals("")) {
	    int articleNo = Integer.parseInt(articleNoStr);
	    dto = dao.getArticle(articleNo);
	    dtl = dtdao.getArticleDtl(articleNo);
	}
	String loaded = request.getParameter("loaded");

	// 대분류
	List<ArticleDTO> parentList = cdao.getCategoryList(memberStatCd);
 
	// 소분류 Map 준비 (대분류ID -> 소분류 리스트)
	Map<String, List<ArticleDTO>> subMap = new HashMap<>();
	for(ArticleDTO parent : parentList) {
	    List<ArticleDTO> subs = cdao.getSubCategoryList(parent.getCategoryId()); // up_category_id 기준
	    subMap.put(parent.getCategoryId(), subs);
	}
%>

<title>글 작성</title>
<script>
// 입력값 검증
function writeCheck(){
    const f = document.forms['f'];
    if(!f.category_id.value){
        alert("카테고리를 입력하세요");
        return false;
    }
    const title = f.subject.value.trim();
    if(!title){
        alert("제목을 입력하세요");
        return false;
    }
    if(title.length < 10 || title.length > 100){
        alert("제목은 10자 이상 100자 이하로 입력하세요");
        return false;
    }
    
    const img1 = f.article_img1.value;
    const img2 = f.article_img2.value;
    const img3 = f.article_img3.value;

    if(!img1 && !img2 && !img3){
        alert("이미지는 최소 1장 이상 등록해야 합니다.");
        return false;
    }

    if(!img1 && (img2 || img3)){
        alert("이미지는 첫 번째 파일(img1)에 최소 1장을 등록해야 합니다.");
        return false;
    }
    
    const content = f.article_content.value.trim();
    if(!content){
        alert("내용을 입력하세요");
        return false;
    }
    if(content.length < 10){
        alert("본문은 최소 10자 이상 입력하세요");
        return false;
    }
    const linkUrl = f.link_url.value.trim();
    const linkText = f.link_text.value.trim();
    if(linkUrl || linkText){
        if(!linkUrl || !linkText){
            alert("링크 주소와 링크 이름을 모두 입력하세요");
            return false;
        }
        const urlPattern = /^https?:\/\/.+/;
        if(!urlPattern.test(linkUrl)){
            alert("링크 주소는 http:// 또는 https:// 로 시작해야 합니다");
            return false;
        }
    }
    const tagStr = f.hash_tag.value.trim();
    if(tagStr){
        const tags = tagStr.split("#").filter(t => t.trim() !== "");
        const lowerTags = tags.map(t => t.trim().toLowerCase());
        const uniqueTags = new Set(lowerTags);
        if(uniqueTags.size !== lowerTags.length){
            alert("태그는 중복 입력이 불가능합니다");
            return false;
        }
        if(tags.length > 3){
            alert("태그는 최대 3개까지 입력 가능합니다");
            return false;
        }
    }
    return true;
}

// 미리보기
function openPreview(){
    const f = document.forms['f'];
    f.action = "preview.jsp";
    f.target = "previewWin";
    window.open("", "previewWin", "width=900,height=700,scrollbars=yes");
    f.submit();
    f.action = "writePro.jsp";
    f.target = "";
}

// 임시저장 목록 새창
function openTempList(){
    window.open("tempList.jsp", "tempListWin", "width=800,height=600,scrollbars=yes");
}

// 임시저장 / 등록 처리
function setStatAndSubmit(stat){
    if(!writeCheck()) return;
    if(stat == 0){
        const ok = confirm("임시저장 누르면 이미지는 임시저장이 되지 않습니다.\n임시저장 하시겠습니까?");
        if(!ok) return;
    }
    if(stat == 1){
        const ok = confirm("등록하시겠습니까?");
        if(!ok) return;
    }
    document.getElementById("article_stat").value = stat;
    if(stat == 0) alert("임시저장되었습니다");
    if(stat == 1) alert("등록되었습니다");
    document.forms['f'].submit();
}

// 글 불러왔을 때 알림
window.onload = function() {
    <% if ("1".equals(loaded)) { %>
        alert("작성하신 게시글을 불러왔습니다");
    <% } %>
}
</script>

<title>글 작성</title>

<link rel="stylesheet"
href="<%=request.getContextPath()%>/resources/css/writeForm.css">

<!-- 상단바 -->
<div style="width:100%; height:50px; background-color:#fff; display:flex; justify-content:space-between; align-items:center; padding:0 20px; box-sizing:border-box; position:fixed; top:0; left:0; z-index:1000; border-bottom:1px solid #ccc;">
    
    <!-- 왼쪽: 로고 -->
    <a href="<%=request.getContextPath()%>/main" 
       style="color:#000; font-size:20px; font-weight:bold; text-decoration:none;">
        TRAVEL+
    </a>
</div>

<!-- 상단바 때문에 콘텐츠 밀림 방지용 여백 -->
<div style="height:50px;"></div>

<form name="f" action="writePro.jsp" method="post" enctype="multipart/form-data" onsubmit="return writeCheck()">
<input type="hidden" name="articleNo" value="<%= dto != null ? dto.getArticleNo() : "" %>">

카테고리 (필수) <br/>
<select name="category_id" id="categorySelect">
    <option value="">--대분류 선택--</option>
    <% 
    for(ArticleDTO parent : parentList) { %>
        <option value="<%= parent.getCategoryId() %>"
            <%= dto != null && parent.getCategoryId().equals(dto.getCategoryId()) ? "selected" : "" %>>
            <%= parent.getCategoryNm() %>
        </option>
    <% } %>
</select><br/><br/>

제목 (필수) <br/>
<input type="text" name="subject" value="<%= dto != null ? dto.getSubject() : "" %>" /><br/><br/>

이미지 (한장은 필수) (순서대로 선택)<br/>
<input type="file" name="article_img1" /><br/>
<input type="file" name="article_img2" /><br/>
<input type="file" name="article_img3" /><br/><br/>

내용 (필수) <br/>
<textarea name="article_content" rows="10" cols="80"><%= dtl != null ? dtl.getArticleContent() : "" %></textarea><br/><br/>

관련 링크 (선택) <br/>
<input type="text" name="link_text"
       value='<%= dtl != null && dtl.getLinkText() != null ? dtl.getLinkText() : "" %>'
       placeholder="링크 이름 (예 : 네이버)" /><br />

<input type="text" name="link_url"
       value='<%= dtl != null && dtl.getLinkUrl() != null ? dtl.getLinkUrl() : "" %>'
       placeholder="https://example.com" /><br/><br/>

태그 (선택) <br/>
<input type="text" name="hash_tag"
       placeholder="#제주도#고기국수"
       value='<%= dto != null && dto.getHashTag() != null ? dto.getHashTag() : "" %>' /><br/><br/>

<input type="hidden" name="article_stat" id="article_stat" value="0" />

<div class="btn-area">

    <!-- 왼쪽 그룹 -->
    <div class="btn-left">
        <input type="button" value="임시저장" onclick="setStatAndSubmit(0)"/>
        <input type="button" value="불러오기" onclick="openTempList()" />
    </div>

    <!-- 오른쪽 그룹 -->
    <div class="btn-right">
        <input type="button" value="돌아가기"
               onclick="location.href='/mini_project/articleboard/board/boardList.jsp'">
        <input type="button" value="미리보기" onclick="openPreview()" />
        <input type="button" value="등록" onclick="setStatAndSubmit(1)" />
    </div>

</div>
</form>

<!-- JS용 소분류 데이터 -->
<script>
// 서버에서 선택된 카테고리값
const selectedCategory = "<%= dto != null && dto.getCategoryId() != null ? dto.getCategoryId() : "" %>";
const subCategories = {
<%
int parentCount = 0;
for(String parentId : subMap.keySet()) {
    List<ArticleDTO> subs = subMap.get(parentId);
    out.print("'" + parentId + "':[");
    for(int i=0;i<subs.size();i++){
        ArticleDTO sub = subs.get(i);
        out.print("{id:'" + sub.getCategoryId() + "', name:'" + sub.getCategoryNm() + "'}");
        if(i != subs.size()-1) out.print(",");
    }
    out.print("]");
    if(parentCount != subMap.size()-1) out.print(",");
    parentCount++;
}
%>
};

const select = document.getElementById("categorySelect");
let previousOptions = [...select.options];
let currentLevel = 'parent';

// 대분류 선택 시 소분류로 교체
select.addEventListener("change", function(){
    const selectedValue = this.value;

    if(selectedValue === "__back__"){
        select.innerHTML = "";
        previousOptions.forEach(opt => select.add(opt));
        currentLevel = 'parent';
        return;
    }

    if(subCategories[selectedValue] && subCategories[selectedValue].length > 0){
        select.innerHTML = "";

        // 대분류로 돌아가기 옵션
        const backOpt = document.createElement("option");
        backOpt.value = "__back__";
        backOpt.text = "<< 대분류 선택";
        select.add(backOpt);

        subCategories[selectedValue].forEach(sub=>{
            const opt = document.createElement("option");
            opt.value = sub.id;
            opt.text = sub.name;
            if(sub.id === selectedCategory){ // 선택값 유지
                opt.selected = true;
            }
            select.add(opt);
        });

        currentLevel = 'sub';
        if(selectedCategory && selectedCategory !== selectedValue){
            select.value = selectedCategory; // 선택값 세팅
        } else {
            select.value = ""; // 초기화
        }
    }
});

// 페이지 로드시, 선택값 복원
window.addEventListener("load", function(){
    if(selectedCategory){
        // 부모인지 자식인지 확인
        let isSub = false;
        for(const parent in subCategories){
            if(subCategories[parent].some(sub=>sub.id === selectedCategory)){
                isSub = true;
                select.value = parent;
                const event = new Event('change');
                select.dispatchEvent(event);
                break;
            }
        }
        if(!isSub){
            select.value = selectedCategory;
        }
    }
});
</script>
