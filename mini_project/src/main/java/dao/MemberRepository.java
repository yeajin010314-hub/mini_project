package dao;

import java.util.List;

import dto.AdminMemberStatsDTO;
import dto.CategoryDTO;
import dto.MemberBasDTO;
import dto.MemberRoleChange;

// 각 DAO분리로 삭제예정
public interface MemberRepository {

	// 1. [=====관리자 대시보드=====]
	// 1.1 회원 현황 통계 조회(record DTO용)
	List<AdminMemberStatsDTO> selectMemberStatsList();
//    List<AdminMemberStatusDTO> findById(String memberId);

	// 1.2 기자 등록요청 통계 조회(record DTO용)
	List<AdminMemberStatsDTO> selectReqReportStatList();

	// 1.3 카테고리 통계 조회(record DTO용)
	List<AdminMemberStatsDTO> selectCategoryStatList();
	
	
	// 2. =====[회원관리=====]
	// 2.1 MemberBas 회원 List 조회(일반 DTO용)
    List<MemberBasDTO> findAll(int pagenum, int pagesize);
    
    // 2.2 전체 회원수 조회
    int selectmemberTotalCount();
    
    // 2.3 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
    int updateMemberRoleChange(MemberRoleChange dto);

    // 2.4 회원관리 신규 회원 추가
    int insertMemberBas(MemberBasDTO dto);
    
    // 3. [=====카테고리 관리=====]
    // 3.1 Category List 조회(일반 DTO용)
    List<CategoryDTO> selectCategoryList(int pagenum, int pagesize);
    
    // 3.2 전체 카테고리 건수 조회
    int selectCategoryTotalCount();

    // 3.3 삭제대상의 하위 카테고리 존재여부 확인
    int selectExistsChildCategory(CategoryDTO dto);

    // 3.3 Category 수정
    int updateCategory(CategoryDTO dto);
    
    // 3.4 Category 등록
    int insertCategory(CategoryDTO dto);
    
    
    // 4. [=====기자 등록 요청관리=====]
    // 4.1 기자 등록요청 조회
    List<MemberBasDTO> selectJournalRegRequestList(int pagenum, int pagesize);

    // 4.2 기자등록 요청 전체 건수 조회
    int selectJournalRegTotalCount();

    // 4.2 기자 등록요청 변경(승인, 반려, 보류)
    
    // 4.3 기자 등록 상태변경
    int updateMemberJournalCdStatus(MemberBasDTO dto);
    
    // 회원 등록/수정/삭제
    void save(MemberBasDTO memberBas);

    void update(MemberBasDTO memberBas);

    void delete(String memberId);
    
    // [카테고리]
    // 카테고리  
    
    // 00 공통코드 조회(record DTO) JOURNAL_REASON_CD, MEMBER_STAT_CD
    List<AdminMemberStatsDTO> selectStatByCode(String cd);
        
}
