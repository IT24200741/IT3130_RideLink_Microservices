const request = require('supertest');
const app = require('../src/server');

describe('Account Service - Health Check', () => {
  it('should return status UP and owner Sasiru (IT24200741)', async () => {
    const res = await request(app).get('/health');
    expect(res.statusCode).toBe(200);
    expect(res.body.status).toBe('UP');
    expect(res.body.owner).toBe('Sasiru (IT24200741)');
  });
});
