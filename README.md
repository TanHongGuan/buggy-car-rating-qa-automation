# Buggy Cars Rating QA Automation

Public-UI portfolio tests using Java 17 language compatibility, Selenium, TestNG,
Maven and Chrome. Install a JDK (17 or later), Maven and Chrome. Selenium Manager
resolves ChromeDriver; initial setup may require network access.

## Coverage

| Class | Coverage |
| --- | --- |
| `WebsiteSmokeTest` | Homepage has a non-empty title |
| `GuestVotingTest` | VOTE-TC01: guests have no Vote button; VOTE-TC02: guests have no comment input |
| `EligibleVotingTest` | VOTE-TC03: newly registered user logs in, clicks Vote! once, sees success feedback and the Vote button disappear |

```sh
mvn test-compile
mvn -Dtest=EligibleVotingTest test
mvn test
```

The last two commands each execute TC03. To minimize demo data, use just `mvn test`
to validate TC03 and the existing tests together. For checks without account creation:

```sh
mvn -Dtest=WebsiteSmokeTest,GuestVotingTest test
```

## TC03 account data and behavior

Each TC03 execution creates exactly one unique account through the public registration
form. `UiAccountHelper` generates a UUID-based username and a random password in memory;
there are no hardcoded credentials, credential files or required environment variables.
The helper is called only from TC03. A fresh account has no prior votes, and the test
also requires an enabled Vote button and no existing success message before submission.
There are no registration or vote retries. Re-running TC03 creates another account and
vote, so avoid repeated runs or unattended loops against this public demo.

The live registration form inspected on 2026-10-11 required no email verification,
CAPTCHA or manual step. If that changes, stop and reassess account preparation; do not
bypass controls. Registration/login failures are setup failures, not proof of a voting
defect. Accounts and votes remain on the demo; browser cleanup does not delete them.

The helper waits for `Registration is successful`, fills the navigation login fields,
and waits for `Logout` to establish authentication. TC03 waits up to 30 seconds per
condition, clicks `//button[normalize-space()='Vote!']` once, then checks the observed
paragraph `Thank you for your vote!` and removal of the button. The optional comment
is left blank. No exact vote-total assertion is used because others can vote concurrently.

This covers the UI-observable scenario described in the supplied TC03 prompt. It does
not verify database persistence, exactly one server-side record, or duplicate-vote
prevention. No spreadsheet was supplied for an independent row-by-row comparison.

## Reading the test

- `@BeforeMethod` creates a fresh, non-static driver and prepares the account before the test.
- `@Test` identifies the scenario TestNG executes; assertions fail it if expectations differ.
- `@AfterMethod(alwaysRun = true)` closes Chrome even when setup or assertions fail.
- `private static final By` stores a shared immutable locator, not a shared browser.
- `WebDriverWait.until(...)` polls a condition instead of using fixed sleeps.
- `UUID.randomUUID()` makes account collisions very unlikely; `String` holds text.
- `try/finally` clears the driver reference even if `quit()` throws an exception.
- The helper has package visibility and a private constructor because it only supplies
  a reusable static operation; a larger Page Object refactor is unnecessary here.

## Validation on 2026-10-11

- Baseline: `mvn test-compile` succeeded; `mvn test` passed all 3 existing tests.
- After implementation: `mvn test-compile test` succeeded; all 4 tests passed,
  with 0 failures, 0 errors and 0 skips. TC03 ran once within that full suite.
- Validation used Maven 3.10.0, JDK 27 with compiler `release 17`, and Chrome
  154.0.8037.98. Execution on a Java 17 runtime was not separately verified.
- Non-blocking warnings: Apache snapshots metadata timed out; SLF4J had no logging
  provider; Selenium had no matching Chrome 154 CDP module. Standard WebDriver
  interactions still passed; this test does not use CDP.
- Live inspection created one account/vote; Selenium validation created one more,
  both explicitly approved. No other account or vote retries were performed.
- Existing guest-test edits and the untracked smoke-test file were preserved.
