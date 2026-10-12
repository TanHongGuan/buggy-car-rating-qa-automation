# F04 Test Execution Summary — Voting & Comments

**Report prepared:** 2026-10-13

**Execution evidence:** 2026-10-12; times below are UTC+08:00.

This report summarizes recorded results; no tests were rerun to prepare it. Sources are the [F04 specification and execution evidence](../agents/F04.md), [implemented voting tests](../src/test/java/com/bgr/qa/voting/), and [Maven configuration](../pom.xml).

## 1. Executive Summary

F04 testing evaluates guest restrictions, voting eligibility, repeat-vote prevention, session transitions, displayed vote counts, and comment handling and feedback through the public Buggy Cars Rating UI.

The latest normal regression completed successfully: **14 tests passed**, comprising **13 F04 cases and one homepage smoke test**. Separate execution of TC13 failed because its submitted comment persisted after refresh but its Author cell was empty. Two cases remain manual-first. These results support the tested UI behaviors, with the outstanding attribution discrepancy and coverage limitations described below.

## 2. Scope and Environment

| Item | Repository-supported detail |
|---|---|
| Application | [Buggy Cars Rating](https://buggy.justtestit.org/), public demo |
| Approach | Documented manual black-box testing followed by Java/Selenium UI automation |
| Scope | Voting and comments on the configured car model; registration/login used for account preparation |
| Language | Java compiler compatibility set to `release 17`; this does not establish execution on a Java 17 runtime |
| Automation | Selenium WebDriver 4.35.0, TestNG 7.11.0 |
| Build | Maven; Compiler plugin 3.14.0 and Surefire 3.5.3 |
| Browser | Google Chrome through ChromeDriver; recorded warnings identify Chrome 154 |
| Isolation and cleanup | Fresh UI-registered accounts where needed, explicit waits, independent browser sessions and always-run cleanup |
| Access | Public UI only; no application source, backend or database access |

The [README's historical validation](../README.md#validation-on-2026-10-11) records Maven 3.10.0, JDK 27 and Chrome 154.0.8037.98 for 2026-10-11. Those exact runtime versions are not independently established for the final run by this report. The final execution record notes non-blocking SLF4J and Chrome CDP warnings.

## 3. Test Coverage

| Measure | Count |
|---|---:|
| Approved manual cases | 16 |
| Automated cases implemented | 14 |
| Implementation coverage | **87.5% (14/16)** |
| Automated cases passing in latest recorded execution | 13 |
| Automated known failures | 1 — TC13 |
| Manual-first cases without automation | 2 — TC10, TC14 |

**Implementation coverage is not the pass rate.** It measures cases with automation, including failing TC13. Across the two recorded executions, 13 of 14 automated F04 cases passed (approximately 92.9%). The separate smoke test is excluded from both F04 calculations. Implemented cases also have procedural limitations; 87.5% does not imply equivalent coverage of every manual action or requirement.

## 4. Regression Execution

| Execution | Maven result | Tests | Failures | Errors | Skips | Finished on 2026-10-12 |
|---|---|---:|---:|---:|---:|---|
| Normal regression | BUILD SUCCESS; exit 0 | 14 | 0 | 0 | 0 | 17:01:31 |
| TC13 separately | BUILD FAILURE; exit 1 | 1 | 1 | 0 | 0 | 17:02:27 |

Each command was run once, without automatic submission retries:

```sh
mvn clean test
```

Normal execution took 176.7 seconds in Surefire; Maven reported 2:59 minutes total. The recorded XML review confirmed 13 normal F04 methods plus smoke, with TC13 absent—not passed or skipped.

```sh
mvn -DexcludedGroups= -Dgroups=known-failure '-Dtest=com.bgr.qa.voting.CommentPersistenceTest#commentPersistsWithCorrectAuthorAfterRefresh' test
```

TC13 took 36.79 seconds in Surefire; Maven reported 38.468 seconds total. Its `known-failure` group is excluded by default to separate the documented discrepancy from normal regression. Clearing the exclusion permits explicit execution and still produces a real build failure. Its nonempty-author and exact-display-name assertions remain intact; it is not disabled and failures are not globally ignored.

## 5. Test Case Results

Automation results below refer to 2026-10-12. Manual results are historical entries in the specification, not reruns during the final regression. TC13's historical manual PASS and current automation FAIL are deliberately distinguished.

| Case | Scenario | Latest automation | Historical manual | Execution selection |
|---|---|---|---|---|
| TC01 | Guest vote restriction | PASS | PASS | Normal |
| TC02 | Guest comment restriction | PASS | PASS | Normal |
| TC03 | First eligible vote | PASS | PASS | Normal |
| TC04 | Login without refresh | PASS | PASS | Normal |
| TC05 | Second vote without refresh | PASS | PASS | Normal |
| TC06 | Repeat-vote restriction after refresh | PASS | PASS | Normal |
| TC07 | Repeat-vote restriction after revisit | PASS | PASS | Normal |
| TC08 | Voting restriction after logout | PASS | PASS | Normal |
| TC09 | Voting restriction after logout/refresh | PASS | PASS | Normal |
| TC10 | Long-comment layout | Not automated | FAIL | Manual-first |
| TC11 | Special-symbol comment | PASS | PASS | Normal |
| TC12 | Vote-count change | PASS | PASS | Normal |
| TC13 | Comment persistence and attribution | **FAIL — empty Author** | PASS | `known-failure` |
| TC14 | Comment metadata | Not automated | FAIL | Manual-first |
| TC15 | Vote success feedback | PASS | PASS | Normal |
| TC16 | Comment submission feedback | PASS | PASS | Normal |

Recorded observations include TC12's **2166 → 2167 (+1)** count, TC15's **“Thank you for your vote!”**, TC11's text rendering with safe HTML escaping, and TC16's immediate display of the exact submitted comment. Class and method traceability is maintained in the [F04 automation mapping](../agents/F04.md#3-automation-mapping-and-status).

## 6. Defect Summary

| Reference | Evidence and status |
|---|---|
| VOTE-DF01 / Jira KAN-1 | Historical manually observed long-comment layout overflow, associated with TC10. Not reverified in the final regression. |
| VOTE-DF02 / Jira KAN-2 | Provisional missing-author-metadata issue associated with TC14; requirement confirmation remains outstanding. |
| TC13 automation failure | The exact submitted comment persisted after refresh, but Author was empty. The nonempty-author assertion failed; the subsequent display-name comparison was not reached. |

TC13 may relate to KAN-2, but the evidence does **not** establish that they are definitively the same defect or confirm its product-defect classification.

## 7. Limitations and Residual Risks

- **UI prevention versus server enforcement:** Guest and repeat-vote tests often verify removed controls. They do not force prohibited submissions or prove server-side rejection. TC03 does not independently establish exactly one backend record.
- **Scenario boundaries:** TC04 covers a fresh eligible account only. TC07 leaves through the UI and returns by direct URL. The smoke test checks a nonempty homepage title only.
- **Public-demo concurrency:** Other users may affect displayed vote totals. TC12 observed exactly +1 but does not isolate concurrent voting or prove backend attribution.
- **Special symbols:** TC11 covers the approved symbol sequence with a unique marker. Safe escaping is accepted; no child markup/dialog was observed. The input contains no executable script payload, so this is not comprehensive XSS protection.
- **Comment feedback:** TC16 interprets immediate inline comment rendering as outcome feedback. It does not verify a dedicated comment notification or use vote feedback as a substitute.
- **Outstanding coverage:** TC13 remains a real failure outside normal regression. TC10 and TC14 have no automated coverage and their historical manual failures were not reverified. Comment submission also casts a vote; test data persists in the demo.
- **Evidence boundaries:** The original spreadsheet was not independently available for comparison in the documented revision. The README and root guidance retain older baseline details; current coverage uses F04's reconciled mapping and Java code. No screenshot evidence, new manual execution, cross-browser validation or backend verification is claimed here.

## 8. Final QA Assessment

The recorded execution supports the implemented UI restrictions, eligible voting, displayed count update, and tested comment-rendering and feedback behaviors. Normal regression is green for its selected scope; the separately executed attribution check remains red.

Outstanding work includes resolving or clarifying the author-attribution discrepancy, reviewing the manual-first layout and metadata findings, and retaining explicit awareness of UI-only and feedback-interpretation limits. These results do not establish that the application is defect-free or production-ready.
