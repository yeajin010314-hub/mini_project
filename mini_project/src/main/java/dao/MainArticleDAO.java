package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.ArticleViewDTO;
import dto.ArticleDetailViewDTO;
import dto.ArticleDtlViewDTO;

/*
 * 작성자 : 김상현
 * 작성일 : 2026-02-03
 * DESC : 관리자 회원관리 기능에서 회원 정보 조회 및 상태 변경을 처리하는 DAO
 */
public class MainArticleDAO {
	// 필드 선언
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * 카테고리별 기사목록을 조회한다.
	 * 
	 * @param 카테고리아이디
	 * @param 조회할 건수
	 * @return 카테고리별 기사목록
	 */
	public List<ArticleDetailViewDTO> selectArticleListById(String categoryId, int rowLimit) {
		List<ArticleDetailViewDTO> listResultSet = new ArrayList<>();
		int rowCount = 0; // 수행 건수
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // Main화면 카테고리별 상위 기사 목록조회
		    sql = "Select												    \n"
		    		+ "        ARTICLE_NO        as articleNo				\n"
		    		+ "       ,CATEGORY_ID       as categoryId              \n"
		    		+ "       ,TRIM(SUBSTR(SUBJECT, 1, 10))           as subject                 \n"
		    		+ "       ,MEMBER_ID         as memberId                \n"
		    		+ "       ,MEMBER_NM         as memberNm                \n"
		    		+ "       ,TRIM(substr(HASH_TAG, 1, DECODE(INSTR(HASH_TAG, '#', 1, 3), 0, LENGTH(HASH_TAG), "
		    		+ "                                        INSTR(HASH_TAG, '#', 1, 3) -1)))  as hashTag  	\n"
		    		+ "       ,ARTICLE_STAT      as articleStat             \n"
		    		+ "       ,GOOD              as good                    \n"
		    		+ "       ,CNT               as cnt                     \n"
		    		+ "       ,RECOMMEND_SCORE   as recommendScore          \n"
		    		+ "       ,ARTICLE_CONTENT   as articleContent          \n"
		    		+ "       ,ARTICLE_IMG1      as articleImg1             \n"
		    		+ "From   (                                             \n"
		    		+ "        Select                                       \n"
		    		+ "                a.ARTICLE_NO                         \n"
		    		+ "               ,a.CATEGORY_ID                        \n"
		    		+ "               ,a.SUBJECT                            \n"
		    		+ "               ,a.MEMBER_ID                          \n"
		    		+ "               ,e.MEMBER_NM                          \n"
		    		+ "               ,a.HASH_TAG                           \n"
		    		+ "               ,a.ARTICLE_STAT                       \n"
		    		+ "               ,a.GOOD                               \n"
		    		+ "               ,a.CNT                                \n"
		    		+ "               ,(a.GOOD + CNT) as recommend_score    \n"
		    		+ "               ,ARTICLE_CONTENT                      \n"
		    		+ "               ,ARTICLE_IMG1                         \n"
		    		+ "               ,d.CATEGORY_ROOT                      \n"
		    		+ "               ,ROW_NUMBER() OVER(Order by (GOOD + CNT) DESC, a.ARTICLE_NO) as recommend_score_NO \n"
		    		+ "        From   article a                                                 \n"
		    		+ "              ,CODE_MST b                                                \n"
		    		+ "              ,ARTICLE_DTL c                                             \n"
		    		+ "              ,(Select /* 카테고리ID가 00000이면 전체, 아니면 카테고리ID로 추출함. */ \n"
		    		+ "                        CATEGORY_ID                                      \n"
		    		+ "                       ,UP_CATEGORY_ID                                   \n"
		    		+ "                       ,CONNECT_BY_ROOT CATEGORY_ID as CATEGORY_ROOT     \n"
		    		+ "                From  CATEGORY                                           \n"
		    		+ "                Start with ? = DECODE(?, '00000', UP_Category_ID, CATEGORY_ID)  \n"
		    		+ "                Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID    \n"
		    		+ "               ) d                                                       \n"
		    		+ "              ,MEMBER_BAS e                                              \n"
		    		+ "        Where  1 = 1                                                   	\n"
		    		+ "        And    a.ARTICLE_STAT = b.CD_VAL(+)                              \n"
		    		+ "        And    b.cd_id(+) = 'ARTICLE_STAT'                               \n"
		    		+ "        And    b.CD_VAL(+) = '1'                                         \n"
		    		+ "        And    a.article_no = c.article_no                               \n"
		    		+ "        And    a.category_id = d.category_id                             \n"
		    		+ "        And    a.member_id   = e.member_id                               \n"
		    		+ "       ) x                                                               \n"
		    		+ "Where  1 = 1                                                             \n"
		    		+ "And    recommend_score_NO <= ?";
		    
		    System.out.println("MainArticleDAO.selectArticleListById.sql : " + sql);
		    System.out.println("categoryId : " + categoryId);
		    
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, categoryId);
		    pstmt.setString(2, categoryId);
		    pstmt.setInt(3, rowLimit);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
			    	rowCount++;

			    	ArticleDetailViewDTO dto = new ArticleDetailViewDTO();
			    	ArticleViewDTO articltdto = new ArticleViewDTO();
			    	ArticleDtlViewDTO articltDtldto = new ArticleDtlViewDTO();

			    	articltdto.setArticleNo(rs.getInt("articleNo"));
			    	articltdto.setCategoryId(rs.getString("categoryId"));
			    	articltdto.setSubject(rs.getString("subject"));
			    	articltdto.setMemberId(rs.getString("memberId"));
			    	articltdto.setMemberNm(rs.getString("memberNm"));
			    	articltdto.setHashTag(rs.getString("hashTag"));
			    	articltdto.setArticleStat(rs.getInt("articleStat"));
			    	articltdto.setGood(rs.getString("good"));
			    	articltdto.setCnt(rs.getString("cnt"));
			    	articltdto.setRecommendScore(rs.getInt("recommendScore"));
			    	articltDtldto.setArticleNo(rs.getInt("articleNo"));
			    	articltDtldto.setArticleContent(rs.getString("articleContent"));
			    	articltDtldto.setArticleImg1(rs.getString("articleImg1"));
			    	
	                dto.setArticle(articltdto);
	                dto.setArticleDtl(articltDtldto);
	                listResultSet.add(dto);
		    }
		    System.out.println("조회된 dto 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return listResultSet;

	}
	
	public ArticleDetailViewDTO selectMainTopArticlesByCategoryGroup(String div) {
    	ArticleDetailViewDTO dto = new ArticleDetailViewDTO();
		int rowCount = 0; // 수행 건수
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }

		    // Main화면의 카테고리별 상위 기사 1건조회
		    sql = "Select												\n"
	    		+ "        ARTICLE_NO        as articleNo				\n"
	    		+ "       ,CATEGORY_ID       as categoryId              \n"
	    		+ "       ,TRIM(SUBSTR(SUBJECT, 1, 10))           as subject                 \n"
	    		+ "       ,MEMBER_ID         as memberId                \n"
	    		+ "       ,MEMBER_NM         as memberNm                \n"
	    		+ "       ,NICK_NM           as nickNm                	\n"
	    		+ "       ,TRIM(substr(HASH_TAG, 1, DECODE(INSTR(HASH_TAG, '#', 1, 3), 0, LENGTH(HASH_TAG), "
	    		+ "                                        INSTR(HASH_TAG, '#', 1, 3) -1)))  as hashTag  	\n"
	    		+ "       ,ARTICLE_STAT      as articleStat             \n"
	    		+ "       ,GOOD              as good                    \n"
	    		+ "       ,CNT               as cnt                     \n"
	    		+ "       ,RECOMMEND_SCORE   as recommendScore          \n"
	    		+ "       ,ARTICLE_CONTENT   as articleContent          \n"
	    		+ "       ,ARTICLE_IMG1      as articleImg1             \n"
	    		+ "From   (                                             \n"
	    		+ "        Select                                       \n"
	    		+ "                a.ARTICLE_NO                         \n"
	    		+ "               ,a.CATEGORY_ID                        \n"
	    		+ "               ,a.SUBJECT                            \n"
	    		+ "               ,a.MEMBER_ID                          \n"
	    		+ "               ,e.MEMBER_NM                          \n"
	    		+ "               ,e.NICK_NM                          	\n"
	    		+ "               ,a.HASH_TAG                           \n"
	    		+ "               ,a.ARTICLE_STAT                       \n"
	    		+ "               ,a.GOOD                               \n"
	    		+ "               ,a.CNT                                \n"
	    		+ "               ,(a.GOOD + CNT) as recommend_score    \n"
	    		+ "               ,ARTICLE_CONTENT                      \n"
	    		+ "               ,ARTICLE_IMG1                         \n"
	    		+ "               ,d.CATEGORY_ROOT                      \n"
	    		+ "               ,ROW_NUMBER() OVER(Order by (GOOD + CNT) DESC, a.ARTICLE_NO) as recommend_score_NO \n"
	    		+ "        From   article a                                                 \n"
	    		+ "              ,CODE_MST b                                                \n"
	    		+ "              ,ARTICLE_DTL c                                             \n"
	    		+ "              ,(Select /* 여행 추천 */                                      \n"
	    		+ "                        CATEGORY_ID                                      \n"
	    		+ "                       ,UP_CATEGORY_ID                                   \n"
	    		+ "                       ,CONNECT_BY_ROOT CATEGORY_ID as CATEGORY_ROOT     \n"
	    		+ "                From  CATEGORY                                           \n"
	    		+ "                Start with CATEGORY_ID IN ( '00001', '00002', '00004' )  \n"
	    		+ "                Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID    \n"
	    		+ "               ) d                                                       \n"
	    		+ "              ,MEMBER_BAS e                                              \n"
	    		+ "        Where  ? = '1'                                                   \n"
	    		+ "        And    a.ARTICLE_STAT = b.CD_VAL(+)                              \n"
	    		+ "        And    b.cd_id(+) = 'ARTICLE_STAT'                               \n"
	    		+ "        And    b.CD_VAL(+) = '1'                                         \n"
	    		+ "        And    a.article_no = c.article_no                               \n"
	    		+ "        And    a.category_id = d.category_id                             \n"
	    		+ "        And    a.member_id   = e.member_id                               \n"
	    		+ "        UNION ALL                                                        \n"
	    		+ "        Select                                                           \n"
	    		+ "                a.ARTICLE_NO                                             \n"
	    		+ "               ,a.CATEGORY_ID                                            \n"
	    		+ "               ,a.SUBJECT                                                \n"
	    		+ "               ,a.MEMBER_ID                                              \n"
	    		+ "               ,e.MEMBER_NM                          					\n"
	    		+ "               ,e.NICK_NM                          						\n"
	    		+ "               ,a.HASH_TAG                                               \n"
	    		+ "               ,a.ARTICLE_STAT                                           \n"
	    		+ "               ,a.GOOD                                                   \n"
	    		+ "               ,a.CNT                                                    \n"
	    		+ "               ,(a.GOOD + CNT) as recommend_score                        \n"
	    		+ "               ,ARTICLE_CONTENT                      					\n"
	    		+ "               ,ARTICLE_IMG1                                             \n"
	    		+ "               ,d.CATEGORY_ROOT                                          \n"
	    		+ "               ,ROW_NUMBER() OVER(Order by (GOOD + CNT) DESC, a.ARTICLE_NO) as recommend_score_NO                                                 \n"
	    		+ "        From   article a                                                 \n"
	    		+ "              ,CODE_MST b                                                \n"
	    		+ "              ,ARTICLE_DTL c                                             \n"
	    		+ "              ,(Select /* 맛집 추천 */                                      \n"
	    		+ "                        CATEGORY_ID                                      \n"
	    		+ "                       ,UP_CATEGORY_ID                                   \n"
	    		+ "                       ,CONNECT_BY_ROOT CATEGORY_ID as CATEGORY_ROOT     \n"
	    		+ "                From  CATEGORY                                           \n"
	    		+ "                Start with CATEGORY_ID IN ( '00003', '00004' )           \n"
	    		+ "                Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID    \n"
	    		+ "               ) d                                                       \n"
	    		+ "              ,MEMBER_BAS e                                              \n"
	    		+ "        Where  ? = '2'                                                 	\n"
	    		+ "        And    a.ARTICLE_STAT = b.CD_VAL(+)                              \n"
	    		+ "        And    b.cd_id(+) = 'ARTICLE_STAT'                               \n"
	    		+ "        And    b.CD_VAL(+) = '1'                                         \n"
	    		+ "        And    a.article_no = c.article_no                               \n"
	    		+ "        And    a.category_id = d.category_id                             \n"
	    		+ "        And    a.member_id   = e.member_id                               \n"
	    		+ "       ) x                                                               \n"
	    		+ "Where  1 = 1                                                             \n"
	    		+ "And    recommend_score_NO = 1";

		    System.out.println("MainArticleDAO.selectMainTopArticlesByCategoryGroup.sql : " + sql);
		    System.out.println("div : " + div);
	    
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, div);
		    pstmt.setString(2, div);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
		    	rowCount++;

		    	ArticleViewDTO articltdto = new ArticleViewDTO();
		    	ArticleDtlViewDTO articltDtldto = new ArticleDtlViewDTO();

		    	articltdto.setArticleNo(rs.getInt("articleNo"));
		    	articltdto.setCategoryId(rs.getString("categoryId"));
		    	articltdto.setSubject(rs.getString("subject"));
		    	articltdto.setMemberId(rs.getString("memberId"));
		    	articltdto.setMemberNm(rs.getString("memberNm"));
		    	articltdto.setNickNm(rs.getString("nickNm"));
		    	articltdto.setHashTag(rs.getString("hashTag"));
		    	articltdto.setArticleStat(rs.getInt("articleStat"));
		    	articltdto.setGood(rs.getString("good"));
		    	articltdto.setCnt(rs.getString("cnt"));
		    	articltdto.setRecommendScore(rs.getInt("recommendScore"));
		    	articltDtldto.setArticleNo(rs.getInt("articleNo"));
		    	articltDtldto.setArticleContent(rs.getString("articleContent"));
		    	articltDtldto.setArticleImg1(rs.getString("articleImg1"));
		    	
                dto.setArticle(articltdto);
                dto.setArticleDtl(articltDtldto);
            	System.out.println("dto : [" + dto + "]");
            	System.out.println("dto.getArticle() : [" + dto.getArticle() + "]");

                System.out.println("***************************************************");
                System.out.println("articleNo : [" + articltdto.getArticleNo() + "]");
		    }
		    System.out.println("조회된 dto 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return dto;
	}
}
