package dto;

import java.time.LocalDate;

public class MemberBasDTO {
	// 필드
    private String memberId;
    private String memberNm;
    private String passwd;
    private String birth;
    private String email;
    private String telNo;
    private String nickNm;
    private String memberStatCd;
    private String memberStatNm;
    private String journalReasonCd;
    private String journalReasonNm;
    private String journalReasonDesc;
    private LocalDate regDt;
    private String profileImgUrl;
    private String procReason;
    private LocalDate leaveDt;
    private LocalDate joinDt;
    
    // 기본 생성자
    public MemberBasDTO() {}

    // 전체 생성자
    public MemberBasDTO(
    			String memberId, 
    			String memberNm, 
    			String passwd,
                String birth, 
                String email, 
                String telNo, 
                String nickNm,
                String memberStatCd, 
                String memberStatNm, 
                String journalReasonCd, 
                String journalReasonNm, 
                String journalReasonDesc, 
                LocalDate regDt,
                String profileImgUrl,
                String procReason,
                LocalDate leaveDt) {
                     
        this.memberId = memberId;
        this.memberNm = memberNm;
        this.passwd = passwd;
        this.birth = birth;
        this.email = email;
        this.telNo = telNo;
        this.nickNm = nickNm;
        this.memberStatCd = memberStatCd;
        this.memberStatNm = memberStatNm;
        this.journalReasonCd = journalReasonCd;
        this.journalReasonNm = journalReasonNm;
        this.journalReasonDesc = journalReasonDesc;
        this.regDt = regDt;
        this.profileImgUrl = profileImgUrl;
        this.procReason = procReason;
        this.leaveDt = leaveDt;
    }
    
    // getter/setter
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
	public String getMemberStatNm() {
		return memberStatNm;
	}
	public void setMemberStatNm(String memberStatNm) {
		this.memberStatNm = memberStatNm;
	}
	public String getJournalReasonCd() {
		return journalReasonCd;
	}
	public void setJournalReasonCd(String journalReasonCd) {
		this.journalReasonCd = journalReasonCd;
	}
	public String getJournalReasonNm() {
		return journalReasonNm;
	}
	public void setJournalReasonNm(String journalReasonNm) {
		this.journalReasonNm = journalReasonNm;
	}
	public String getJournalReasonDesc() {
		return journalReasonDesc;
	}
	public void setJournalReasonDesc(String journalReasonDesc) {
		this.journalReasonDesc = journalReasonDesc;
	}
	public LocalDate getRegDt() {
		return regDt;
	}
	public void setRegDt(LocalDate regDt) {
		this.regDt = regDt;
	}

    public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public String getProcReason() {
		return procReason;
	}

	public void setProcReason(String procReason) {
		this.procReason = procReason;
	}

	public LocalDate getLeaveDt() {
		return leaveDt;
	}

	public void setLeaveDt(LocalDate leaveDt) {
		this.leaveDt = leaveDt;
	}

	public LocalDate getJoinDt() {
		return joinDt;
	}

	public void setJoinDt(LocalDate joinDt) {
		this.joinDt = joinDt;
	}

	// 메소드
	@Override
	public String toString() {
		return "MemberBasDTO [memberId={" + memberId + "}, memberNm={" + memberNm + "}, passwd={" + passwd + "}, birth={"
				+ birth + "}, email={" + email + "}, telNo={" + telNo + "}, nickNm={" + nickNm + "}, memberStatCd={"
				+ memberStatCd + "}, journalReasonCd={" + journalReasonCd + "}, journalReasonDesc={" + journalReasonDesc
				+ "}, regDt={" + regDt + "}, profileImgUrl={" + profileImgUrl + "}, procReason={" + procReason 
				+ "}, leaveDt={" + leaveDt + "}, joinDt={" + joinDt + "}]";
	}
	
}  // class of end;
