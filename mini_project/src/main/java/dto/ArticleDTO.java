package dto;

import java.sql.Timestamp;

/*
작성자 : 박재운
작성일 : 2026-02-04
기사 정보를 담는 DTO 클래스
DB의 article 테이블과 매핑
*/
public class ArticleDTO {
	private int articleNo;
	private String categoryId;
	private String memberId;
	private String subject;
	private String memberNm;
	private String nickNm;
	private String hashTag;
	private int articleStat;
	private int good;
	private int cnt;
	private String passwd;
	private String memberStatCd;
	private String categoryNm;
	private String up_categoryId;
	private String thumbnail;
	private Timestamp regDt;
	private String content_role;
	
	public String getContent_role() {
		return content_role;
	}

	public void setContent_role(String content_role) {
		this.content_role = content_role;
	}

	public String getNickNm() {
		return nickNm;
	}

	public void setNickNm(String nickNm) {
		this.nickNm = nickNm;
	}

	public Timestamp getRegDt() {
		return regDt;
	}

	public void setRegDt(Timestamp regDt) {
		this.regDt = regDt;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
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

	public String getMemberStatCd() {
		return memberStatCd;
	}

	public void setMemberStatCd(String memberStatCd) {
		this.memberStatCd = memberStatCd;
	}

	public String getCategoryNm() {
		return categoryNm;
	}

	public void setCategoryNm(String categoryNm) {
		this.categoryNm = categoryNm;
	}

	public String getUp_categoryId() {
		return up_categoryId;
	}

	public void setUp_categoryId(String up_categoryId) {
		this.up_categoryId = up_categoryId;
	}
}
