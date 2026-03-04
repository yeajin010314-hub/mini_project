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
import dto.CategoryDTO;
import dto.MemberBasDTO;
import service.AdminCategoryServiceImpl;
import service.AdminDashboardServiceImpl;
import service.AdminJournalRequestServiceImpl;
import service.AdminMemberServiceImpl;

public class MemberBasStatsAction implements Action {

//	private final MemberService service;

	private final AdminDashboardServiceImpl 	 dashboardService;
	private final AdminMemberServiceImpl         memberService;
	private final AdminCategoryServiceImpl 		 categoryService;
	private final AdminJournalRequestServiceImpl JournalReqService;

	// 생성자
	public MemberBasStatsAction() {
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
		
		// 1. Service 호출
		// =================[관리자 대시보드] START =================
        // 회원 통계(record DTO사용)
		List<AdminMemberStatsDTO> statsList = dashboardService.getMemberStatsList();
        
		// 기자 등록요청 통계 조회(record DTO사용)
		List<AdminMemberStatsDTO> reqReportStat = dashboardService.getReqReportStatList();
		
		// 카테고리 통계 조회(record DTO사용)
		List<AdminMemberStatsDTO> categoryStat = dashboardService.getCategoryStatList();
		// =================[관리자 대시보드]  END  =================


	    // ================== 페이징 설정(공통) ==================
	    int pageSize = 10;		// 한 페이지에 표시할 회원 수
        int pageBlock = 10;		// 한 블록에 표시할 페이지 수

        // ================== 회원관리 페이지 번호 계산 START ==================
        // get으로 받아온 페이지번호
	    String memberPageNum = request.getParameter("memberPageNum");
	    // 현재 페이지
	    int memberCurrentPage = (memberPageNum == null) ? 1 : Integer.parseInt(memberPageNum);

		// 전체 회원수
	    int memberAllCount = memberService.getMemberTotalCount();
	    
	    // 관리자(00001)정보 조회한다.
	    MemberBasDTO member = memberService.getMemberBas("00001");
	    System.out.println("==========member : [" + member + "]=======");
        // ================== 페이징 계산 ==================
        // 전체 페이지 수
        int memberPageCount = memberAllCount / pageSize + (memberAllCount % pageSize == 0 ? 0 : 1);

        // 현재 블록 시작/끝 페이지 계산
        int memberStartPage = (memberCurrentPage - 1) / pageBlock * pageBlock + 1;
        int memberEndPage = memberStartPage + pageBlock - 1;

        if (memberEndPage > memberPageCount) {
        	memberEndPage = memberPageCount;
        }        
		// ================== 회원관리 페이지 번호 계산  END  ==================

        // 회원 전체 가져오기
        List<MemberBasDTO> memberList = memberService.getFindAll(memberCurrentPage, pageSize);

        // ================== 카테고리 페이지 번호 계산 START ==================
        // get으로 받아온 페이지번호
	    String categoryPageNum = request.getParameter("categoryPageNum");
	    // 현재 페이지
	    int categoryCurrentPage = (categoryPageNum == null) ? 1 : Integer.parseInt(categoryPageNum);

		// 전체 카테고리 건수 조회
	    int categoryAllCount = categoryService.getCategoryTotalCount();

        // ================== 페이징 계산 ==================
        // 전체 페이지 수
        int categoryPageCount = categoryAllCount / pageSize + (categoryAllCount % pageSize == 0 ? 0 : 1);

        // 현재 블록 시작/끝 페이지 계산
        int categoryStartPage = (categoryCurrentPage - 1) / pageBlock * pageBlock + 1;
        int categoryEndPage = categoryStartPage + pageBlock - 1;

        if (categoryEndPage > categoryPageCount) {
        	categoryEndPage = categoryPageCount;
        }        
		// ================== 카테고리 페이지 번호 계산  END  ==================

        // Category List 조회(일반 DTO용)
        List<CategoryDTO> categoryList = categoryService.getCategoryList(categoryCurrentPage, pageSize);
        

        
        // ================== 카테고리 페이지 번호 계산 START ==================
        // get으로 받아온 페이지번호
	    String journalPageNum = request.getParameter("journalPageNum");
	    // 현재 페이지
	    int journalCurrentPage = (journalPageNum == null) ? 1 : Integer.parseInt(journalPageNum);

		// 전체 카테고리 건수 조회
	    int journalAllCount = JournalReqService.getJournalRegTotalCount();

        // ================== 페이징 계산 ==================
        // 전체 페이지 수
        int journalPageCount = journalAllCount / pageSize + (journalAllCount % pageSize == 0 ? 0 : 1);

        // 현재 블록 시작/끝 페이지 계산
        int journalStartPage = (journalCurrentPage - 1) / pageBlock * pageBlock + 1;
        int journalEndPage = journalStartPage + pageBlock - 1;

        if (journalEndPage > journalPageCount) {
        	journalEndPage = journalPageCount;
        }        
		// ================== 카테고리 페이지 번호 계산  END  ==================
        
        // 기자 등록요청 조회
        List<MemberBasDTO> journalRegReqList = JournalReqService.getJournalRegRequestList(journalCurrentPage, pageSize);
        
        
		// ================== 공통코드 START ==================
        // 상태코드 : JOURNAL_REASON_CD
        List<AdminMemberStatsDTO> jourReasonCd = dashboardService.getStatByCode("JOURNAL_REASON_CD");
        
        // 상태코드 : MEMBER_STAT_CD
        List<AdminMemberStatsDTO> memberStatCd = dashboardService.getStatByCode("MEMBER_STAT_CD");
		// ================== 공통코드  END  ==================
        
        
        // ================== JSP로 전달 ==================
        // 2. JSP로 데이터 전달
        // 회원 통계
        request.setAttribute("statsList", statsList);
        // 기자 등록요청 통계 조회
        request.setAttribute("reqReportStat", reqReportStat);
        // 카테고리 통계 조회
        request.setAttribute("categoryStat", categoryStat);

        // 회원 전체
        request.setAttribute("memberList", memberList);
        
        // 회원 전체
        request.setAttribute("member", member);
        
        // 회원관리 페이징 정보
        System.out.println("================================================================");
        System.out.println("페이징 정보 : memberAllCount : " + memberAllCount);
        System.out.println("페이징 정보 : memberCurrentPage : " + memberCurrentPage);
        System.out.println("페이징 정보 : memberPageCount : " + memberPageCount);
        System.out.println("페이징 정보 : memberStartPage : " + memberStartPage);
        System.out.println("페이징 정보 : memberEndPage : " + memberEndPage);
        
        request.setAttribute("memberAllCount", memberAllCount);
        request.setAttribute("memberCurrentPage", memberCurrentPage);
        request.setAttribute("memberPageCount", memberPageCount);
        request.setAttribute("memberStartPage", memberStartPage);
        request.setAttribute("memberEndPage", memberEndPage);
//        request.setAttribute("pageBlock", pageBlock);
        

        // 카테고리정보 페이징 정보
        System.out.println("================================================================");
        System.out.println("페이징 정보 : categoryAllCount : " + categoryAllCount);
        System.out.println("페이징 정보 : categoryCurrentPage : " + categoryCurrentPage);
        System.out.println("페이징 정보 : categoryPageCount : " + categoryPageCount);
        System.out.println("페이징 정보 : categoryStartPage : " + categoryStartPage);
        System.out.println("페이징 정보 : categoryEndPage : " + categoryEndPage);
        
        request.setAttribute("categoryAllCount", categoryAllCount);
        request.setAttribute("categoryCurrentPage", categoryCurrentPage);
        request.setAttribute("categoryPageCount", categoryPageCount);
        request.setAttribute("categoryStartPage", categoryStartPage);
        request.setAttribute("categoryEndPage", categoryEndPage);
        
        // 기자등록 요청 정보 페이징 정보
        System.out.println("================================================================");
        System.out.println("페이징 정보 : journalAllCount : " + journalAllCount);
        System.out.println("페이징 정보 : journalCurrentPage : " + journalCurrentPage);
        System.out.println("페이징 정보 : journalPageCount : " + journalPageCount);
        System.out.println("페이징 정보 : journalStartPage : " + journalStartPage);
        System.out.println("페이징 정보 : journalEndPage : " + journalEndPage);
        
        request.setAttribute("journalAllCount", journalAllCount);
        request.setAttribute("journalCurrentPage", journalCurrentPage);
        request.setAttribute("journalPageCount", journalPageCount);
        request.setAttribute("journalStartPage", journalStartPage);
        request.setAttribute("journalEndPage", journalEndPage);
        

        // Category List 조회(일반 DTO용)
        request.setAttribute("categoryList", categoryList);

        // 상태코드
        request.setAttribute("jourReasonCd", jourReasonCd);
        request.setAttribute("memberStatCd", memberStatCd);
        
        // 기자 등록요청 조회
        request.setAttribute("journalRegReqList", journalRegReqList);
        
//        request.setAttribute("requestStats", requestStats); // 등록 요청 통계
//        request.setAttribute("categoryCount", categoryCount); // 카테고리 개수

        // ===========================================================================
        String memberId = request.getParameter("memberId");
        // request.setAttribute("message", "회원 " + memberId + " Role Change 완료");
        
        System.out.println("============== message ==============");
        System.out.println("회원 " + memberId + " Role Change 완료");

        // 조회는 forward, 처리(등록/수정/삭제)는 redirect를 사용해야함.
        // forward
        return "/dashboard/dashBoard.jsp";
	}

}
