import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const loginCounter = new Counter('login_total');
const dbFavoriteCounter = new Counter('db_favorite_operations_total');
const errorCounter = new Counter('error_total');

export const options = {
    scenarios: {
        db_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 100,
            stages: [
                { target: 30, duration: '5s' },    // 5초 동안 초당 30건까지 증가
                { target: 30, duration: '10s' },   // 10초 동안 초당 30건 유지
                { target: 0, duration: '5s' },     // 5초 동안 0건까지 감소
            ],
        }
    }
};

export default function () {
    const BASE_URL = 'http://host.docker.internal:8080';
    
    const loginRes = http.post(`${BASE_URL}/api/members/login`, JSON.stringify({
        email: 'tigerrla@naver.com',
        password: '1234',
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    check(loginRes, {
        '로그인 성공': (r) => r.status === 200,
    });
    loginCounter.add(1);

    const cookies = loginRes.headers['Set-Cookie'];
    let accessToken = null;
    if (typeof cookies === 'string') {
        const tokenMatch = cookies.match(/accessToken=([^;]+)/);
        if (tokenMatch) {
            accessToken = tokenMatch[1];
            console.log('추출된 토큰:', accessToken); // 디버깅용
        }
    }
    
    if (!accessToken) {
        console.log('액세스 토큰을 찾을 수 없습니다.');
        return;
    }

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        cookies: {
            'accessToken': accessToken
        }
    };

    // 쿠키는 제거하고 Authorization 헤더만 사용
    console.log('요청 헤더:', params.headers); // 디버깅용

    const classId = 37;
    const addFavoriteRes = http.post(
        `${BASE_URL}/api/favorite/db/${classId}`,
        null,
        params
    );
    
    check(addFavoriteRes, {
        'DB 즐겨찾기 추가 성공': (r) => r.status === 200,
    });
    dbFavoriteCounter.add(1);
    if (addFavoriteRes.status !== 200) {
        errorCounter.add(1);
        console.log('DB 즐겨찾기 추가 실패:', addFavoriteRes.status, addFavoriteRes.body);
    }

    sleep(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'metrics.json': JSON.stringify(data),
    };
} 