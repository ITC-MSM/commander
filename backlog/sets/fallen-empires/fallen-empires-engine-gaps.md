# Fallen Empires — engine gaps

Divergences found while verifying FEM (2026-08-23, `verify-set`) that are **not** card-shaped and are
deliberately left open. Each survived an adversarial verification pass whose brief was to refute it.

Four other confirmed gameplay bugs found in the same pass **were** fixed rather than recorded here —
Deep Spawn's unimplemented mill payment, Farrel's Mantle's dead trigger, Orcish Spy's library
reordering, and Dwarven Soldier's per-Orc trigger. What follows is the remainder: things whose fix
would change shared engine behaviour well outside this set, or would need infrastructure that does
not exist.

---

## 1. A cost-derived delayed trigger is only registered at resolution — Vodalian War Machine

"When this creature dies, destroy all Merfolk tapped this turn to pay for its abilities."

The card has no printed `triggeredAbility`. Each of its two activated abilities ends its resolution
with a `CreateDelayedTriggerEffect(trigger = Triggers.Dies, watchedTarget = Self, …)` naming the
Merfolk that paid for *that* activation. The Merfolk is tapped when the ability is **activated**
(`ActivateAbilityHandler` records the payers on `ActivatedAbilityOnStackComponent`), but the
dies-watcher only enters `state.delayedTriggers` when the ability **resolves**.

So there is a response window. Kill the War Machine — or counter the ability — while that activation
is still on the stack, and the Dies event passes with no watcher resident; the later-resolving
activation then registers a watcher for a death that already happened and it expires unfired. The
printed trigger belongs to the permanent and would still see that Merfolk as "tapped this turn to
pay for its abilities".

Not fixed here because the fix is infrastructure, not a card edit: the payers have to be recorded on
the permanent at cost-payment time (a per-turn noted-entity list written from
`ActivatedAbilityOnStackComponent.tappedPermanents`) and read by a real `Triggers.Dies` ability —
or, more generally, the engine has to schedule cost-derived delayed triggers when the cost is paid
rather than when the effect runs.

A second claim about this card — that N activations produce N separate triggers rather than one —
was **refuted**: each one-shot trigger destroys its own baked payer id, so the same set dies. Only
the stack-object count and the priority windows between them differ.

Scope: one card. Reachable only with instant-speed removal or a Stifle effect held for the window.

## 2. `MoveCollectionEffect` to the top of a library reverses the batch — CORPUS-WIDE

`MoveCollectionExecutor` places cards one at a time
(`moveCardsToZoneInternal`, the per-card `for (cardId in cards)` loop), and
`ZoneTransitionService`'s `LibraryPlacement.Top` prepends each individually. So moving `[c1,c2,c3]`
to the top yields `[c3,c2,c1]`. `CardOrder.Preserve` does not prevent it — it only means "don't
shuffle and don't prompt".

The prompting sibling does it correctly: the `ControllerChooses` path in
`LibraryAndZoneContinuationResumer` writes the whole batch in one go (`orderedCards + currentLibrary`)
and does preserve order. The two paths disagree, and the non-prompting one is the wrong one.

This is what made Orcish Spy reverse the top three cards of the library it only looked at. That card
was fixed at the card level — the gather never removed the cards, so the move-back was pure churn and
is simply gone — which is the right fix for *that* card regardless. The engine asymmetry is left
alone deliberately: reconciling it changes the behaviour of every multi-card move-to-top in the
corpus, well outside this set's blast radius, and belongs in its own change with its own review.

Only two other cards currently pass `CardOrder.Preserve` (Esper Origins, Hermit Druid) and neither
exposes the bug — one moves a single card, the other moves to a graveyard.

## 3. Identical continuous effects from separate resolutions collapse — CORPUS-WIDE, unverified

Noted while fixing Dwarven Soldier, and recorded because it hides bugs rather than because it is
known to be one.

With the trigger firing twice (two Orc blockers, before the fix), the projected P/T was **already
correct** at 2/3: two identical `+0/+2` continuous effects with the same source, ability and
duration collapse into one. The defect was visible only as two copies of the ability on the stack.

Whether that collapse is ever *wrong* — two legitimately separate resolutions of the same pump
ability that should stack — was not established here, and no card in this set depends on it. It is
recorded so the next person to write a stat-modifying test knows that **asserting the projection is
not sufficient**: assert the stack, or the assertion passes for the wrong reason. `AGENTS.md` already
warns against `toMutableSet()` on a `ContinuousEffect` list for the neighbouring reason.

## 4. `Player.AnOpponent` picks the first opponent instead of asking — Rainbow Vale, multiplayer only

`EffectTarget.PlayerRef(Player.AnOpponent)` resolves to the first opponent in turn order
(`TargetResolutionUtils`) rather than letting the controller choose. Rainbow Vale's "target opponent
gains control of this land" is unaffected in a two-player game, which is the only shape FEM is played
in here. Pre-existing and already tracked as a multiplayer backlog item; listed only so the card is
not re-reported.

## 5. Farrel's Mantle's ANY-binding sibling is still unwired

The ATTACHED binding of `AttacksAndIsntBlocked` was wired for this set. An **ANY**-binding filtered
variant ("whenever a creature you control attacks and isn't blocked") still is not, and
`TriggerMatcher` declines it. No FEM card needs it; noted because the language reference's claim that
the trigger is "SELF only" was updated and this is the part of that claim that survives.
