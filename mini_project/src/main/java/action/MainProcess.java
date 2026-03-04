package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.AdminCategoryDAO;
import dao.AdminMemberDAO;
import dao.MainArticleDAO;
import dto.ArticleDetailViewDTO;
import dto.CategoryDTO;
import dto.MemberBasDTO;
import service.AdminCategoryServiceImpl;
import service.AdminMemberServiceImpl;
import service.ArticleServiceImpl;

public class MainProcess implements Action {

	private final AdminCategoryServiceImpl 		categoryService;
	private final ArticleServiceImpl			articleService;
	private final AdminMemberServiceImpl        memberService;

	// 생성자
	public MainProcess() {
        AdminCategoryDAO categoryDAO = new AdminCategoryDAO();
        this.categoryService = new AdminCategoryServiceImpl(categoryDAO);

        MainArticleDAO mainArticleDAO = new MainArticleDAO();
        this.articleService = new ArticleServiceImpl(mainArticleDAO);

        AdminMemberDAO memberDAO = new AdminMemberDAO();
        this.memberService = new AdminMemberServiceImpl(memberDAO);

	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// ===============================================================
		// 1. Service 호출
		// ===============================================================

		// =================[MAIN Servlet] START =================
		System.out.println("=========== MAIN Servlet ============");
		MemberBasDTO member = null;
		
		// ---------------------------------------------------------
		// 로그인 session이 있는지 체크
		// ---------------------------------------------------------
		HttpSession session = request.getSession();
        System.out.println("session : [" + session + "]");

        if (session != null) {
            member = (MemberBasDTO) session.getAttribute("member");
        }

		// ---------------------------------------------------------
		// 관리자(00001)정보 조회한다.(임시 로그인 테스트용)
		// ---------------------------------------------------------
//        member = memberService.getMemberBas("00001");
	    System.out.println("member============ : [" + member + "]");
	    
		// ---------------------------------------------------------
		// 카드 Session 조회
		// ---------------------------------------------------------
        // Main 카드 Session 여행추천 1개
        ArticleDetailViewDTO articleDetailViewTravelView = articleService.getMainTopArticlesByCategoryGroup("1");
	    
        // Main 카드 Session 맛집추천 1개
        ArticleDetailViewDTO articleDetailViewFoodView = articleService.getMainTopArticlesByCategoryGroup("2");

	    // Sub 카드 Session 3개(전체 카테고리중 상위 3개 추출)
        List<ArticleDetailViewDTO> articleSubView = articleService.getArticleDetailView("00000", 3);

	    // 전체 카테고리 리스트 검색
        List<CategoryDTO> categoryTreeList = categoryService.getCategoryTreeList();

        // 카테고리 4개 Session
        String categoryId = request.getParameter("categoryId");
        System.out.println("categoryId : [" + categoryId + "]");
        
        // 값이 안넘어올 경우는 Default 1로 셋팅함.
        String rowLimitParam = request.getParameter("rowLimit");
        int    rowLimit   = rowLimitParam != null ? Integer.parseInt(rowLimitParam) : 1;
        
        System.out.println("rowLimit : [" + rowLimit + "]");

        List<ArticleDetailViewDTO> articleView = articleService.getArticleDetailView(categoryId, rowLimit);


		// ===============================================================
        // 2. JSP로 데이터 전달
		// ===============================================================
        
        // ================== JSP로 전달 ==================
        // View로 전달
        request.setAttribute("loginUser", member);
        request.setAttribute("articleDetailViewTravelView", articleDetailViewTravelView);
        request.setAttribute("articleDetailViewFoodView", articleDetailViewFoodView);
        request.setAttribute("categoryTreeList", categoryTreeList);
        request.setAttribute("articleView", articleView);
        request.setAttribute("articleSubView", articleSubView);
        session.setAttribute("member", member);


        // ===============================================================
        // 3. return(view)
        // 조회는 forward, 처리(등록/수정/삭제)는 redirect를 사용해야함.
		// ===============================================================
        
        // forward
		return "/main/main.jsp";
	}

}
