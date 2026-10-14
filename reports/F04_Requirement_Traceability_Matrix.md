# F04 Requirement Traceability Matrix

## Purpose and evidence basis

This matrix traces all 16 documented Voting & Comments cases to their existing risks,
priorities, Java methods, recorded outcomes and defect references. Definitions and
expected-result wording come from the [F04 specification](../agents/F04.md#2-approved-manual-test-cases-spreadsheet).
No new requirement IDs or approvals are assigned.

**Business requirement IDs: Not documented.** The first column contains the documented
risk IDs and scenario IDs, not invented business requirements. The observable-behavior
column reproduces each approved expected result verbatim; implementation limits appear
separately. Full preconditions, test data and steps remain in the specification.

**Result baseline:** the recorded 2026-10-12 final regression and separate known-failure
execution in the [execution summary](F04_Test_Execution_Summary.md#4-regression-execution)
and [F04 execution evidence](../agents/F04.md#final-regression-and-coverage-review--2026-10-12).
Automated results below refer to that baseline, not a new run. Historical manual results
are explicitly labelled. The README is contextual documentation, not independent proof
of a passing test.

## Traceability matrix

Automation classes are in `com.bgr.qa.voting`; links point to their actual Java sources.
“Not documented” in the defect column means no relationship is recorded, not proof
that no defect exists.

| Requirement / Risk ID | Requirement or Observable Behavior | Manual Test Case | Priority | Automation Method | Latest Result | Defect Reference | Coverage limitations |
|---|---|---|---|---|---|---|---|
| VOTE-R01 / VOTE-TS01 | No guest vote is recorded; login restriction is observable | VOTE-TC01 — Guest attempts vote | High | [GuestVotingTest.guestCannotSeeVoteButton](../src/test/java/com/bgr/qa/voting/GuestVotingTest.java) | Automated PASS; historical manual PASS | Not documented | Absent Vote!; no guest submission or server rejection exercised. |
| VOTE-R02 / VOTE-TS02 | Guest comment is not published | VOTE-TC02 — Guest attempts comment | High | [GuestVotingTest.guestCannotSeeCommentSection](../src/test/java/com/bgr/qa/voting/GuestVotingTest.java) | Automated PASS; historical manual PASS | Not documented | Absent input; no guest submission or independent publication check. |
| VOTE-R03 / VOTE-TS03 | Eligible vote is handled once with appropriate feedback | VOTE-TC03 — First eligible vote | High | [VotingEligibilityTest.firstEligibleVoteIsAccepted](../src/test/java/com/bgr/qa/voting/VotingEligibilityTest.java) | Automated PASS; historical manual PASS | Not documented | UI acceptance only; no backend exactly-once proof. |
| VOTE-R03 / VOTE-TS03 | Voting eligibility reflects actual account vote history | VOTE-TC04 — Immediate post-login voting state | High | [VotingEligibilityTest.loginEnablesVotingWithoutRefresh](../src/test/java/com/bgr/qa/voting/VotingEligibilityTest.java) | Automated PASS; historical manual PASS | Not documented | Fresh eligible account only. |
| VOTE-R03 / VOTE-TS03 | Second vote is prevented | VOTE-TC05 — Second vote without refresh | High | [RepeatVotingTest.secondVoteIsPreventedWithoutRefresh](../src/test/java/com/bgr/qa/voting/RepeatVotingTest.java) | Automated PASS; historical manual PASS | Not documented | Removed control; no actual second submission. |
| VOTE-R04 / VOTE-TS04 | Previous vote remains enforced | VOTE-TC06 — Vote restriction after refresh | High | [RepeatVotingTest.repeatVoteIsPreventedAfterRefresh](../src/test/java/com/bgr/qa/voting/RepeatVotingTest.java) | Automated PASS; historical manual PASS | Not documented | Absent control after refresh; no second submission. |
| VOTE-R04 / VOTE-TS04 | Previous vote remains enforced | VOTE-TC07 — Vote restriction after revisit | High | [RepeatVotingTest.repeatVoteIsPreventedAfterRevisit](../src/test/java/com/bgr/qa/voting/RepeatVotingTest.java) | Automated PASS; historical manual PASS | Not documented | UI departure, direct URL return; no second submission. |
| VOTE-R05 / VOTE-TS05 | Unauthenticated vote is rejected | VOTE-TC08 — Vote after logout | High | [VotingSessionTest.logoutRestrictsVotingWithoutRefresh](../src/test/java/com/bgr/qa/voting/VotingSessionTest.java) | Automated PASS; historical manual PASS | Not documented | Guest controls after logout; no submission. |
| VOTE-R05 / VOTE-TS05 | Unauthenticated vote is rejected | VOTE-TC09 — Vote after logout and refresh | High | [VotingSessionTest.logoutAndRefreshRestrictsVoting](../src/test/java/com/bgr/qa/voting/VotingSessionTest.java) | Automated PASS; historical manual PASS | Not documented | Guest controls after refresh; no submission. |
| VOTE-R06 / VOTE-TS06 | Validation or rendering does not overflow or break page | VOTE-TC10 — Long comment layout | Medium | Manual-first — not automated | Historical manual FAIL; not rerun | [VOTE-DF01 / KAN-1](../agents/F04.md#5-existing-defects-and-cautions) — historical layout issue | Not automated or reverified in final regression. |
| VOTE-R07 / VOTE-TS07 | Content is handled as text; no markup execution | VOTE-TC11 — Special symbols in comments | Medium | [CommentPersistenceTest.specialSymbolCommentIsHandledAsText](../src/test/java/com/bgr/qa/voting/CommentPersistenceTest.java) | Automated PASS; historical manual PASS | Not documented | Approved symbols only; safe escaping; not comprehensive XSS testing. |
| VOTE-R08 / VOTE-TS08 | Count reflects one successful vote, not more | VOTE-TC12 — Vote count change | High | [VotingCountTest.oneVoteIncreasesDisplayedCountByOne](../src/test/java/com/bgr/qa/voting/VotingCountTest.java) | Automated PASS; historical manual PASS | Not documented | Displayed +1; public concurrency not isolated. |
| VOTE-R09 / VOTE-TS09 | Successfully submitted comment persists with correct author | VOTE-TC13 — Comment persists after refresh | Medium | [CommentPersistenceTest.commentPersistsWithCorrectAuthorAfterRefresh](../src/test/java/com/bgr/qa/voting/CommentPersistenceTest.java) | Automated FAIL — known failure (`known-failure`); historical manual PASS | Possible relation to [VOTE-DF02 / KAN-2](../agents/F04.md#5-existing-defects-and-cautions); not confirmed as the same defect | Persistence passed; Author empty; name comparison not reached. |
| VOTE-R09 / VOTE-TS09 | Attribution matches account; displayed metadata is internally consistent | VOTE-TC14 — Comment metadata displayed | Medium | Manual-first — not automated | Historical manual FAIL; not rerun | [VOTE-DF02 / KAN-2](../agents/F04.md#5-existing-defects-and-cautions) — provisional | Not automated or reverified; defect remains provisional. |
| VOTE-R10 / VOTE-TS10 | Clear success or failure outcome is displayed | VOTE-TC15 — Vote success feedback | Medium | [VotingEligibilityTest.eligibleVoteDisplaysSuccessFeedback](../src/test/java/com/bgr/qa/voting/VotingEligibilityTest.java) | Automated PASS; historical manual PASS | Not documented | Success-path notification only. |
| VOTE-R10 / VOTE-TS10 | Submission outcome is communicated clearly | VOTE-TC16 — Comment submission feedback | Medium | [CommentPersistenceTest.commentSubmissionDisplaysImmediateFeedback](../src/test/java/com/bgr/qa/voting/CommentPersistenceTest.java) | Automated PASS; historical manual PASS | Not documented | Inline rendering interpreted as feedback; no dedicated comment notification verified. |

## Traceability summary

| Measure | Recorded coverage |
|---|---:|
| Total manual cases | 16 |
| Automated cases | 14 |
| Implementation coverage | 87.5% (14/16) |
| Automated passing cases | 13 |
| Automated known failures | 1 |
| Manual-first cases | 2 |

Implementation coverage measures cases with automation, including the failing case;
it is not the pass rate or proof of complete procedural coverage. The recorded automated
F04 pass proportion is 13/14 (approximately 92.9%), excluding the additional smoke test.
The normal regression recorded **BUILD SUCCESS: 14 tests, 0 failures, 0 errors, 0 skips**
(13 F04 cases plus smoke). Separate known-failure execution recorded **BUILD FAILURE:
1 test, 1 failure, 0 errors, 0 skips**.

The [Maven configuration](../pom.xml) excludes `known-failure` by default. The known-failure
method retains both its nonempty-author and exact-display-name assertions and is explicitly
runnable with the documented exclusion override. An excluded case is not a passing case.

## Coverage boundaries and source reconciliation

- UI restrictions demonstrate unavailable controls; they do not establish server-side
  rejection. Published content, count changes and feedback are public-UI evidence only.
- The attribution case's historical manual PASS differs from its later automated FAIL.
  Both are preserved. The records do not establish why the outcomes differ or confirm
  that the automated failure and provisional KAN-2 are the same product defect.
- Manual-first layout and metadata cases retain historical manual FAIL results; no
  current manual re-execution is documented in the final regression evidence.
- Independent access to the original spreadsheet was not available in the documented
  revision. This RTM traces the repository transcription, not an independently verified
  spreadsheet. Separate business requirement IDs and manual execution dates are
  **Not documented** in the case definitions.
- The [README](../README.md) agrees with the F04 counts. Its later hosted CI success
  concerns only selected smoke/guest tests and explicitly lacks independently inspected
  hosted per-method results. It does not supersede the full F04 result baseline above.
- The execution summary's statement that the README retains older baseline details
  predates the README's coverage/CI updates; its execution counts still agree with the
  specification. Root guidance also retains a historical four-test baseline and an old
  class name. These historical descriptions are not current automation mappings.
- All 14 automation mappings were checked against current Java declarations and
  assertions; no mapping inconsistency was found. No tests were run for this RTM.

## Evidence index

- [F04 specification, mapping and recorded execution](../agents/F04.md)
- [F04 Test Execution Summary](F04_Test_Execution_Summary.md)
- [Documented defect references](../agents/F04.md#5-existing-defects-and-cautions)
- [Voting/comment Java classes](../src/test/java/com/bgr/qa/voting/) — individual class links are in the matrix
- [Account preparation helper](../src/test/java/com/bgr/qa/helpers/UiAccountHelper.java)
- [Maven selection configuration](../pom.xml)
- [Project overview and CI context](../README.md)
