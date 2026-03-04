package dto;

import java.time.LocalDate;

public class ArticleViewDTO {
    private int    articleNo;
    private String categoryId;
    private String subject;
    private String memberId;
    private String memberNm;
    private String nickNm;
    private String hashTag;
    private int 	   articleStat;
    private String good;
    private String cnt;
    private LocalDate regDt;
    private int		recommendScore;

	// 기본생성자
	public ArticleViewDTO() {};
	
	// 전체 생성자
	public ArticleViewDTO(
		    int 	   articleNo,
		    String categoryId,
		    String subject,
		    String memberId,
		    String hashTag,
		    int 	   articleStat,
		    String good,
		    String cnt,
		    LocalDate regDt)
	{
		this.articleNo = articleNo;
		this.categoryId = categoryId;
		this.subject = subject;
		this.memberId = memberId;
		this.hashTag = hashTag;
		this.articleStat = articleStat;
		this.good = good;
		this.cnt = cnt;
		this.regDt = regDt;
	}

	public int getArticleNo() {
		return articleNo;
	}

	public void setArticleNo(int articleNo) {
		this.articleNo = articleNo;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberNm() {
		return memberNm;
	}

	public void setMemberNm(String memberNm) {
		this.memberNm = memberNm;
	}

	public String getNickNm() {
		return nickNm;
	}

	public void setNickNm(String nickNm) {
		this.nickNm = nickNm;
	}

	public String getHashTag() {
		return hashTag;
	}

	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
	}

	public int getArticleStat() {
		return articleStat;
	}

	public void setArticleStat(int articleStat) {
		this.articleStat = articleStat;
	}

	public String getGood() {
		return good;
	}

	public void setGood(String good) {
		this.good = good;
	}

	public String getCnt() {
		return cnt;
	}

	public void setCnt(String cnt) {
		this.cnt = cnt;
	}

	public LocalDate getRegDt() {
		return regDt;
	}

	public void setRegDt(LocalDate regDt) {
		this.regDt = regDt;
	}

	public int getRecommendScore() {
		return recommendScore;
	}

	public void setRecommendScore(int recommendScore) {
		this.recommendScore = recommendScore;
	}

} // class of end;
