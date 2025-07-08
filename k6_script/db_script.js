import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const dbClassCounter = new Counter('db_class_requests_total');
const errorCounter = new Counter('error_total');

export const options = {
    scenarios: {
        db_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 200,
            stages: [
                // { target: 50, duration: '30s' },
                { target: 150, duration: '30s' },
                // { target: 0, duration: '10s' },
              ]
        }
    }
};

export default function () {
    const BASE_URL = 'http://host.docker.internal:8080';
    const classId = 66; // 테스트할 인기 x 클래스 ID
    
    const dbClassRes = http.get(
        `${BASE_URL}/api/class/${classId}`,
        {
            headers: { 'Content-Type': 'application/json' }
        }
    );
    
    check(dbClassRes, {
        'DB 클래스 상세 조회 성공': (r) => r.status === 200,
    });
    dbClassCounter.add(1);
    
    if (dbClassRes.status !== 200) {
        errorCounter.add(1);
        console.log('DB 클래스 상세 조회 실패:', dbClassRes.status, dbClassRes.body);
    }

    sleep(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'output/metrics_db.json': JSON.stringify(data),
    };
} 