import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const redisClassCounter = new Counter('redis_class_requests_total');
const errorCounter = new Counter('error_total');

export const options = {
    scenarios: {
        redis_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 100,
            stages: [
                { target: 50, duration: '5s' },    
                { target: 50, duration: '10s' },   
                { target: 0, duration: '5s' },     
            ],
        }
    }
};

export default function () {
    const BASE_URL = 'http://host.docker.internal:8080';
    const classId = 37; // 테스트할 인기 클래스 ID
    
    const redisClassRes = http.get(
        `${BASE_URL}/api/class/${classId}`,
        {
            headers: { 'Content-Type': 'application/json' }
        }
    );
    
    check(redisClassRes, {
        'Redis 클래스 상세 조회 성공': (r) => r.status === 200,
    });
    redisClassCounter.add(1);
    
    if (redisClassRes.status !== 200) {
        errorCounter.add(1);
        console.log('Redis 클래스 상세 조회 실패:', redisClassRes.status, redisClassRes.body);
    }

    sleep(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'metrics.json': JSON.stringify(data),
    };
} 