package action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AdminCategoryDAO;
import dao.AdminDashboardStatsDAO;
import dao.AdminJournalReqDAO;
import dao.AdminMemberDAO;
import dto.MemberRoleChange;
import service.AdminCategoryServiceImpl;
import service.AdminDashboardServiceImpl;
import service.AdminJournalRequestServiceImpl;
import service.AdminMemberServiceImpl;

public class MemberBasUpdateAction implements Action {

//	private final MemberService service;

	private final AdminDashboardServiceImpl 	 dashboardService;
	private final AdminMemberServiceImpl         memberService;
	private final AdminCategoryServiceImpl 		 categoryService;
	private final AdminJournalRequestServiceImpl JournalReqService;

	
	// 생성자
	public MemberBasUpdateAction() {
        // DAO 생성 후 Service 생성자에 전달
//        MemberRepository memberRepositoryDao = new MemberRepositoryImpl();
//        this.service = new MemberServiceImpl(memberRepositoryDao);

        
        AdminDashboardStatsDAO dashboardStatsDAO = new AdminDashboardStatsDAO();
        this.dashboardService = new AdminDashboardServiceImpl(dashboardStatsDAO);
        
        AdminMemberDAO memberDAO = new AdminMemberDAO();
        this.memberService = new AdminMemberServiceImpl(memberDAO);
        
        AdminCategoryDAO categoryDAO = new AdminCategoryDAO();
        this.categoryService = new AdminCategoryServiceImpl(categoryDAO);
        
        AdminJournalReqDAO journalReqDAO = new AdminJournalReqDAO();
        this.JournalReqService = new AdminJournalRequestServiceImpl(journalReqDAO);

	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("===== memberBasUpdate 호출됨 =====");


        String[] memberIds = request.getParameterValues("memberId");
        String memberStatCd = request.getParameter("memberStatCd");

//        String memberId = request.getParameter("memberId");
		
		if (memberIds == null || memberStatCd == null || memberStatCd.isEmpty()) {
		 	request.getSession().setAttribute("message", "선택된 회원이 없습니다.");
            return "redirect:/memberBasStats";
        }
	 
		for(String memberId : memberIds) {
			MemberRoleChange dto = new MemberRoleChange(memberId, memberStatCd);

			// update처리
			int roleChgResult = memberService.setMemberRoleChange(dto);
			System.out.println("int roleChgResult  : " + roleChgResult );
			request.setAttribute("message", "회원 " + memberId + " Role Change 완료");
        }

		request.getSession().setAttribute("message", "권한 변경이 완료되었습니다.");
		
        // DB 조회 로직 예제 (생략)
		
        
        System.out.println("============== message ==============");
        System.out.println("변경할 권한 " + memberStatCd + " Role Change 완료");
        
        // 조회는 forward, 처리(등록/수정/삭제)는 redirect를 사용해야함.
        // redirect 처리
        return "redirect:/memberBasStats";
//        return "/dashboard/dashBoard.jsp";
	}

}
