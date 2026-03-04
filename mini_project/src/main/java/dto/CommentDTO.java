package dto;

import java.sql.Timestamp;

/*
작성자 : 박재운
작성일 : 2026-02-04
댓글 정보를 담는 DTO 클래스
DB의 ARTICLE_COMMENT 테이블과 매핑 
*/
public class CommentDTO {
	private String commentId;
	private String memberId;
	private String memberNm;
	private String nickNm;
	private int articleNo;
	private String upCommentId;
	private String commentContent;
	private int commentGood;
	private Timestamp regDt;
	
	public String getNickNm() {
		return nickNm;
	}
	public void setNickNm(String nickNm) {
		this.nickNm = nickNm;
	}
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
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
	public int getArticleNo() {
		return articleNo;
	}
	public void setArticleNo(int articleNo) {
		this.articleNo = articleNo;
	}
	public String getUpCommentId() {
		return upCommentId;
	}
	public void setUpCommentId(String upCommentId) {
		this.upCommentId = upCommentId;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public int getCommentGood() {
		return commentGood;
	}
	public void setCommentGood(int commentGood) {
		this.commentGood = commentGood;
	}
	public Timestamp getRegDt() {
		return regDt;
	}
	public void setRegDt(Timestamp regDt) {
		this.regDt = regDt;
	}
}
