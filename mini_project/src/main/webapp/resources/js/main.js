document.addEventListener("DOMContentLoaded", () => {

    console.log("메인 페이지 로드 완료");

    document.querySelectorAll(".mega-section h3").forEach(title => {

        title.addEventListener("click", function () {

            const section = this.parentElement;

            // 다른 메뉴 닫기
            document.querySelectorAll(".mega-section").forEach(sec => {
                if(sec !== section) {
                    sec.classList.remove("open");
                }
            });

            // 현재 toggle
            section.classList.toggle("open");

        });

    });

});
