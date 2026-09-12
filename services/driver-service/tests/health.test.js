const request = require('supertest');
const app = require('../src/server');

describe('Driver & Vehicle Service - Health Check', () => {
  it('should return status UP and owner Randi Sithma (IT24104341)', async () => {
    const res = await request(app).get('/health');
    expect(res.statusCode).toBe(200);
    expect(res.body.status).toBe('UP');
    expect(res.body.owner).toBe('Randi Sithma (IT24104341)');
  });
});
