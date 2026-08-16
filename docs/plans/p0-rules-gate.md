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
branch from its `{T}, Sacrifice: Add {C}{C}` branch. Its accepted intent additionally stores a
chosen colour for the bounded targetless `{T}: Add N mana of any one color` shape, so Gilded Lotus
does not re-choose a colour when the transaction resumes. It supports deterministic fixed-output
`{T}` and `{T}, Sacrifice this` branches (including Elvish Aberration and Crystal Vein) plus that
fixed-`N`, unrestricted any-one-colour branch, preserves applicable mana-production multipliers,
and retains surplus in that controller's individual mana pool. It also supports the deliberately
narrow printed `{1}, {T}: Add {U}{R}` branch represented by Izzet Signet: the source controller
must first supply its own pre-existing `{1}`, then explicitly records whether `{U}` or `{R}` pays
that controller's `{1}` block-tax share. The other colour remains in that controller's pool; no
teammate can fund either the activation or the tax. A selected blocker that sacrifices itself pays the locked tax and leaves its
attacker blocked, but does not receive blocking status. The same atomic vocabulary now also covers
the exact Springleaf Drum form `{T}, tap one untapped creature you control: add one mana of any
colour`: the selected creature is carried in the intent, must be an offered untapped creature
controlled by the payer, and may still become a blocker after being tapped. It also admits the
direct pain-land rider `{T}: add one fixed coloured mana; this land deals 1 damage to you`, using
the exact printed ability branch only on the candidate state after all payer intents are accepted.
Any decline, forged branch, duplicate
source, direct mana activation, or failed payment leaves mana, blockers, and declaration markers
unchanged. The slice still excludes other activation-mana shapes, other secondary-tap shapes,
restricted-mana, dynamic/restricted-output, granted abilities, and other side-effecting branches;
those remain explicit implementation gaps, separate from the FFA Commander simulation evidence
above.

## Next implementation order

1. Expand the shared-team / Two-Headed Giant block-tax flow beyond the verified fixed-output,
   choice-output, self-sacrifice, and exact Izzet-Signet `{1},{T}` sources while preserving atomic
   payment and rollback.
2. Keep the P0 composition traces (replacement chains, partial target fizzle,
   multiplayer stack/concession, and commander SBA-loss) in every repeatable release gate.
3. Run the P0 scenario matrix and the broad rules-engine gate before treating readiness
   dashboard evidence as eligible for larger card batches.
