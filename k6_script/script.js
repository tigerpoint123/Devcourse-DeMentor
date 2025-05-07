import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// Prometheus 메트릭 정의
const loginCounter = new Counter('login_total');
const favoriteCounter = new Counter('favorite_operations_total');
const errorCounter = new Counter('error_total');

// 테스트 옵션
export const options = {
    scenarios: {
        single_user_test: {
            executor: 'constant-vus',
            vus: 1,         // 단일 사용자
            duration: '3s', // 5분 동안 테스트
        },
    }
};

export default function () {
    const BASE_URL = 'http://host.docker.internal:8080';
    
    // 1. 로그인 API 호출 (POST)
    const loginRes = http.post(`${BASE_URL}/api/members/login`, JSON.stringify({
        email: 'tigerrla@naver.com',
        password: '1234',
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    // 2. 로그인 응답 확인
    check(loginRes, {
        '로그인 성공': (r) => r.status === 200,
    });
    loginCounter.add(1);

    // 3. 응답 헤더에서 토큰 추출
    const cookies = loginRes.headers['Set-Cookie'];
    console.log('Set-Cookie 헤더:', cookies);
    
    let accessToken = null;
    if (typeof cookies === 'string') {
        const tokenMatch = cookies.match(/accessToken=([^;]+)/);
        if (tokenMatch) {
            accessToken = tokenMatch[1];
            console.log('추출된 액세스 토큰:', accessToken);
        }
    }
    
    if (!accessToken) {
        console.log('액세스 토큰을 찾을 수 없습니다.');
        return;
    }

    // 4. 인증 헤더 설정
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`  // Bearer 토큰 형식으로 추가
        },
        cookies: {
            accessToken: accessToken
        }
    };

    // 5. 즐겨찾기 추가
    const classId = 35; // 테스트할 클래스 ID
    const addFavoriteRes = http.post(
        `${BASE_URL}/api/favorite/${classId}`,
        null,
        params
    );
    
    check(addFavoriteRes, {
        '즐겨찾기 추가 성공': (r) => r.status === 200,
    });
    favoriteCounter.add(1);
    if (addFavoriteRes.status !== 200) {
        errorCounter.add(1);
        console.log('즐겨찾기 추가 실패:', addFavoriteRes.status, addFavoriteRes.body);
    }

    // 6. 즐겨찾기 카운트 확인
    const countRes = http.get(
        `${BASE_URL}/api/class/favoriteCount/${classId}`,
        params
    );
    
    check(countRes, {
        '즐겨찾기 카운트 조회 성공': (r) => {
            console.log('상태 코드:', r.status);
            console.log('응답 내용:', r.body);
            return r.status === 200;
        },
        '즐겨찾기 카운트 값 유효성': (r) => {
            const response = r.json();
            console.log('파싱된 응답:', response);
            const favoriteCount = response.data;
            console.log('즐겨찾기 카운트:', favoriteCount);
            return typeof favoriteCount === 'number' && favoriteCount >= 0;
        }
    });

    // 요청 간 간격
    sleep(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'metrics.json': JSON.stringify(data),
    };
}
