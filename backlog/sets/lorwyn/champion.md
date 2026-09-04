# Champion implementation research

Champion is required by Boggart Mob, Changeling Berserker, Changeling Hero, Changeling Titan, Mistbind Clique, Nova Chaser, Thoughtweft Trio, Wanderwine Prophets, and Wren’s Run Packmaster. This is an implementation worklist, not a completed capability.

## Verified rules boundary

The current official Comprehensive Rules text downloaded from [the rules page](https://magic.wizards.com/en/rules) to `/tmp/lrw-MagicCompRules-20260819.txt` defines champion in 702.72a–c and links its two abilities in 607.2k. Champion has separate enters and leaves triggers; the first offers another matching permanent you control in place of sacrificing the source, and the second returns the linked card under its owner’s control. Being championed specifically means being exiled by that mechanic.

## Existing implementation seams

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
