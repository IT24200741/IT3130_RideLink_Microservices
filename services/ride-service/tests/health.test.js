const request = require('supertest');
const app = require('../src/server');

describe('Ride Management Service - Health Check', () => {
  it('should return status UP and owner Bhanuka (IT24103298)', async () => {
    const res = await request(app).get('/health');
    expect(res.statusCode).toBe(200);
    expect(res.body.status).toBe('UP');
    expect(res.body.owner).toBe('Bhanuka (IT24103298)');
  });
});
