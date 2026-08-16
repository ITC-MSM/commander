# u30 — Captain Mar-Vell, Space-Born (conditional flash-cast permission)

Branch `loop-msh-u30`, rebased onto `loop-msh-u28` (local, **not** merged upstream). u28 itself sits
on `origin/main`, so `main` is an ancestor, but u28's commits are not upstream yet — this waits for
u28 to land before it can be opened on its own. Reviewer: diff with `git diff loop-msh-u28...HEAD`.

The rebase moved the card and its scenario test into the per-era modules that `origin/main` now
uses: `mtg-sets/2026/src/main/.../msh/cards/CaptainMarVellSpaceBorn.kt` and
`mtg-sets/2026/tests/src/test/.../CaptainMarVellSpaceBornScenarioTest.kt`. The mechanic-level
`ConditionalFlashGrantTest.kt` uses only locally-defined cards and stays in `rules-engine`.

- **Primitive:** flash permission now honours a `ConditionalStaticAbility`-wrapped
  `GrantFlashToSpellType`. Lives in
  `rules-engine/src/main/kotlin/com/wingedsheep/engine/mechanics/FlashTypeGrants.kt`, which grew from
  a single `nthGateAllows` helper into the whole `hasGrantedFlash` decision.
- **Design route:** unwrap at the read site, **not** a `condition` field on `GrantFlashToSpellType`.
  Flash has exactly two read sites (`CastPermissionUtils.hasGrantedFlash`,
  `CastZoneResolver.hasGrantedFlash`); they were *semantically* identical copies of each other
  (same control flow, guards, ordering and early returns — differing only in comments and one local's
  name, `def` vs `cardDef`), and both now delegate to one `FlashTypeGrants.hasGrantedFlash` — so
  teaching that one function to unwrap fixes every conditional flash grant at once and cannot drift.
  The deciding factor is that shared owner, not a read-site count (`MayCastSelfFromZones` has two
  sites as well and still folded its gate into the type).
- **Consolidation:** the two read sites were duplicated in full (own-`conditionalFlash` check,
  turn-scoped `FlashGrantsThisTurnComponent` grants, battlefield scan). They are now thin delegates.
  That is a real dedup, not a drive-by refactor — it is the structural guarantee that both learned
  the unwrap.
- **Card:** `Captain Mar-Vell, Space-Born` (MSH #12, {4}{W} 4/4 Legendary Kree Soldier Hero, flying +
  vigilance). "Cosmic Awareness — As long as an opponent has cast a spell this turn, you may cast
  spells as though they had flash." Modelled as `staticAbility { condition = CompareAmounts(
  SpellsCastThisTurn(EachOpponent) >= 1); ability = GrantFlashToSpellType(Any, controllerOnly = true) }`.
  No new condition or dynamic-amount type was needed; `Player.EachOpponent` sums across opponents, so
  `>= 1` is multiplayer-correct for "an opponent".
- **Tests:** `ConditionalFlashGrantTest.kt` (mechanic-level, synthetic cards — closed gate, gate
  opening mid-turn, open gate offered *and* castable, no leak past the grant's filter, granter
  leaving revokes) and `CaptainMarVellSpaceBornScenarioTest.kt` (the card — closed gate, opponent's
  spell opens it, no-Mar-Vell control, `controllerOnly`). Every assertion goes through
  `getLegalActions`/`legalActions` or the cast handler, never the static-ability list.
- **Mutation check:** with `activeGrant`'s `ConditionalStaticAbility` branch stubbed to `null` (the
  pre-fix behaviour) 4 of 5 mechanic tests and the card's positive test went red; the closed-gate and
  control tests correctly stayed green. Mutation reverted and both suites re-run green.
- **Gate (re-run after the rebase onto `loop-msh-u28`; the earlier 11,273-test green predated it and
  no longer applied — the base changed and the card/test moved modules):** an engine change, so
  `:rules-engine:test :mtg-sets:scenarioTest`, run as `scripts/gradle-locked` (`just` doesn't parse on
  this box) with `--no-build-cache`.
  - `:rules-engine:test` — **2739 tests, 0 failures, 0 ignored**, task executed rather than cached.
  - All 9 era scenario suites ran; `:mtg-sets:2026:tests:test` (this card's module, plus the rest of
    MSH on the u28 stack) green in full, `CaptainMarVellSpaceBornScenarioTest` 6/6 and
    `ConditionalFlashGrantTest` 9/9.
  - The combined run first came back red **from the environment, not the diff**: a
    `CorruptedCacheException` in the shared `/workspace/.gradle/9.6.1/fileHashes/fileHashes.bin`, the
    `:rules-engine:test` executor killed (`exit value 13`, no assertion failure), a dead daemon, and
    three 120 s `TimeoutCancellationException`s (`AgelessSentinelsTest`, `AbattoirGhoulScenarioTest`,
    `AbhorrentOculusScenarioTest` — the known contention flake; load average peaked at 10.4 with a
    sibling container's Kotlin daemon in the log). All three pass standalone in ~20 s, and
    `:rules-engine:test` is clean on its own re-run. Shared cache state was not touched.
  - **Not covered:** `:game-server`, `:ai`, `:gym*`, `:oracle-assay`, and the web client. The diff
    reaches only `rules-engine` and `mtg-sets`, so those are out of scope by the module rule.
- **Also found silently inert, NOT fixed (out of scope, reported):** the same missing unwrap makes a
  `ConditionalStaticAbility`-wrapped grant inert in `FlashbackGrants`, `WarpGrants`, `MiracleGrants`,
  `MayhemGrants`, `WebSlinging`, `GrantedKeywordResolver` (`GrantKeywordToOwnSpells`),
  `CostCalculator` (`MayCastWithoutPayingManaCost`) and the `MayCastFromGraveyard` read sites in
  `CastFromZoneEnumerator` / `CastZoneResolver`. None has a gated card today, so nothing is currently
  broken by it — but the next author to gate one hits the same trap.
- **Review corrections (see `build/pr/loop-msh-u30-correction.md`):** the battlefield scan now uses
  `state.controlledBattlefield(playerId)` (a stolen granter follows its controller, CR 109.5; a
  phased-out granter grants nothing, CR 702.26b) and `effectiveStaticAbilities(classLevel)` (a Class
  card's level-gated grant is no longer inert), each with its own test. Added tests for
  `EachOpponent` vs `Any`, the turn boundary, and the granter-controller condition context; the eight
  still-inert sibling families are now warned about in `docs/card-sdk-language-reference.md`.
- **Unsure / deliberately not changed:**
  - No manual playthrough in the web client and no e2e; a playtest scenario JSON is shipped instead.
