import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// Prometheus 메트릭 정의
const loginCounter = new Counter('login_total');
const apiCallCounter = new Counter('api_call_total');

export default function () {
    const BASE_URL = 'http://host.docker.internal:8080';
    // 1. 로그인 API 호출 (POST)
    const loginRes = http.post(`${BASE_URL}/api/members/login`, JSON.stringify({
        email: 'mentee@test.com',
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
        }
    }
    
    if (!accessToken) {
        console.log('액세스 토큰을 찾을 수 없습니다.');
        return;
    }

    console.log('추출된 액세스 토큰:', accessToken);

    // 4. 인증 헤더로 실제 API 호출
    const params = {
        headers: {
            'Content-Type': 'application/json'
        },
        cookies: {
            accessToken: accessToken
        }
    };

    console.log('k6 요청 params:', JSON.stringify(params));
    const memberId = 2; // 실제 로그인한 사용자의 memberId로 대체
    const res = http.get(`${BASE_URL}/api/favorite/${memberId}`, params);
    console.log('API 응답 상태:', res.status);
    console.log('API 응답 헤더:', JSON.stringify(res.headers));
    console.log('API 응답 본문:', res.body);

    console.log('Login response body:', loginRes.body);
    console.log('Access token:', accessToken);
    check(res, {
        'API 호출 성공': (r) => r.status === 200,
    });
    apiCallCounter.add(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'metrics.json': JSON.stringify(data),
    };
}
