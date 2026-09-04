# Champion implementation research

Champion is required by Boggart Mob, Changeling Berserker, Changeling Hero, Changeling Titan, Mistbind Clique, Nova Chaser, Thoughtweft Trio, Wanderwine Prophets, and Wren’s Run Packmaster. This is an implementation worklist, not a completed capability.

## Verified rules boundary

The current official Comprehensive Rules text downloaded from [the rules page](https://magic.wizards.com/en/rules) to `/tmp/lrw-MagicCompRules-20260819.txt` defines champion in 702.72a–c and links its two abilities in 607.2k. Champion has separate enters and leaves triggers; the first offers another matching permanent you control in place of sacrificing the source, and the second returns the linked card under its owner’s control. Being championed specifically means being exiled by that mechanic.

## Original implementation seams

- `ExileUntilLeavesExecutor` requires the source still to be on the battlefield. It cannot directly implement champion’s independent enters trigger when that source has already left.
- `Effects.ExileLinkedToSource` lowers to the existing zone-move effect with `linkToSource = true`; the Gather → Select → Move pipeline can express choosing an eligible permanent without targeting it.
- `LinkedExileComponent` stores only a list of entity IDs on the source entity. It intentionally survives source departure. `GatherCardsExecutor` reads the current source component for `FromLinkedExile`, and `MoveCollectionExecutor.linkCardsToSource` writes by current source ID. Verify source departure/re-entry before reusing this storage: links from separate battlefield instances must not mix.
- `TriggeredAbilityOnStackComponent` and `EffectContext` carry a source ID and several last-known values, but the inspected fields do not establish a general source battlefield-instance identity. Check the remaining source/capture/zone-transition paths before extending that contract.
- `ZoneChangeEvent` currently marks craft-material exiles but does not identify champion exiles or their champion source. Mistbind Clique requires a distinct champion occurrence. Its follow-up is an ordinary live triggered ability, not an unconditional callback embedded in the already-stacked enters ability.

## Required behavioral cases

- Accept a legal choice, decline, and have no eligible permanent; sacrifice only when the champion action was not performed.
- Exclude the source itself, use projected characteristics/control, and allow noncreature Kindred permanents when the quality is a creature subtype rather than “creature.”
- Handle tokens being championed, leaving exile, stolen eligible permanents, and return under the owner’s control.
- Resolve enters and leaves triggers in both orders when the source leaves before its enters trigger resolves. Resolve an old trigger after its source leaves and returns without touching the new instance’s links or sacrificing the new instance as the old source.
- Preserve links correctly for multiple champion instances, copied triggers, simultaneous departures, and removal of the exiled object before return.
- Mistbind: choose the player target when the champion event creates the trigger; no follow-up when no Faerie was championed or Mistbind lacks the ability at that event; test projected Faerie characteristics at exile.
- Reuse battlefield selection and existing return/zone events; trace any new champion event through trigger matching, indexing, continuations, source snapshots, serialization, and client event handling.

Prefer composition for selection, movement, and sacrifice. Add only the event/identity/linking vocabulary the existing primitives cannot faithfully express. Update the SDK reference and add engine and per-card tests in the same implementation.


## Reproduced prerequisite failure

The new local engine probe `LinkedExileSourceInstanceTest` exercises only the shared linked-exile slice, using inline test cards. Ordinary source departure passes. Exiling and immediately returning the source before its leaves trigger resolves fails: the previously linked creature remains exiled after all triggers resolve, even when the new source instance declines its own exile choice. The assertion is on the victim returning, not on a prompt or setup error. This confirms the source-instance issue above; it is not a failure in the published five-card batch.

The fix must query links belonging to the originating source object dynamically. Freezing just the exiled-card IDs when the leaves trigger fires is insufficient: enters and leaves triggers can resolve in the opposite order, and an exiled object can leave and return to exile before the return resolves. Source tokens may also cease to exist, so storing historical links only on the current source entity cannot cover every case. Preserve exact source and exiled-object identity, and test these cases before lowering champion into the existing pipeline.


## Source-visit prerequisite implementation

The working fix carries `BattlefieldEntryTimestampComponent.timestamp` through exit snapshots,
entry events, pending triggers, stack components, target-selection continuations, and effect contexts.
Departed visits retain their exile piles in immutable game state, so returning sources and vanished
tokens do not overwrite those piles. The common `FromLinkedExile` gather and `linkToSource` move paths
use the captured visit. Every removal from an exile zone invalidates previous links, including
cast-from-exile and token cleanup paths that bypass the main zone-transition service.

All seven source-visit scenarios pass: ordinary departure, blinking, separate old/new piles, an entry
trigger resolving after the source's return trigger, a vanished token source, and leaving/reentering
exile with both live and departed sources. The token case includes a full game-state serialization
round trip while its return trigger is on the stack. `just test-rules` passed (2m 19s, 61 tasks), covering engine tests and every card scenario.

This does not implement champion. Remaining work includes linking distinct ability pairs on the same
source, the actual champion event and Mistbind follow-up, source-instance-aware self/exclusion
semantics, and the rest of the behavioral matrix above. Ad hoc battlefield-entry event producers
also need review before claiming every batched entry path captures the originating visit.
