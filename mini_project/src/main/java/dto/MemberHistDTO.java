package dto;

import java.sql.Timestamp;
import java.util.Date;

public class MemberHistDTO {

	private String memberId;
    private String memberNm;
    private String passwd;
    private String birth;
    private String email;
    private String telNo;
    private String nickNm;
    private String memberStatCd;
    private String journalReasonCd;
    private String journalReasonDesc;
    private Timestamp statChgStDt;
    private Timestamp statChgEndDt;
    private Date regDt;
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
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelNo() {
		return telNo;
	}
	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}
	public String getNickNm() {
		return nickNm;
	}
	public void setNickNm(String nickNm) {
		this.nickNm = nickNm;
	}
	public String getMemberStatCd() {
		return memberStatCd;
	}
	public void setMemberStatCd(String memberStatCd) {
		this.memberStatCd = memberStatCd;
	}
	public String getJournalReasonCd() {
		return journalReasonCd;
	}
	public void setJournalReasonCd(String journalReasonCd) {
		this.journalReasonCd = journalReasonCd;
	}
	public String getJournalReasonDesc() {
		return journalReasonDesc;
	}
	public void setJournalReasonDesc(String journalReasonDesc) {
		this.journalReasonDesc = journalReasonDesc;
	}
	public Timestamp getStatChgStDt() {
		return statChgStDt;
	}
	public void setStatChgStDt(Timestamp statChgStDt) {
		this.statChgStDt = statChgStDt;
	}
	public Timestamp getStatChgEndDt() {
		return statChgEndDt;
	}
	public void setStatChgEndDt(Timestamp statChgEndDt) {
		this.statChgEndDt = statChgEndDt;
	}
	public Date getRegDt() {
		return regDt;
	}
	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}
    
}
