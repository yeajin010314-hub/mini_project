package dto;

import java.sql.Timestamp;

public class ArticleDivDTO {

	 private int articleNo;        
	    private String memberId;      
	    private String articleDivCd;  
	    private String commentId;     
	    private Timestamp regDt;
		public int getArticleNo() {
			return articleNo;
		}
		public void setArticleNo(int articleNo) {
			this.articleNo = articleNo;
		}
		public String getMemberId() {
			return memberId;
		}
		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}
		public String getArticleDivCd() {
			return articleDivCd;
		}
		public void setArticleDivCd(String articleDivCd) {
			this.articleDivCd = articleDivCd;
		}
		public String getCommentId() {
			return commentId;
		}
		public void setCommentId(String commentId) {
			this.commentId = commentId;
		}
		public Timestamp getRegDt() {
			return regDt;
		}
		public void setRegDt(Timestamp regDt) {
			this.regDt = regDt;
		}
	    
}
