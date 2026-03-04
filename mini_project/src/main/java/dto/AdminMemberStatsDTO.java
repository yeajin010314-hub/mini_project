package dto;

public record AdminMemberStatsDTO(String cd_nm, String cd_val, int cnt) {

	// 생성자 : 필드 검증
    public AdminMemberStatsDTO {
        if (cd_nm == null || cd_nm.isBlank()) {
            throw new IllegalArgumentException("cd_nm 빈문자열일수 없습니다.");
        }
        if (cd_val == null || cd_val.isBlank()) {
            throw new IllegalArgumentException("cd_val은 하나의 값이 필요합니다.");
        }
        if (cnt < 0) { // cnt는 int이므로 null 검사 불필요
            throw new IllegalArgumentException("cnt는 0 이상이어야 합니다.");
        }
    }
}
