/**
 * DashBoard.js
 * 관리자 대시보드 공통 스크립트
 */

// DOM 완전히 로드 후 실행 (DOMContentLoaded)
document.addEventListener('DOMContentLoaded', function () {

    /* ===============================
     * 섹션 열고/닫기
     * =============================== */
    window.toggleSection = function(header) {
        const content = header.nextElementSibling;
        const arrow = header.querySelector('.arrow');

        if (content.style.display === 'none') {
            content.style.display = 'block';
            arrow.innerText = '▼';
        } else {
            content.style.display = 'none';
            arrow.innerText = '▶';
        }
    };

    /* ===============================
     * 검색 필터
     * =============================== */
    window.filterTable = function(tableId, inputId) {
        const keyword = document.getElementById(inputId).value.toLowerCase();
        document.querySelectorAll(`#${tableId} tr`).forEach(row => {
            row.style.display = row.innerText.toLowerCase().includes(keyword)
                ? ''
                : 'none';
        });
    };

    window.filterUsers = function() { filterTable('userTable', 'userSearch'); };
    window.filterCategories = function() { filterTable('categoryTable', 'categorySearch'); };
    window.filterRequests = function() { filterTable('requestTable', 'requestSearch'); };

    /* ===============================
     * 전체 체크박스 토글
     * =============================== */
    window.toggleAll = function(source, type) {
        const selector =
            type === 'user' ? '.user-check' :
            type === 'request' ? '.request-check' :
            '.category-check';

        document.querySelectorAll(selector).forEach(chk => {
            chk.checked = source.checked;

            if (type === 'request') {
                toggleRequestSelect(chk);
            }
            if (type === 'category') {
                toggleCategoryRow(chk);
            }
			if (type === 'journal') {
			    toggleJournalRow(chk);
			}
			if (type === 'userTable') {
			    toggleUserSelect(this)
			}			
			
        });
    };

		/* ===============================
		 * 회원관리 처리
		 * =============================== */
		window.toggleUserSelect = function(checkbox) {
		    const row = checkbox.closest('tr');
		    const select = row.querySelector('.member-select');
		    if (select) {
		        select.disabled = !checkbox.checked;
		    }

			// row 안의 모든 input/select/textarea
			const inputs = row.querySelectorAll('input, select, textarea');
			
			inputs.forEach(input => {
			    if (input === checkbox) return; // 체크박스 본인은 항상 enable
			    input.disabled = !checkbox.checked;
			});

	//		const proc = row.querySelector('.proc-text');
	//		if (proc) {
	//		    proc.disabled = !checkbox.checked;
	//		}
		};


	/* ===============================
	 * 기자 등록 요청 처리
	 * =============================== */
	window.toggleRequestSelect = function(checkbox) {
	    const row = checkbox.closest('tr');
	    const select = row.querySelector('.request-select');
	    if (select) {
	        select.disabled = !checkbox.checked;
	    }

		// row 안의 모든 input/select/textarea
		const inputs = row.querySelectorAll('input, select, textarea');
		
		inputs.forEach(input => {
		    if (input === checkbox) return; // 체크박스 본인은 항상 enable
		    input.disabled = !checkbox.checked;
		});

//		const proc = row.querySelector('.proc-text');
//		if (proc) {
//		    proc.disabled = !checkbox.checked;
//		}
	};


	/* ===============================
	 * 기자등록 요청관리 체크박스별 활성/비활성
	 * =============================== */
	window.toggleJournalRow = function(checkbox) {
	    const row = checkbox.closest('tr');

	    // row 안의 모든 input/select/textarea
	    const inputs = row.querySelectorAll('input, select, textarea');

	    inputs.forEach(input => {
	        if (input === checkbox) return; // 체크박스 본인은 항상 enable
	        input.disabled = !checkbox.checked;
	    });
	};


	/* ===============================
     * 카테고리 체크박스별 활성/비활성
     * =============================== */
    window.toggleCategoryRow = function(checkbox) {
        const row = checkbox.closest('tr');

        // row 안의 모든 input/select/textarea
        const inputs = row.querySelectorAll('input, select, textarea');

        inputs.forEach(input => {
            if (input === checkbox) return; // 체크박스 본인은 항상 enable
            input.disabled = !checkbox.checked;
        });
    };


	/* ===============================
	 * 카테고리명 실시간 자리수 체크 (UX)
	 * =============================== */
	//	window.validateCategoryNameInput = function(input) {
	//	    const value = input.value.trim();
	//	    const minLen = 2;
	//	    const maxLen = 50;
	//
	//	    if (value.length < minLen || value.length > maxLen) {
	//	        input.style.border = '2px solid red';
	//	        input.title = `카테고리명은 ${minLen}~${maxLen}자여야 합니다.`;
	//	        return false;
	//	    } else {
	//	        input.style.border = '';
	//	        input.title = '';
	//	        return true;
	//	    }
	//	};
	window.validateCategoryNameInput = function(input) {
	    const value = input.value.trim();
	    const minLen = 2;
	    const maxLen = 50;

	    const row = input.closest('td');
	    const errorSpan = row.querySelector('.error-msg');

	    if (value.length < minLen || value.length > maxLen) {
	        input.classList.add('input-error');
	        if (errorSpan) {
	            errorSpan.textContent = `카테고리명은 ${minLen}~${maxLen}자여야 합니다.`;
	        }
	        return false;
	    } else {
	        input.classList.remove('input-error');
	        if (errorSpan) {
	            errorSpan.textContent = '';
	        }
	        return true;
	    }
	};
	

	/* ===============================
     * 회원 권한 변경 UI
     * =============================== */
    window.updateRolesImmediately = function(select) {
        const roleMap = {
            "0": "탈퇴",
            "1": "회원",
            "2": "기자",
            "3": "관리자"
        };

        const roleName = roleMap[select.value];
        if (!roleName) return;

        document.querySelectorAll('.user-check:checked').forEach(chk => {
            const tr = chk.closest('tr');
            const roleTd = tr.querySelector('.member-role');
            if (roleTd) {
                roleTd.textContent = roleName;
                roleTd.className = 'member-role role-' + roleName;
            }
        });
    };

    /* ===============================
     * 카테고리 수정 form submit 처리
     * 체크된 row는 전부 enable
	 * 최종 검증
     * =============================== */
	const categoryForm = document.querySelector('form[action$="categoryUpdate"]');

	if (categoryForm) {
	    categoryForm.addEventListener('submit', function (e) {

	        let isValid = true;
	        let errorMsg = '';

	        document.querySelectorAll('#categoryTable tr').forEach(row => {
	            const chk = row.querySelector('.category-check');
	            if (!chk || !chk.checked) return;

	            const nameInput = row.querySelector("input[name='categoryNm']");
	            if (!nameInput) return;

	            const value = nameInput.value.trim();

	            if (value.length < 2 || value.length > 50) {
	                isValid = false;
	                nameInput.style.border = '2px solid red';
	                errorMsg = '카테고리명은 2자 이상 50자 이하로 입력하세요.';
	            }

	            // 서버 전송을 위해 enable
	            row.querySelectorAll('input, select, textarea')
	                .forEach(el => el.disabled = false);
	        });

	        if (!isValid) {
	            e.preventDefault(); // ? submit 중단
	            alert(errorMsg);
	        }
	    });
	}
	
	/* ===============================
	 * 기자등록 요청관리 수정 form submit 처리
	 * 체크된 row는 전부 enable
	 * 최종 검증
	 * =============================== */
//	const categoryForm = document.querySelector('form[action$="jourReasonCdUpdate"]');
//
//	if (categoryForm) {
//	    categoryForm.addEventListener('submit', function (e) {
//
//	        let isValid = true;
//	        let errorMsg = '';
//
//	        document.querySelectorAll('#requestTable tr').forEach(row => {
////	            const chk = row.querySelector('.request-check');
////	            if (!chk || !chk.checked) return;
//
//	            const nameInput = row.querySelector("input[name='procReason']");
//	            if (!nameInput) return;
//
//	            const value = nameInput.value.trim();
//
//	            if (value.length < 2 || value.length > 50) {
//	                isValid = false;
//	                nameInput.style.border = '2px solid red';
//	                errorMsg = '처리내용은 5자 이상 100자 이하로 입력하세요.';
//	            }
//
//				const nameInputProcReason = row.querySelector("input[name='procReason']");
//				if (!nameInputProcReason) return;
//
//				const valueProcReason = nameInputProcReason.value.trim();
//
//				if (valueProcReason.length < 2 || valueProcReason.length > 50) {
//				    isValid = false;
//				    nameInput.style.border = '2px solid red';
//				    errorMsg = '처리내용은 5자 이상 100자 이하로 입력하세요.';
//				}
//
//				// 서버 전송을 위해 enable
//	            row.querySelectorAll('input, select, textarea')
//	                .forEach(el => el.disabled = false);
//	        });
//
//	        if (!isValid) {
//	            e.preventDefault(); // ? submit 중단
//	            alert(errorMsg);
//	        }
//	    });
//	}
});
