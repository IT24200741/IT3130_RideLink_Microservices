# 📸 Required Screenshots Checklist for Technical Report

Save all evidence screenshots in their respective folders here. These will be embedded directly into the final 8-12 page Technical Report PDF (IT3130 AD Assignment).

## 1. Per-Service Evidence (Sasiru, Randi, Bhanuka, Nethmini)
For each of your assigned microservices, save screenshots of:
- [ ] **Unit Tests Passing:** Terminal output of `./mvnw test -pl services/<service-name>` showing all test suites passing green (`BUILD SUCCESS`).
- [ ] **Swagger/OpenAPI UI:** Browser screenshot of `http://localhost:808X/swagger-ui.html` showing all documented REST endpoints.
- [ ] **Database Isolation:** Browser screenshot of H2 Console (`http://localhost:808X/h2-console`) showing isolated in-memory tables (`jdbc:h2:mem:<service>db`).

## 2. GitHub & PR Peer Review Evidence (Rubric I3: 3 Marks)
- [ ] **Pull Request Created:** Screenshot of the created PR showing Title, Description with checklist, and Branch mapping (`feature/IT24200741-account-service` ➔ `develop`).
- [ ] **GitHub Actions CI Run:** Green checkmark showing `build (Java 17)` automated Maven test passed on the PR.
- [ ] **Peer Review Approval:** Screenshot showing approval/comment from a teammate (`Approved` / `LGTM`).
- [ ] **PR Merged:** Purple badge showing `Merged` into `develop`.

## 3. Integrated Postman Evidence (Rubric G2, G3: 7 Marks)
- [ ] **Happy Path Run:** Postman Collection Runner execution showing 100% pass across all 4 services (Ports 8080-8083).
- [ ] **Negative Scenarios (At least 2):**
  - Duplicate registration (`409 Conflict`)
  - Suspended account login / invalid credentials (`403 Forbidden` / `401 Unauthorized`)
  - No available drivers / Invalid ride status transition (`404 Not Found` / `400 Bad Request`)
  - Simulated payment failure (`400 Bad Request`)

## 4. Final Release Evidence
- [ ] **Release Tag:** Screenshot of Git release `v1.0.0` on GitHub from `main` branch.
