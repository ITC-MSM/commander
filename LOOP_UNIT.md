# loop-msh-u25 — Captain America, Living Legend + a per-permanent "first time tapped this turn" window

Base branch: **`loop-msh-u32`** (not `main`). Stacked. Since u32 sits on u23, which was itself
rebased onto `origin/main`, `main` *is* now an ancestor, but the u23 and u32 commits below this
branch are not upstream yet — this still waits for those to land before it can be opened on its own.

## The primitive

- **`EventPattern.TapEvent` grew `firstTimeEachTurn: Boolean = false`**
  (`mtg-sdk/.../scripting/EventPattern.kt`) — the same axis name the four existing first-time events
  already use (`LifeGainEvent`, `BecameSaddledEvent`, `CountersPlacedEvent`, `BecomesTargetEvent`).
  Surfaced on `Triggers.becomesTapped(...)` and `Triggers.OneOrMoreBecomeTapped(...)`.
- **`TappedEvent` grew `firstThisTurn: Boolean = true`** (`rules-engine/.../core/GameEvent.kt`),
  mirroring `BecameSaddledEvent.firstThisTurn`.
- **`HasBecomeTappedComponent(lastBecameTappedTurn: Int)`**
  (`rules-engine/.../state/components/battlefield/BattlefieldComponents.kt`) — a turn *stamp*, not a
  cleanup-cleared marker, following u32's `HasDealtDamageComponent` rather than u23's
  `ReceivedCountersThisTurnComponent`: the window closes on its own when `turnNumber` moves, so there
  is no `CleanupPhaseManager` entry to forget. Stripped on zone change (`ZoneMovementUtils`, CR 400.7).
- **Computed in the `tap()` atom** (`rules-engine/.../core/TapHelpers.kt`), read before the stamp is
  written. `isFirstTapThisTurn(state, entityId)` is the shared read, also used by the two
  mana-payment sites that hand-build a `TappedEvent` for a `{T}, Sacrifice this` source.
- **Match sites wired:** `TriggerMatcher` (per-event) and `TriggerDetector.detectTapBatchTriggers`
  (batch — narrows the batch to its first-time taps, like `reason`/`tapper` already do).
- **`ai/.../StateProgress.kt`**: `HasBecomeTappedComponent` added to `IGNORED_COMPONENTS`. A bare
  tap now writes a component, which made Aphetto Alchemist's self-targeted `{T}: Untap target` read as
  a new position and reopened the infinite-activation bug `LoopingActionAiTest` guards (3 of its 4
  tests failed on the gate). It sits next to `TargetedByControllerThisTurnComponent`, Valiant's marker
  — same shape, same reason. u23's/u32's markers do not need the entry: both are only ever written
  alongside a real board change.

Path enumeration is *mostly* closed by construction rather than by a list: `TapEventEnforcementTest`
bans open-coded `with(TappedComponent)` outside its enters-tapped/cleanup allowlist, so tap
transitions go through `tap()`. `git grep "TappedEvent("` finds exactly three production construction
sites; all three are handled. One caveat remains, written down in the code rather than glossed:

- **The guard is a text scan with two holes**, both currently benign: its regex misses
  `components.add(TappedComponent)` (the token executors), and it scans only
  `rules-engine/src/main/kotlin`, so `game-server`'s scenario builder is out of range. Every current
  hit of both kinds is a legitimate enters-tapped site.

**Regeneration's tap is now routed through `tap()`** (`ZoneMovementUtils.applyRegenerationReplacement`).
CR 701.19a makes it a real tap ("…remove all damage marked on it and *its controller taps it*"), so it
emits a `TappedEvent` and stamps the window like any other tap. That fixes both directions at once: the
trigger now fires on a regeneration tap (Captain America; and Deeproot Pilgrimage, which had silently
missed it since before this unit), and the stamp closes the stale window that handed out a second
"first tap" later in the same turn. `MoveCollectionExecutor` was dropping the returned events on the
floor and now folds them in. Covered by `RegenerationTapEventTest` plus one end-to-end case in
`CaptainAmericaLivingLegendScenarioTest`.

## The card

`Captain America, Living Legend` (MSH #210, {1}{W}{U} 3/4 Legendary Human Soldier Hero, vigilance).
`Triggers.becomesTapped(binding = ANY, filter = Creature.youControl(), firstTimeEachTurn = true)` +
`triggerCondition = Conditions.IsYourTurn` + `Effects.Untap(EffectTarget.TriggeringEntity)`. No
card-specific engine code. MSH is the earliest real printing, so canonical placement is here.

## Why not `oncePerTurn`

`oncePerTurn` caps the **ability** at one firing per turn; this card's clause names the **creature**.
With three creatures tapped in a turn, `oncePerTurn` untaps one and `firstTimeEachTurn` untaps three.
The two are orthogonal and composable; `FirstTimeTappedThisTurnScenarioTest` runs them head-to-head on
the same board.

## Tests

- `rules-engine/.../scenarios/FirstTimeTappedThisTurnScenarioTest.kt` — the primitive: two creatures
  each firing, the `oncePerTurn` contrast, tap→untap→re-tap in one turn, the turn boundary, a creature
  that *entered* tapped, the zone change (the stamp is stripped **and** the replayed object's window
  reopens — it is tapped again in the same turn and fires again), five tapping paths (attack declaration, `Effects.Tap`,
  crew, teamwork cost, a spell's mana payment), both batch directions, description rendering and
  serialization round-trip. Trigger firing is asserted as a **library** delta (the payoff draws), and
  every negative case first asserts the tap really happened with `firstThisTurn == false` — so no test
  can pass by nothing having been tapped.
- `rules-engine/.../scenarios/CaptainAmericaLivingLegendScenarioTest.kt` — the card: both directions of
  all three riders, plus vigilance, self-untap, and an attacker untapping without leaving combat. The
  vigilance case asserts on the **event** (`declareAttackers` emits no `TappedEvent` for him), because
  asserting "he ends up untapped" passes with vigilance removed — his own trigger would untap him.
- `ai/.../StateProgressTest.kt` — one case pinning the `IGNORED_COMPONENTS` entry in the file that
  documents the exclusions, alongside `LoopingActionAiTest`'s behavioural coverage.

## Gate

`just test` — **passed**, zero failures. `just rebless-cards` moved only `MSH.json` (+45/−0, my card
only). `just check-card-printing` clean. `just fix-backlog` → 256 / 276. Three runs: a Kotlin-daemon
OOM (environmental, fixed by `./gradlew --stop` on idle daemons), then the snapshot golden + the
`LoopingActionAiTest` regression above, then one bug in my own turn-boundary test (a
`passUntilPhase(PRECOMBAT_MAIN)` issued while already in precombat main returns instantly, so it never
crossed a turn). `ConniveTargetingTest` timed out on run 2 and passed on run 3 — untouched by this
change, documented as flaky under load.

## Things I'm unsure about / did not verify

- **No mtgish bridge/emitter entry — a verified negative, not an unchecked one.** The corpus that
  *is* in the worktree is `mtgish-tooling/src/test/resources/fixtures/*.json` (set-scoped IR samples).
  Grepped: it carries first-time tags for other events —
  `WhenACreatureAttacksForTheFirstTimeEachTurn`,
  `WhenAPlayerAttacksWithAnyNumberOfGroupCreaturesForTheFirstTimeEachTurn`,
  `WhenAPlayerGainsLifeForTheFirstTimeEachTurn` — and in the tapped family only
  `WhenAPermanentBecomesTapped` and `WhenAPermanentIsTappedForMana`, with **no first-time variant**.
  So there is nothing to map: the existing `WhenAPermanentBecomesTapped` capability covers the shape
  the emitter renders (SELF `Triggers.BecomesTapped`), which is unchanged, and inventing a tag name
  would be a fabricated capability claim. (The earlier note here said the corpus was absent; it named
  a path that doesn't exist rather than checking the fixtures.)
- `TappedEvent.firstThisTurn` defaults to **`true`** (matching `BecameSaddledEvent`). Every production
  site passes it explicitly, so the default only affects hand-constructed events and legacy decodes;
  a `false` default would silently under-fire instead. Arguable either way.
- `HasBecomeTappedComponent` has no default on its parameter, on purpose — so a live game state
  persisted before this change fails to decode on restore, exactly as u32's `HasDealtDamageComponent`
  does. TTL'd live state, no migration layer in the repo.
- No manual playthrough in the web client, no UX pass from both seats, no e2e. The playtest scenario
  `manual-scenarios/sets/msh/loop-msh-u25-captain-america-living-legend.json` is shipped but unrun.
- The `firstTimeEachTurn` axis is exposed on `OneOrMoreBecomeTapped` for symmetry with `reason`; no
  printed card uses that combination yet, so its only coverage is the two batch tests here.
