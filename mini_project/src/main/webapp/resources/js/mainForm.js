/**
 * 
 */


document.addEventListener("DOMContentLoaded", function () {

    // 🔽 기존 JS 코드 전부 이 안으로 이동 🔽


// 게시글 데이터
const travelPosts = [
    {title:"제주도 가족 여행", author:"김철수", usage:120, recent:15, preference:5, adminWeight:2, tags:["가족","국내"], likes:0},
    {title:"파리 연인 여행", author:"이영희", usage:80, recent:20, preference:3, adminWeight:4, tags:["연인","해외"], likes:0},
    {title:"서울 힐링 여행", author:"박민수", usage:150, recent:10, preference:4, adminWeight:1, tags:["힐링","국내"], likes:0},
    {title:"혼자 떠나는 솔로 여행", author:"최지훈", usage:60, recent:30, preference:5, adminWeight:3, tags:["솔로","국내"], likes:0}
];

const foodPosts = [
    {title:"강남 한식 맛집", author:"홍길동", usage:200, recent:40, preference:4, adminWeight:2, tags:["한식"], likes:0},
    {title:"신촌 일식 추천", author:"유재석", usage:150, recent:25, preference:3, adminWeight:3, tags:["일식"], likes:0},
    {title:"명동 인도요리", author:"박명수", usage:100, recent:10, preference:5, adminWeight:2, tags:["인도"], likes:0}
];

const weights = {usage:1, recent:2, preference:3, admin:1, userPref:4, likes:5};
let currentUser = {name:"", preferenceTags:[]};
let currentCategory = 'travel';

// 로그인
document.getElementById('login-btn').addEventListener('click', () => {
    const name = prompt("이름을 입력하세요:");
    if(name){
        const pref = prompt("선호 태그 입력 (콤마 구분)");
        currentUser = {
            name,
            preferenceTags: pref ? pref.split(",").map(t=>t.trim()) : []
        };
        document.getElementById('welcome-msg').textContent =
            `${currentUser.name}님, 환영합니다! `;
        document.getElementById('login-btn').style.display='none';
        document.getElementById('signup-btn').style.display='none';
        renderPosts(currentCategory);
    }
});

// 추천 점수
function calcScore(post){
    let userScore = 0;
    post.tags.forEach(tag=>{
        if(currentUser.preferenceTags.includes(tag)) userScore++;
    });

    return post.usage*weights.usage +
           post.recent*weights.recent +
           (currentUser.preferenceTags.length ? userScore : post.preference)*weights.userPref +
           post.adminWeight*weights.admin +
           post.likes*weights.likes;
}

// 렌더링
function renderPosts(category){
    const container = document.getElementById(
        category === 'travel' ? 'travel-content' : 'food-content'
    );
    container.innerHTML = '';

    const posts = category === 'travel' ? travelPosts : foodPosts;
    posts.sort((a,b)=>calcScore(b)-calcScore(a));

    posts.forEach(post=>{
        const div = document.createElement('div');
        div.className='post';
        div.innerHTML=`
            <h3>${post.title}</h3>
            <p>작성자: ${post.author}</p>
            <p>추천점수: ${calcScore(post)}</p>
            <p>태그: ${post.tags.join(", ")}</p>
            <button class="like-btn">추천 👍 ${post.likes}</button>
        `;

        div.addEventListener('click',()=>{
            post.recent++;
            renderPosts(category);
        });

        div.querySelector('.like-btn').addEventListener('click',(e)=>{
            e.stopPropagation();
            post.likes++;
            renderPosts(category);
        });

        container.appendChild(div);
    });
}

// 메뉴 전환
document.querySelectorAll('.menu-btn').forEach(btn=>{
    btn.addEventListener('click',()=>{
        document.querySelectorAll('.menu-btn').forEach(b=>b.classList.remove('active'));
        btn.classList.add('active');
        currentCategory = btn.dataset.category;

        document.getElementById('travel-content').style.display =
            currentCategory==='travel'?'block':'none';
        document.getElementById('food-content').style.display =
            currentCategory==='food'?'block':'none';

        renderPosts(currentCategory);
    });
});

// 검색
document.getElementById('search-btn').addEventListener('click',()=>{
    const keyword=document.getElementById('search-input').value.trim();
    alert(keyword ? `검색: ${keyword}` : '검색어를 입력하세요');
});


// 로그인 버튼 부분
const loginBtn = document.getElementById('login-btn');
const signupBtn = document.getElementById('signup-btn');
const welcomeMsg = document.getElementById('welcome-msg');

if (loginBtn) {
    loginBtn.addEventListener('click', () => {
        const name = prompt("이름을 입력하세요:");
        if(name){
            const pref = prompt("선호 태그 입력 (콤마 구분)");
            currentUser = {
                name,
                preferenceTags: pref ? pref.split(",").map(t=>t.trim()) : []
            };
            welcomeMsg.textContent = `${currentUser.name}님, 환영합니다! `;
            loginBtn.style.display='none';
            if (signupBtn) signupBtn.style.display='none';
            renderPosts(currentCategory);
        }
    });
}


// 초기 실행
renderPosts('travel');

});
