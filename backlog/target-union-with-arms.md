# Scoping: a composable mixed target — `TargetUnion` of typed arms

**Status:** Proposed — not started. **Owner:** TBD. **Related:**
[`sdk-language-design.md`](sdk-language-design.md) §1 (unify the predicate languages),
[`sdk-architecture-review.md`](sdk-architecture-review.md).

> **TL;DR.** "Target creature or player", "any target", "target opponent or planeswalker", and the
> not-yet-expressible "target player with 10 or less life" are all the *same shape*: a target that is a
> **disjunction of typed arms**, where each arm is `(kind, criteria)`. Today these live as a handful of
> bespoke, **criteria-less** sealed `TargetRequirement` types (`AnyTarget`, `TargetCreatureOrPlayer`,
> `TargetPlayerOrPlaneswalker`, `TargetOpponentOrPlaneswalker`), and there is **no way to attach a
> restriction to an arm** (no player-criteria layer exists at target-enumeration time at all). The
> proposal: one `TargetUnion(arms: List<TargetArm>)` requirement, `TargetArm = Obj(TargetFilter) |
> Player(restriction: Condition?)`. Object arms reuse the existing `GameObjectFilter`/`TargetFinder`
> pipeline unchanged; player arms reuse the existing `Condition` + `DynamicAmount` vocabulary via a new
> `Player.Candidate` binding. This subsumes the cross-kind sealed types, lets each arm carry criteria,
> and adds exactly one genuinely-new mechanism (per-candidate player restriction). Estimated ~1 PR for
> the player-criteria prerequisite + ~1 PR for `TargetUnion` itself.

---

## 1. Why this came up

`GameObjectFilter` is, correctly, an **object** filter — its predicates read object properties (type
line, P/T, tapped, color, counters) and its evaluator
(`rules-engine/.../handlers/PredicateEvaluator.kt` `matches(state, projected, entityId, filter, context)`)
takes an `entityId`. Players are *not* objects (CR 109 objects vs CR 102 players) and have none of those
properties. So filtering and single-kind targeting are already well-unified **for objects** —
`TargetFilter` and `GroupFilter` are thin wrappers reusing `GameObjectFilter`.

The gap surfaces with two intuitive card texts:

- **"Target player with less than 10 life"** — a *player* target with a *criterion on the player*.
- **"Target creature or player"** (classic burn) — a target spanning the object/player kind boundary,
  and its generalization "creature with power 4+ **or** player with ≤10 life".

Neither is cleanly expressible. This doc scopes the model for both.

## 2. The two boundaries a mixed target crosses

A mixed target has **two independent axes** that today's design conflates into bespoke types:

1. **Kind** — *object* or *player*. They enumerate and validate through different paths and produce
   different `ChosenTarget` variants.
2. **Criteria within a kind** — for objects this is a `TargetFilter` (`GameObjectFilter` + zone); for
   players it is naturally the `Condition` + `DynamicAmount` system (life total, hand size,
   controls-a-matching-permanent, life lost this turn).

The right model separates these: the **arm type** encodes kind; the **arm payload** encodes criteria.

## 3. Current state

### 3.1 Mixed targets are bespoke, criteria-less sealed types

`mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/targets/TargetRequirement.kt`:

| Type | Spans | Criteria slot? |
|---|---|---|
| `AnyTarget` | creature / planeswalker / player (CR also battle) | ❌ none |
| `TargetCreatureOrPlayer` | creature, player | ❌ none |
| `TargetPermanentOrPlayer` | permanent, player | `permanentFilter` ✅ |
| `TargetPlayerOrPlaneswalker` | player, planeswalker | ❌ none |
| `TargetOpponentOrPlaneswalker` | opponent, planeswalker | ❌ none |
| `TargetCreatureOrPlaneswalker` | creature, planeswalker — **both objects** | (object OR; no player) |
| `TargetSpellOrPermanent` | spell, permanent — **both objects, diff. zones** | `permanentFilter` only |
| `TargetPlayer` / `TargetOpponent` | player only | ❌ none |

Note the last two object-only rows: `TargetCreatureOrPlaneswalker` and `TargetSpellOrPermanent` do **not**
cross the player boundary and do **not** need this proposal — creature-or-planeswalker is already a plain
`TargetObject(filter = GameObjectFilter.CreatureOrPlaneswalker)`, and spell-or-permanent is an object union
across zones. `TargetUnion` earns its keep specifically **when a player arm is involved.**

**`TargetPermanentOrPlayer` (added for Powerful Broker) is the interim shape and the migration target.**
It is the only player-crossing member with a criteria slot on its object arm, which is half of what this
proposal asks for — the missing half is criteria on the *player* arm (§3.2). Until `TargetUnion` exists,
a new "… or player" wording should be spelled `TargetPermanentOrPlayer(permanentFilter = …)` rather than
a seventh bespoke type; a wording that genuinely cannot be expressed that way is the signal to build
`TargetUnion` rather than extend the family again. When `TargetUnion` lands, this type is the natural
first one to reduce to `TargetUnion(Obj(permanentFilter), Player(null))`, since it has no legacy
serialized shape to preserve beyond its own.

### 3.2 There is no player-criteria layer at enumeration time

- `TargetPlayer` / `TargetOpponent` carry only `count`/`optional`/`id` — no filter.
- `rules-engine/.../handlers/TargetFinder.kt` `findPlayerTargets()` / `findOpponentTargets()` enumerate
  **all** players (resp. opponents) and apply only shroud/hexproof. No per-player property check.
- `rules-engine/.../mechanics/targeting/TargetValidator.kt` `validatePlayerTarget()` checks only
  existence + shroud/hexproof.
- Player properties live only in `Condition` + `DynamicAmount`
  (`mtg-sdk/.../scripting/values/DynamicAmount.kt`: `LifeTotal(player)`, `Count(player, zone)`,
  `TurnTracking(player, …)`; `mtg-sdk/.../scripting/conditions/GenericConditions.kt`: `Compare(left, op,
  right)`), which are **resolution-time gates** — they cannot enumerate legal targets today.

### 3.3 History — don't resurrect the deleted `PlayerFilter`

A `PlayerFilter` sealed interface existed and was **deliberately deleted on 2026-02-05 (commit
`929f0692b`)** along with 21 legacy `EffectTarget` variants, in favour of composable `Player` references.
The fix below therefore **reuses `Condition`** rather than re-introducing a parallel player-predicate
hierarchy — a player's filterable properties already live in `Condition`/`DynamicAmount`; a `PlayerFilter`
bag would just re-skin `Compare`.

## 4. Proposed model

### 4.1 SDK types (`mtg-sdk/.../scripting/targets/`)

```kotlin
@Serializable
sealed interface TargetArm : TextReplaceable<TargetArm> {
    val description: String

    /** An object arm: any object matching [filter] (creature, planeswalker, spell, card-in-zone, …). */
    @SerialName("Obj")
    @Serializable
    data class Obj(val filter: TargetFilter) : TargetArm {
        override val description get() = filter.description
        override fun applyTextReplacement(r: TextReplacer) =
            filter.applyTextReplacement(r).let { if (it !== filter) copy(filter = it) else this }
    }

    /** A player arm: any player satisfying [restriction] (null = any player). */
    @SerialName("Player")
    @Serializable
    data class Player(val restriction: Condition? = null) : TargetArm {
        override val description get() = restriction?.let { "player $it" } ?: "player"   // refine wording
        override fun applyTextReplacement(r: TextReplacer) = this
    }
}

@SerialName("TargetUnion")
@Serializable
data class TargetUnion(
    val arms: List<TargetArm>,
    override val count: Int = 1,
    override val minCount: Int = count,
    override val optional: Boolean = false,
    override val id: String? = null,
) : TargetRequirement {
    override val description = arms.joinToString(" or ") { it.description }   // e.g. "creature or player"
    override fun applyTextReplacement(r: TextReplacer) =
        copy(arms = arms.map { it.applyTextReplacement(r) })
}
```

Authoring examples:

| Card text | Modeled as |
|---|---|
| target **creature or player** | `TargetUnion(listOf(Obj(TargetFilter.Creature), Player()))` |
| **any target** | `TargetUnion(listOf(Obj(TargetFilter.CreatureOrPlaneswalker), Player()))` |
| target **opponent or planeswalker** | `TargetUnion(listOf(Obj(TargetFilter.Planeswalker), Player(Conditions.candidateIsOpponent)))` |
| target **creature w/ power 4+ or player w/ ≤10 life** | `TargetUnion(listOf(Obj(TargetFilter.Creature.powerAtLeast(4)), Player(Conditions.candidateLifeAtMost(10))))` |

### 4.2 The player-criteria prerequisite (`Player.Candidate` + `restriction`)

The player arm needs to evaluate a `Condition` against **the player currently being considered**. Reuse
the `Condition` engine with a new self-binding reference:

- Add `Player.Candidate` to `mtg-sdk/.../scripting/references/Player.kt` — "the player under consideration
  as a target". (Description: deferred to the owning `TargetArm`.)
- The enumerator binds each candidate as `Player.Candidate` when evaluating `restriction`.
- Add a small `Conditions.candidate*` facade (e.g. `candidateLifeAtMost(n)` →
  `Compare(LifeTotal(Player.Candidate), LTE, Constant(n))`, `candidateIsOpponent`,
  `candidateControlsMatching(GameObjectFilter)`), so cards never hand-write the binding.

This same mechanism independently unlocks single-kind "target player with < 10 life": it is just
`TargetUnion(listOf(Player(Conditions.candidateLifeAtMost(10))))`, or — if we want the ergonomic shorthand —
an optional `restriction: Condition?` field added to `TargetPlayer`/`TargetOpponent`. **Build the
prerequisite first** (§7 PR 1); it is useful on its own.

## 5. Engine wiring

All paths already exist for objects and for unrestricted players; the work is dispatch + the player
restriction.

### 5.1 Enumeration — `rules-engine/.../handlers/TargetFinder.kt`

- Add a `TargetUnion` branch in the requirement dispatch: enumerate each arm and **union** the resulting
  `EntityId` lists (de-duplicated).
  - `Obj(filter)` → existing `findObjectTargets(state, ..., filter, ...)` (already routes by
    `filter.zone` to `findPermanentTargets` / `findGraveyardTargets` / `findSpellTargets`).
  - `Player(restriction)` → `findPlayerTargets` extended to filter each candidate by evaluating
    `restriction` with `Player.Candidate` bound to that candidate (via `ConditionEvaluator`).
- Extend `findPlayerTargets` / `findOpponentTargets` to accept and apply an optional `Condition`.
- Mirror in `rules-engine/.../handlers/TargetEnumerationUtils.kt` (legal-actions enumeration).

### 5.2 Validation — `rules-engine/.../mechanics/targeting/TargetValidator.kt`

- Add a `TargetUnion` case: a `ChosenTarget` is legal iff it satisfies **at least one** arm
  (object arm → existing object validation for that `TargetFilter`; player arm → `validatePlayerTarget` +
  re-evaluate `restriction`).
- The restriction is **re-checked at resolution** (CR 608.2b — a target illegal at resolution is removed):
  a player who rose above 10 life after being targeted is no longer a legal target. This falls out of
  re-running the same validation, so no extra bookkeeping.
- `ChosenTarget` (`rules-engine/.../state/components/stack/StackComponents.kt`) already has
  `Player | Permanent | Card | Spell` — **no new variant needed.**

### 5.3 No client/server contract change

Filters/targets are not sent raw to the client — only `.description` text crosses the boundary
(`ClientStateTransformer`). `TargetUnion.description` ("creature or player") renders the same way. Legal
targets are already surfaced as entity ids + choices. So this is **server-side only** plus a serialization
addition for the new `@Serializable` types.

## 6. What it subsumes (and what it deliberately doesn't)

- **Subsumes** (can be re-expressed as `TargetUnion`, optionally kept as thin factories): `AnyTarget`,
  `TargetCreatureOrPlayer`, `TargetPlayerOrPlaneswalker`, `TargetOpponentOrPlaneswalker`.
- **Does NOT need it** (object-only ORs — leave as-is or express via `GameObjectFilter.Or` /
  `TargetObject`): `TargetCreatureOrPlaneswalker`, `TargetSpellOrPermanent`.
- **Leave single-kind types alone**: `TargetObject`, `TargetPlayer`, `TargetOpponent` stay — they are the
  common, ergonomic case. Per the project's "no single-use patterns" / bounded-set instinct, do **not**
  rip out the criteria-less mixed types in a big-bang migration. Introduce `TargetUnion` to enable
  *criteria-bearing* and *arbitrary* mixed targets; migrate the legacy types only opportunistically (e.g.
  when a card needs a criterion an existing type can't carry), or make them `fun` factories over
  `TargetUnion` for description parity.

## 7. Suggested PR breakdown

1. **Player target restriction (prerequisite).** ✅ **Done.** Added `Player.Candidate` +
   `EffectContext.candidatePlayerId`; `restriction: Condition?` on `TargetPlayer`/`TargetOpponent`;
   one `PlayerTargetRestriction.isSatisfied(...)` helper routed through all three target sites
   (`TargetFinder`, `TargetEnumerationUtils`, and both `TargetValidator` + the resolution-time
   `StackResolver.validateTargets` for the CR 608.2b re-check); `Conditions.candidateLostLifeThisTurn()`
   / `candidateLifeAtMost(n)` facade. No real "N or less life" card exists (Scryfall confirms), so the
   prerequisite is proven by `PlayerTargetRestrictionScenarioTest` with inline cards mirroring Rix Maadi
   Guildmage's "target player who lost life this turn" ability; real consumers (Rix Maadi Guildmage, the
   Oath cycle) land when their sets are added.
2. **`TargetUnion` + `TargetArm`.** Add the SDK types (`@Serializable`, `SerialName`, `TextReplaceable`);
   add enumeration union + validation in `TargetFinder` / `TargetEnumerationUtils` / `TargetValidator`.
   Driving card: a "deal damage to target creature or player" card (and/or the criteria-bearing variant) +
   scenario test. Update `withId` in `TargetRequirement.kt` to handle `TargetUnion`.
3. **(Optional) Consolidation.** Re-express `TargetCreatureOrPlayer` / `TargetPlayerOrPlaneswalker` /
   `TargetOpponentOrPlaneswalker` / `AnyTarget` as `TargetUnion` factories; re-bless
   `CardDefinitionSnapshotTest` goldens. Only if it nets a real reduction without churning thousands of refs.

## 8. Testing

- **Scenario tests** (rules-engine/game-server): for each PR's driving card — legal-target enumeration
  includes/excludes the right players/objects; resolution re-check removes a target whose restriction
  stopped holding (e.g. life gained above threshold before resolution).
- **`CardDefinitionSnapshotTest`** (mtg-sets): any consolidation in PR 3 shows as reviewable per-card
  diffs; re-bless with `-DupdateSnapshots=true`.
- **Serialization round-trip**: `TargetUnion`/`TargetArm`/`Player.Candidate` must round-trip (they ride in
  `CardDefinition`).
- **DSL reference**: update `docs/card-sdk-language-reference.md` in the same change (load-bearing rule).

## 9. Open questions

- **Description wording for restricted player arms.** "player with 10 or less life" requires the
  `restriction` to render English. Either give the `Conditions.candidate*` factories a human description,
  or pass an explicit `descriptionOverride` on the arm. Decide in PR 1.
- **Does any real card need a *restricted object arm AND restricted player arm* simultaneously?** If the
  near-term backlog has none, PR 2 can ship with object arms unrestricted-by-default and still cover
  "creature or player" + "any target"; criteria-on-both is already supported by the type, just untested
  until a card needs it.
- **`Player.Candidate` vs. overloading `ContextPlayer`.** Confirm a dedicated `Candidate` ref reads better
  than reusing an indexed context player; `Candidate` is clearer and avoids index collisions during
  enumeration.
