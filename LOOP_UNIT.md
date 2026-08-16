# u18 — Leader, Super-Genius (MSH)

**Card.** `Leader, Super-Genius` {2}{U}{U} 1/3 — a connive-prefix replacement plus a
beginning-of-combat "target creature you control connives" trigger. Structurally the same card as
Twists and Turns (LCI) with connive in place of explore.

**Primitive.** Connive became a *named* keyword action: `ConniveEffect(subject, body)` wraps the
existing pipeline (unchanged) so it has a subject, and `ModifyExplore` was **generalized** to
`ModifyKeywordAction` covering explore (CR 701.44) + connive (CR 701.50) rather than adding a
parallel `ModifyConnive`. New `EventPattern.ConnivedEvent` + `PermanentConnivedEvent` make connive
observable ("whenever a creature you control connives"). Shared `KeywordActionReplacements` now
backs both executors.

**Planner triage was wrong twice.** Connive is **CR 701.50**, not 701.47 (that is Amass). And
`Effects.ConniveTargeting` is *not* connive — it is Teo, Spirited Glider's printed looting, which
never says the word — so it is deliberately left unwrapped, unreplaced, and unobserved.

**Gate.** `just test` — see the PR body for the recorded result. `just rebless-cards` +
`just check-card-printing "Leader, Super-Genius"` + backlog tick also run.

## For the reviewer — things worth a second opinion

- **Snapshot churn is deliberate and wider than one card.** Wrapping connive changes every card
  that connives: ~9 MSH + ~7 SPM entries gain a `ConniveEffect` node, and LCI's Twists and Turns
  changes `ModifyExplore` → `ModifyKeywordAction`. TLA is untouched (Teo uses `ConniveTargeting`).
  There is no way to make connive replaceable without touching the shared connive representation.
  Please confirm the moved entries are only that mechanical wrapper and nothing semantic.
- **Prefix controller.** The prefix effect runs in the *replaced action's* context, not one rooted
  at the replacement source. Inherited from `ModifyExplore`; it only diverges if an opponent's
  effect makes your creature connive. Documented, not fixed — say if it should be.
- **Unresolvable subject.** If the conniving permanent is gone, the body still draws/discards (old
  behavior preserved) but no replacement and no event fire. CR 701.50b's last-known-information
  matching is unmodeled.
- **APNAP ordering.** Multiple applicable prefixes chain in battlefield order, not CR 616 order —
  same simplification `ModifyExplore` already shipped with.
- **`ConnivedEvent` as a trigger is wired but has no printed card using it yet.** It exists because
  the replacement needs the pattern; leaving it unwired would have been a silent trap.
