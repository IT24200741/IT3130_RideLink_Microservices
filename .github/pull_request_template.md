## 📌 Microservice Pull Request

### 👤 Author
- **Service Name:** [e.g. Account Service / Driver Service / Ride Service / Payment Service]
- **Owner:** [Sasiru / Randi Sithma / Bhanuka / Nethmini Perera]
- **Feature Branch:** [e.g. feature/account-auth]

---

### 📝 Summary of Changes
- Briefly describe the endpoints, models, or logic implemented in this PR.

---

### ✅ Quality Checklist (IT3130 Rubric Compliance)
- [ ] **Database Isolation:** This service strictly accesses its own database. No direct cross-service queries or shared schemas.
- [ ] **Automated Tests:** Unit test suite updated and passing with positive and negative test cases (
pm test).
- [ ] **Error Handling:** Returns proper HTTP status codes (400, 401, 403, 404, 409, 500) with structured JSON error messages.
- [ ] **Security:** No API keys, database credentials, or secrets committed (all loaded via .env).
- [ ] **Documentation:** Endpoints and request/response models documented according to API contracts.

---

### 🔍 Peer Review (Required before merge to develop/main)
- **Reviewed by:** @[teammate_github_username]
- **Approval Date:** YYYY-MM-DD
