package dto;

import java.sql.Timestamp;

public class CodeMst {

	 private String cdId;           
	    private String cdNm;   
	    private String cdVal;   
	    private Timestamp regDt;
		public String getCdId() {
			return cdId;
		}
		public void setCdId(String cdId) {
			this.cdId = cdId;
		}
		public String getCdNm() {
			return cdNm;
		}
		public void setCdNm(String cdNm) {
			this.cdNm = cdNm;
		}
		public String getCdVal() {
			return cdVal;
		}
		public void setCdVal(String cdVal) {
			this.cdVal = cdVal;
		}
		public Timestamp getRegDt() {
			return regDt;
		}
		public void setRegDt(Timestamp regDt) {
			this.regDt = regDt;
		} 
	    
}
