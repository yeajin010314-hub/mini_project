package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AdminCategoryDAO;
import dao.AdminDashboardStatsDAO;
import dao.AdminJournalReqDAO;
import dao.AdminMemberDAO;
import dto.AdminMemberStatsDTO;
import dto.MemberBasDTO;
import service.AdminCategoryServiceImpl;
import service.AdminDashboardServiceImpl;
import service.AdminJournalRequestServiceImpl;
import service.AdminMemberServiceImpl;

public class JourReasonCdUpdateAction implements Action {

	private final AdminDashboardServiceImpl 	 dashboardService;
	private final AdminMemberServiceImpl         memberService;
	private final AdminCategoryServiceImpl 		 categoryService;
	private final AdminJournalRequestServiceImpl JournalReqService;
	
	// 생성자
	public JourReasonCdUpdateAction() {

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
		System.out.println("===== JourReasonCdUpdateAction 호출됨 =====");
		
		System.out.println("journalMemberIds : [" + request.getParameter("journalMemberId") + "]");
		System.out.println("journalReasonCds : [" + request.getParameter("journalReasonCd") + "]");

		
		String [] journalMemberIds = request.getParameterValues("journalMemberId");
		String [] journalReasonCds = request.getParameterValues("journalReasonCd");
		String [] procReasons = request.getParameterValues("procReason");

		List<AdminMemberStatsDTO> journalStat = dashboardService.getStatByCode("JOURNAL_REASON_CD");
		
	    List<AdminMemberStatsDTO> categoryStat = (List<AdminMemberStatsDTO>) request.getAttribute("categoryStat");
	    
		String cdnm = "";	// 코드명
		String cd_val = "";		// 건수

		    	
		if(journalMemberIds == null || journalReasonCds == null || procReasons == null)
		{
		 	request.getSession().setAttribute("journalmessage", "선택된 기자등록 요청자가 없습니다.");
		 	return "redirect:/memberBasStats";
		}

		// 각 row마다 처리
		for (int i = 0; i < journalMemberIds.length; i++) {
			
			MemberBasDTO dto = new MemberBasDTO();
			dto.setMemberId(journalMemberIds[i]);
			dto.setJournalReasonCd(journalReasonCds[i]);
			
			// 처리사유명은 100자까지만 넣는다.
			String reason = procReasons[i];
			if (reason != null && reason.length() > 100) {
			    reason = reason.substring(0, 100);
			}

			dto.setProcReason(reason);
			
			// 처리사유(PROC_REASON)로 대체됨
//			if(journalStat != null) {
//			    for(AdminMemberStatsDTO statCd : journalStat) {
//			        if(statCd.cd_val().equals(journalReasonCds[i])) {
//				        dto.setJournalReasonDesc(statCd.cd_nm() + " 처리완료");
//			        }
//			    }
//			}
	        
			System.out.println("----------------------------------------------");
	        System.out.println("memberid : [" + dto.getMemberId() + "]");
	        System.out.println("journalReasonCds : [" + dto.getJournalReasonCd() + "]");
	        System.out.println("JournalReasonDesc : [" + dto.getJournalReasonDesc() + "]");
	        System.out.println("procReasons : [" + dto.getProcReason() + "]");
	        
            // DB 업데이트
            int journalStatResult = JournalReqService.changeMemberJournalCdStatus(dto);
            System.out.println("journalStatResult : " + journalStatResult);
		}

		System.out.println("journalMemberIds.length : " + journalMemberIds.length);
		System.out.println("===== JourReasonCdUpdateAction 호출됨 =====");

		request.getSession().setAttribute("messageJournal", "기자등록 요청처리 변경 완료");

        // 조회는 forward, 처리(등록/수정/삭제)는 redirect를 사용해야함.
		// redirect 처리
        return "redirect:/memberBasStats";
	}

	
}
