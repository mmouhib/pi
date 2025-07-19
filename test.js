import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 1000,
    duration: '30s',
};

const BASE_URL = 'http://localhost:8089/kaddem/departement';

export default function () {
    let payload = JSON.stringify({
        idDepart: 0,
        nomDepart: `TestDept-${Math.floor(Math.random() * 1000)}`,
    });

    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let postRes = http.post(`${BASE_URL}/add-departement`, payload, params);
    check(postRes, {
        'POST status is 200': (r) => r.status === 200 || r.status === 201,
        'POST response contains idDepart': (r) => r.body.includes('idDepart'),
    });

    let getRes = http.get(`${BASE_URL}/retrieve-all-departements`);
    check(getRes, {
        'GET status is 200': (r) => r.status === 200,
        'GET response has at least one department': (r) => r.json().length > 0,
    });

    sleep(1);
}
