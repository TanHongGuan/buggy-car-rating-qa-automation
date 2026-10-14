# Buggy Cars Rating QA Automation

[![Safe QA smoke tests](https://github.com/TanHongGuan/buggy-car-rating-qa-automation/actions/workflows/ci.yml/badge.svg)](https://github.com/TanHongGuan/buggy-car-rating-qa-automation/actions/workflows/ci.yml)

## Project Overview

An independent black-box QA portfolio project for the [Buggy Cars Rating public demo](https://buggy.justtestit.org/). It brings together manual test design and execution, defect documentation, Selenium automation, and regression reporting.

Current scope is **F04 — Voting & Comments**: guest restrictions, eligible and repeat voting, login/logout transitions, vote counts, comment rendering, persistence and feedback. All verification uses the public UI; there is no access to application source code or backend data.

## Technology Stack

| Component | Repository configuration |
|---|---|
| Java | Compiler `release 17` language compatibility |
| Selenium WebDriver | 4.35.0 |
| TestNG | 7.11.0 |
| Maven plugins | Compiler 3.14.0; Surefire 3.5.3 |
| Browser | Google Chrome / ChromeDriver; Selenium Manager resolves the driver |

Versions are defined in [pom.xml](pom.xml). Java 17 compatibility does not establish that the tests were executed on a Java 17 runtime; historical runtime details are recorded below.

## Testing Workflow

1. **Requirements and risk analysis:** identify observable behaviors and risks, recorded with scenario IDs, priorities and test techniques.
2. **Manual test design and execution:** document preconditions, data, steps, expected results and historical outcomes.
3. **Defect documentation:** record observed discrepancies and distinguish provisional issues from confirmed observations.
4. **Automation implementation:** map approved cases to independent TestNG methods using fresh accounts where needed, explicit waits and always-run browser cleanup.
5. **Regression and reporting:** execute normal regression and the known failure separately; reconcile results and coverage limitations with the specification.

## F04 — Voting & Comments

Latest recorded verification: **2026-10-12**, UTC+08:00. These are recorded results, not a guarantee of the public demo's current behavior.

| Measure | Result |
|---|---|
| Documented manual cases | 16 |
| Automated cases implemented | 14 — **87.5% implementation coverage** |
| Passing automated F04 cases | 13 |
| Automated known failure | TC13 — comment persists, Author cell empty |
| Manual-first cases | TC10 (long-comment layout), TC14 (metadata) |
| Normal regression | **BUILD SUCCESS** — 14 passed, 0 failures/errors/skips; includes one smoke test |
| Separate TC13 execution | **BUILD FAILURE** — 1 test, 1 failure, 0 errors/skips |

Implementation coverage is **14/16 cases**, including TC13. It is not the pass rate or a claim that every manual action is fully automated. The smoke test is additional to F04 and checks that the homepage title is nonempty.

Tests are grouped by related scenarios in [the voting package](src/test/java/com/bgr/qa/voting/):

| Class | Cases |
|---|---|
| `GuestVotingTest` | TC01–TC02 |
| `VotingEligibilityTest` | TC03–TC04, TC15 |
| `RepeatVotingTest` | TC05–TC07 |
| `VotingSessionTest` | TC08–TC09 |
| `VotingCountTest` | TC12 |
| `CommentPersistenceTest` | TC11, TC13, TC16 |

## Running the Tests

Install a **JDK 17 or later**, **Maven** and **Google Chrome**, with Java and Maven available on your command path. Run commands from the repository root. Network access is needed for the demo and, initially, Maven dependencies and Selenium Manager's ChromeDriver resolution.

No account credentials or environment variables are required. The [account helper](src/test/java/com/bgr/qa/helpers/UiAccountHelper.java) registers unique users through the UI and keeps generated credentials in memory. Registration/login failures are setup failures, not proof of a voting defect. Reassess preparation if the demo introduces verification steps; do not bypass them.

Compile without executing tests:

```sh
mvn test-compile
```

Run normal regression:

```sh
mvn clean test
```

Run TC13 separately using the documented command:

```sh
mvn -DexcludedGroups= -Dgroups=known-failure '-Dtest=com.bgr.qa.voting.CommentPersistenceTest#commentPersistsWithCorrectAuthorAfterRefresh' test
```

`known-failure` is a custom TestNG group. Surefire excludes it by default because TC13 has a documented empty-author discrepancy. The explicit command clears that exclusion and selects TC13. Selecting only `-Dtest` or `-Dgroups` does not clear the exclusion. TC13 retains its original assertions: it is not disabled, and an assertion failure still fails the build.

For smoke and guest checks without account creation or vote submission:

```sh
mvn -Dtest=WebsiteSmokeTest,GuestVotingTest test
```

Execution produces local Surefire reports under `target/surefire-reports/`. Tests do not automatically retry registration or submissions. Avoid unnecessary reruns: accounts, votes and comments persist on the demo, and browser cleanup does not remove them. Comment submission also casts a vote.

## Continuous Integration

The [Safe QA smoke tests workflow](.github/workflows/ci.yml) runs on **pushes and pull requests** using an Ubuntu 24.04 GitHub-hosted runner, Temurin JDK 17 and Maven dependency caching.

GitHub Actions invokes Maven with an explicit method selection. Maven Surefire uses `-Dtest` to select these TestNG methods, and Selenium WebDriver runs their browser checks in headless Chrome:

- `WebsiteSmokeTest.verifyWebsiteLoads`
- `GuestVotingTest.guestCannotSeeVoteButton`
- `GuestVotingTest.guestCannotSeeCommentSection`

```sh
mvn -B -ntp '-Dtest=com.bgr.qa.WebsiteSmokeTest#verifyWebsiteLoads,com.bgr.qa.voting.GuestVotingTest#guestCannotSeeVoteButton+guestCannotSeeCommentSection' clean test
```

The workflow sets `HEADLESS=true`; the selected classes enable headless Chrome with a fixed viewport. Chrome is provided by the hosted runner, and Selenium Manager resolves ChromeDriver without a hard-coded driver path. Local runs remain headed unless headless mode is explicitly enabled.

**CI selects three read-only tests, not the entire F04 regression suite.** Account registration, voting and comment tests are excluded because they change persistent data on the shared public demo. TC13's separate `known-failure` handling remains unchanged.

The [first hosted run](https://github.com/TanHongGuan/buggy-car-rating-qa-automation/actions/runs/38581993081) completed with **SUCCESS** (reported workflow duration: **36 seconds**) and successfully uploaded the `surefire-reports` artifact. Public job metadata confirms the job, test step and upload step succeeded. The hosted logs/Surefire XML were not available for independent inspection during this documentation update, so an exact hosted test count and per-method pass results are not asserted here.

Surefire reports from `target/surefire-reports/` are uploaded as GitHub Actions artifacts even when tests fail, when report files exist. Maven/TestNG failures remain visible as job failures. This workflow provides continuous integration only; it does not deploy the application.

## Test Evidence and Defect Records

- [F04 specification, case mapping and execution evidence](agents/F04.md): approved manual cases, automation traceability and historical results.
- [F04 Test Execution Summary](reports/F04_Test_Execution_Summary.md): consolidated results, defects and residual risks.
- [Voting and comment tests](src/test/java/com/bgr/qa/voting/), [shared helpers](src/test/java/com/bgr/qa/helpers/), and [homepage smoke test](src/test/java/com/bgr/qa/WebsiteSmokeTest.java).
- [Documented defect references](agents/F04.md#5-existing-defects-and-cautions): **VOTE-DF01 / KAN-1**, historical long-comment layout overflow; **VOTE-DF02 / KAN-2**, provisional missing-author metadata. TC13 may relate to KAN-2, but that relationship and product-defect classification are not confirmed. No public Jira URLs are supplied.

### Validation on 2026-10-11

Historical setup evidence records Maven **3.10.0**, **JDK 27** with compiler `release 17`, and Chrome **154.0.8037.98**. That earlier four-test baseline is superseded by the F04 results above. At that historical validation, execution on a Java 17 runtime was not separately verified; these exact runtime versions are not independently established here for the final F04 regression. The later hosted smoke workflow uses JDK 17, as described above. The final record notes non-blocking SLF4J and Chrome CDP warnings.

## Limitations

- **UI-level evidence only:** absent guest/repeat-vote controls demonstrate UI prevention, not server-side rejection or exactly-once database writes. There is no API or backend coverage.
- **Public-demo concurrency:** other users can affect displayed vote counts; test data and submissions persist in the shared environment.
- **Known failure and manual scope:** TC13 is excluded from normal regression but explicitly runnable. TC10 and TC14 remain manual-first; their historical manual failures were not reverified in the final run.
- **Security scope:** the approved special-symbol case checks text rendering and safe escaping, not comprehensive XSS protection or security assurance.
- **Feedback interpretation:** TC16 checks immediate inline rendering of the submitted comment, not a dedicated comment-success notification.
- **Evidence boundaries:** independent comparison with the original spreadsheet remains unverified. Root guidance retains an older baseline; use the current F04 mapping and execution report for coverage. Passing the selected regression does not establish that the application is defect-free or production-ready.
