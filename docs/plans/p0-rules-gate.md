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

## Explicit scope boundary

The Two-Headed Giant/shared-turn combined defending-team declaration is atomic: one defender
submits a map containing blockers controlled by either teammate, the entire map is validated
together, and each controller with a tax share receives their own block-tax prompt. Payment responses are
collected as zero-mutation intents; only after every payer accepts are all simple tap-only mana
payments applied to a candidate state and the block committed once. Any decline, forged source,
duplicate source, or failed payment leaves mana, blockers, and declaration markers unchanged.
This first payment slice deliberately excludes sources with sacrifice, secondary tap, pain,
activation-mana, restricted-mana, or multi-mana side effects. Those source shapes remain an
explicit implementation gap, separate from the FFA Commander simulation evidence above.

## Next implementation order

1. Expand the shared-team / Two-Headed Giant block-tax flow beyond the verified simple,
   tap-only mana-source slice while preserving atomic payment and rollback.
2. Add the remaining P0 composition traces (replacement chains, partial target fizzle,
   multiplayer stack/concession, and commander SBA-loss) to the repeatable release gate.
3. Run the P0 scenario matrix and the broad rules-engine gate before treating readiness
   dashboard evidence as eligible for larger card batches.
