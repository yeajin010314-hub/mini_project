package dto;

public class MemberRoleChange {
	// 필드
    private String memberId;
    private String memberStatCd;
    

    // 기본 생성자
    public MemberRoleChange() {}

    // 전체 생성자
    public MemberRoleChange(
    			String memberId, 
                String memberStatCd) {
                     
        this.memberId = memberId;
        this.memberStatCd = memberStatCd;
    }
    

	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getMemberStatCd() {
		return memberStatCd;
	}
	public void setMemberStatCd(String memberStatCd) {
		this.memberStatCd = memberStatCd;
	}

    // 메소드
	@Override
	public String toString() {
		return "MemberRoleChange [memberId={" + memberId + "}, memberStatCd={" + memberStatCd + "}]";
	}

} // class of end;
