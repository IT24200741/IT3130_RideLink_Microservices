const request = require('supertest');
const app = require('../src/server');

describe('Fare & Payment Service - Health Check', () => {
  it('should return status UP and owner Nethmini Perera (IT24104027)', async () => {
    const res = await request(app).get('/health');
    expect(res.statusCode).toBe(200);
    expect(res.body.status).toBe('UP');
    expect(res.body.owner).toBe('Nethmini Perera (IT24104027)');
  });
});
