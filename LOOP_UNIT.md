# u33 — Nick Fury, Agent of S.H.I.E.L.D. (+ the double-faced-card predicate)

**Card.** Nick Fury, Agent of S.H.I.E.L.D. [MSH 25] — power-up dig (Gather → Select → Move, Gishath's
shape) whose *"If it's a double-faced card, you may transform it"* clause is now a real gate:
`ConditionalEffect(CollectionContainsMatch("fury_entered", Filters.DoubleFaced), MayEffect(ForEach(…,
Transform(Self))))`, applied **after** the move so the permanent enters on its front face.

**Primitive.** Two pieces. (1) `CardPredicate.IsDoubleFaced` — a printed layout characteristic
(CR 712.1) modelled on the existing `HasAdventure`: precomputed onto `CardComponent.isDoubleFaced`
and *carried across a face swap* in `buildCardComponentForDfcFace`, since a back face's own
definition has no back face. Exposed as `Filters.DoubleFaced`. The withdrawal note guessed
`StatePredicate`; the card axis is the right one. (2) An engine bug behind it: `DoubleFacedComponent`
was only stamped by the **cast** pipeline, so a double-faced card put onto the battlefield by an
effect (reanimation, library fetch — Nick Fury's own case) had no face tracking and `TransformEffect`
was silently a no-op. New shared `stampDoubleFacedFrontFace` helper, called from
`ZoneTransitionService.applyBattlefieldEntry`; `DayNightService` now uses it too.

**Gate.** `just test` — 13,243 tests, the only failure the expected MSH golden diff (counted from the
JUnit XML; rules-engine 11,144 / 0 failures). `just rebless-cards` then `:mtg-sets:test` → 345/0;
snapshot diff is 130 insertions, 0 deletions, only Nick Fury added. `just check-card-printing` ok,
`just fix-backlog` → 271/276.
`docs/card-sdk-language-reference.md` updated (`Filters.DoubleFaced` + a `Transform` effect entry).

## Things I'm unsure about — please look

- **Predicate coverage is deliberately incomplete in `TriggerDetector` — in five places, not one.**
  (Corrected after review; the original note named only the creature-death filter.) Five partial
  `when (predicate)` blocks in that file have no `IsDoubleFaced` branch and fall through to an
  `else -> true` fail-open: `detectLibraryToGraveyardBatchTriggers`, `cardMatchesGraveyardBatchFilter`,
  `sacrificedPermanentMatchesFilter`, `detectLeaveBattlefieldWithoutDyingBatchTriggers` and
  `deathMatchesFilter`. Their LKI records carry no card component, so answering needs a new field —
  the same treatment `HasAdventure` gets there (it has zero branches in this file). The one branch
  that *was* added in `TriggerDetector` is the enters-the-battlefield batch filter
  (`matchesCardPredicates`), not creature death. No in-scope card puts this predicate in a trigger
  filter. Every other enumerator got a real branch (8 of them; table in the PR body).
- **`StackResolver`'s cast-path DFC stamp was left alone**, so the front-face stamp now exists in two
  places (there, and the new helper). Deliberate — the cast path has its own `castFaceDown` guard that
  the helper doesn't model — but a reviewer may prefer them unified.
- **Modal DFCs count as double-faced cards here**, so Nick Fury will offer the transform on one and
  `flipDfcInPlace` will turn it over. I read CR 712.1/712.3 and 701.27 as allowing this, but I have
  not played it out against a real MSH modal-DFC hero.
- **`CardComponent.isDoubleFaced` is a defaulted parameter**, which the unit brief warns is a drift
  trap. Making it required would touch every `CardComponent(...)` call site in the repo; I followed the
  `hasAdventure` precedent instead and swept the definition-derived construction sites by grep. If a
  new one is added later and forgets the flag, the predicate quietly answers `false` there.
- **No manual playthrough, no UX pass on the prompt wording, no e2e, no AI-heuristic review.**
