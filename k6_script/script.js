import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// Prometheus 메트릭 정의
const loginCounter = new Counter('login_total');
const redisFavoriteCounter = new Counter('redis_favorite_operations_total');
const dbFavoriteCounter = new Counter('db_favorite_operations_total');
const errorCounter = new Counter('error_total');

// 테스트 옵션
export const options = {
    scenarios: {
        redis_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 200,
            stages: [
                { target: 50, duration: '10s' },
                { target: 50, duration: '20s' },
                { target: 0, duration: '10s' },
            ],
        },
        db_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 200,
            stages: [
                { target: 50, duration: '10s' },
                { target: 50, duration: '20s' },
                { target: 0, duration: '10s' },
            ],
        }
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
            accessToken: accessToken
        }
    };

    // Redis를 사용한 즐겨찾기 추가 (기본 API)
    const classId = 44;
    const addFavoriteRes = http.post(
        `${BASE_URL}/api/favorite/${classId}`,
        null,
        params
    );
    
    check(addFavoriteRes, {
        'Redis 즐겨찾기 추가 성공': (r) => r.status === 200,
    });
    redisFavoriteCounter.add(1);
    if (addFavoriteRes.status !== 200) {
        errorCounter.add(1);
        console.log('Redis 즐겨찾기 추가 실패:', addFavoriteRes.status, addFavoriteRes.body);
    }

    // DB를 사용한 즐겨찾기 추가 (새로운 API)
    const addFavoriteDbRes = http.post(
        `${BASE_URL}/api/favorite/db/${classId}`,
        null,
        params
    );
    
    check(addFavoriteDbRes, {
        'DB 즐겨찾기 추가 성공': (r) => r.status === 200,
    });
    dbFavoriteCounter.add(1);
    if (addFavoriteDbRes.status !== 200) {
        errorCounter.add(1);
        console.log('DB 즐겨찾기 추가 실패:', addFavoriteDbRes.status, addFavoriteDbRes.body);
    }

    sleep(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'metrics.json': JSON.stringify(data),
    };
}
