package dto;

import java.sql.Timestamp;

/*
작성자 : 박재운
작성일 : 2026-02-04
기사의 상세 정보를 담는 DTO 클래스
DB의 ARTICLE_DTL 테이블과 매핑
*/
public class ArticleDtlDTO {  
    private int articleNo;
    private String articleContent;
    private String articleImg1;
    private String articleImg2;
    private String articleImg3;
    private String linkText;
	private String linkUrl;
    private Timestamp regDt;
    
	public int getArticleNo() {
		return articleNo;
	}
	public void setArticleNo(int articleNo) {
		this.articleNo = articleNo;
	}
	public String getArticleContent() {
		return articleContent;
	}
	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}
	public String getArticleImg1() {
		return articleImg1;
	}
	public void setArticleImg1(String articleImg1) {
		this.articleImg1 = articleImg1;
	}
	public String getArticleImg2() {
		return articleImg2;
	}
	public void setArticleImg2(String articleImg2) {
		this.articleImg2 = articleImg2;
	}
	public String getArticleImg3() {
		return articleImg3;
	}
	public void setArticleImg3(String articleImg3) {
		this.articleImg3 = articleImg3;
	}
	public String getLinkText() {
		return linkText;
	}
	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public Timestamp getRegDt() {
		return regDt;
	}
	public void setRegDt(Timestamp regDt) {
		this.regDt = regDt;
	}
}
