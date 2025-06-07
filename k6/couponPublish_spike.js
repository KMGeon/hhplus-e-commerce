import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';
import exec from 'k6/execution';

// Grafana 대시보드에 맞는 메트릭 이름으로 변경
const successfulCoupons = new Counter('successful_coupons');
const errors = new Counter('errors');  // 변경된 이름
const successRate = new Rate('success_rate');

export const options = {
    scenarios: {
        spike_test: {
            executor: 'ramping-arrival-rate',
            startRate: 1,
            timeUnit: '1s',
            stages: [
                { duration: '30s', target: 1 },
                { duration: '5s', target: 100 },
                { duration: '1m', target: 200 },
                { duration: '30s', target: 70 },
                { duration: '1m', target: 25 },
                { duration: '2m', target: 8 },
                { duration: '10s', target: 0 },
            ],
            preAllocatedVUs: 10,
            maxVUs: 200,
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'],
        http_req_failed: ['rate<0.05'],
        success_rate: ['rate>0.95'],
    },
};

function generateUniqueUserId() {
    const vuId = exec.vu.idInTest;
    const iterationId = exec.scenario.iterationInInstance;
    const timestamp = Date.now() % 10000;
    return vuId * 100000 + iterationId * 100 + timestamp;
}

export default function() {
    const userId = generateUniqueUserId();

    const payload = JSON.stringify({
        userId: userId,
        couponId: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
        timeout: '10s',
    };

    const response = http.post('http://localhost:8080/api/v1/coupon/publish', payload, params);

    check(response, {
        'status is 200': (r) => r.status === 200,
        'has response body': (r) => r.body && r.body.length > 0,
    });

    if (response.status === 200) {
        successfulCoupons.add(1);
        successRate.add(1);
    } else {
        errors.add(1);  // Grafana 쿼리에 맞는 메트릭 이름
        successRate.add(0);
    }

    sleep(0.5 + Math.random() * 1.5);
}

export function teardown() {
    const successCount = successfulCoupons.value || 0;
    const errorCount = errors.value || 0;  // 변경된 변수명
    const total = successCount + errorCount;

    const successPercentage = total > 0 ? (successCount / total * 100).toFixed(2) : 0;

    console.log(`성공한 쿠폰 발급: ${successCount}개`);
    console.log(`실패한 요청: ${errorCount}개`);
    console.log(`전체 요청: ${total}개`);
    console.log(`성공률: ${successPercentage}%`);
}

/**
 k6 run \
 --out influxdb=http://localhost:8086/myk6db \
 couponPublish_spike.js
 */
