# Lorwyn Assay differential review

Initial run after adding Bog Hoodlums and Nath’s Elite: 236 canonical cards; 111 compared, 123 declined, two failed to fold. Models agree for 101 of the compared cards; ten divergences classified below. Basic lands and reprint rows are outside this canonical-card count. Declines do not verify behavior.

| Card | Classification |
| --- | --- |
| Boggart Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |
| Boggart Sprite-Chaser | Equivalent fold: CompositeStaticAbility groups the same static abilities under the same condition. |
| Elvish Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |
| Epic Proportions | Equivalent fold: CompositeStaticAbility groups the same static abilities under the same condition. |
| Faerie Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |
| Flamekin Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |
| Giant Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |
| Kithkin Greatheart | Equivalent fold: CompositeStaticAbility groups the same static abilities under the same condition. |
| Kithkin Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |
| Merrow Harbinger | Card bug: optional search gate missing; declining incorrectly searches/shuffles. |

The seven Harbinger scripts now wrap the full search in the existing optional trigger gate. Each has its own scenario test covering decline without a search or shuffle, successful search, and accepting but finding no card. All 21 scenario tests passed. Snapshot regeneration passed, with the seven old card trees changing only by the optional gate. The fresh differential now agrees on 108 of 111 compared cards; the remaining three rows are the equivalent folds below. The full build remains pending while the next three authored cards await snapshot regeneration.

The three equivalent folds were checked by expanding composite static abilities and distributing their existing condition over each child, then comparing every remaining field. No card changes are needed for those three rows.
