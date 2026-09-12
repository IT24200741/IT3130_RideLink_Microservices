# 📸 Required Screenshots Checklist for Technical Report

Save all evidence screenshots in their respective folders here. These will be embedded directly into the final 8-12 page Technical Report PDF.

## 1. Per-Service Evidence (Sasiru, Randi, Bhanuka, Nethmini)
For each of your assigned microservices, save screenshots of:
- [ ] **Unit Tests Passing:** Terminal output of `npm test` showing all test suites passing green (Positive & Negative tests).
- [ ] **Swagger/OpenAPI UI:** Browser screenshot of `http://localhost:500X/api-docs` showing all endpoints.
- [ ] **Database Isolation:** Database client (e.g. MongoDB Compass / DBeaver) showing your service's isolated database and collections.

## 2. Integrated Postman Evidence
- [ ] **Happy Path Run:** Postman Collection Runner execution showing 100% pass for all 4 services.
- [ ] **Negative Scenarios (At least 2):**
  - Unauthorized access / Invalid token (`401 Unauthorized`)
  - No available drivers / Invalid ride status transition (`400/409 Conflict`)
  - Simulated payment failure (`402/400 Payment Failed`)

## 3. GitHub & CI Pipeline Evidence
- [ ] **GitHub Actions CI Run:** Green checkmarks showing automated tests ran and passed on PR.
- [ ] **Pull Request Peer Review:** Screenshot of PR comments showing approval from a teammate.
- [ ] **Release Tag:** Screenshot of Git release `v1.0.0` on GitHub.
