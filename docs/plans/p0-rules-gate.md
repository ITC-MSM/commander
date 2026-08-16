# P0 rules gate

This gate is a release prerequisite for the local CommanderOnline staging environment.  A
green card-definition count or a successful UI build does not satisfy it.

## Completed only when independently reviewed

- **Priority after resolution (CR 117.3b):** the active player receives priority after a
  spell or ability resolves, including when resolution or triggered-ability placement paused
  for a decision.  Casting, activating an ability, and special actions retain the acting
  player instead (CR 117.3c).
- **State-based actions and triggers (CR 117.5, 603.3b, 704):** stabilize SBAs first,
  then stack all simultaneous triggers in APNAP order.  Controllers choose their internal
  simultaneous-trigger order, and triggers caused by abilities being put on the stack form
  the required subsequent wave.
- **Commander zone rules (CR 903.9):** graveyard/exile is an SBA choice after the move;
  hand/library is an optional owner-controlled replacement before the move, participating in
  CR 614–616 replacement ordering.  No `alwaysDivertToCommand` shortcut is allowed in a
  rules-faithful game.
- **Commander multiplayer:** tax, commander-damage loss, owner-only zone choices, player
  elimination, combat damage attribution, and replay/serialization have scenario coverage.

## Evidence ledger

| Gate | Evidence | Status |
| --- | --- | --- |
| Direct priority after a nonactive spell resolves | `HearthbornBattlerTest` | Implemented; staging-tested |
| Priority after paused ETB target placement | `EtbSuspectUpToOneTargetCreatureTest` | Implemented; staging-tested |
| Caster priority after a cast-time decision | `BattleMenuTest` | Implemented; staging-tested |
| Independent priority review | Direct, paused-resolution, and cast-time paths reviewed against CR 117.3b/c | Passed |
| APNAP controller ordering and generic trigger waves | `TriggeredAbilityOrderingTest` (ordering, non-combat wave, paused wave), `FirebenderAscensionScenarioTest` | Implemented; Ubuntu-tested |
| Commander 903.9a graveyard/exile choice | `CommanderZoneChoiceCheckTest`, `CommanderZoneRedirectTest` | Implemented; staging-tested |
| Commander 903.9b hand/library replacement | `CommanderZoneReplacementTest`, including `CMD-REPL-CHAIN-BF-HAND-CMD-LIB-001` for CR 616 choice → command redirect → re-evaluation | Implemented; Ubuntu-tested |
| FFA multiplayer declare-blockers boundary | `CommanderPodSimulationTest`: APNAP defender cursor, paid/declined block tax, deferred triggers, player leave, post-placement SBA loop | Implemented; independently reviewed and Ubuntu-tested |
| Commander replacement re-evaluation chain | `CommanderZoneReplacementTest :: CMD-REPL-CHAIN-BF-HAND-CMD-LIB-001` | Implemented; focused and broad rules-engine-tested |
| Modal partial-target fizzle | `CastIntoTheFirePartialFizzleScenarioTest` | Implemented; focused and broad rules-engine-tested |
| Four-player stack, counter, and concession | `CommanderPodSimulationTest :: P0-4P-STACK-CONCESSION-001` | Implemented; broad rules-engine-tested |
| Commander-loss / lethal-damage SBA / survivor trigger chain | `CommanderPodSimulationTest :: SBA-COMMANDER-LOSS-DIES-4P-001` | Implemented; broad rules-engine-tested |

## Explicit scope boundary

The Two-Headed Giant/shared-turn combined defending-team declaration is atomic: one defender
submits a map containing blockers controlled by either teammate, the entire map is validated
together, and each controller with a tax share receives their own block-tax prompt. Payment responses are
collected as zero-mutation intents; only after every payer accepts are all selected fixed-output
branches applied to a candidate state and the block committed once. The atomic decision names a
source plus its printed mana-ability index, so it distinguishes Crystal Vein's `{T}: Add {C}`
branch from its `{T}, Sacrifice: Add {C}{C}` branch. It supports deterministic, targetless,
fixed-output `{T}` and `{T}, Sacrifice this` branches (including Elvish Aberration and Crystal
Vein), preserves applicable mana-production multipliers, and retains surplus in that controller's
individual mana pool. A selected blocker that sacrifices itself pays the locked tax and leaves its
attacker blocked, but does not receive blocking status. Any decline, forged branch, duplicate
source, direct mana activation, or failed payment leaves mana, blockers, and declaration markers
unchanged. The slice still excludes secondary tap, pain, activation-mana, restricted-mana,
choice/dynamic-output, and other side-effecting branches; those remain explicit implementation
gaps, separate from the FFA Commander simulation evidence above.

## Next implementation order

1. Expand the shared-team / Two-Headed Giant block-tax flow beyond verified fixed-output,
   tap-only mana sources while preserving atomic payment and rollback.
2. Keep the P0 composition traces (replacement chains, partial target fizzle,
   multiplayer stack/concession, and commander SBA-loss) in every repeatable release gate.
3. Run the P0 scenario matrix and the broad rules-engine gate before treating readiness
   dashboard evidence as eligible for larger card batches.
