package dto;

import java.sql.Timestamp;

public class ArticleCommentDTO {

	private String commentId;       
    private String memberId;        
    private int articleNo;          
    private String upCommentId;     
    private String commentContent;  
    private int commentGood;        
    private Timestamp regDt;
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
