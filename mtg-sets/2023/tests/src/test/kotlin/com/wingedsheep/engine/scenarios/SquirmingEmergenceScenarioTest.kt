package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Squirming Emergence (LCI #241, {1}{B}{G} Sorcery).
 *
 * "Fathomless descent — Return to the battlefield target nonland permanent card in your graveyard
 *  with mana value less than or equal to the number of permanent cards in your graveyard."
 *
 * The mana-value cap on the target is DYNAMIC: it equals the number of permanent cards in the
 * caster's graveyard (the "fathomless descent" count), evaluated at target selection.
 *
 * Tests:
 *  1. With 3 permanent cards in the graveyard (cap = 3), a MV-3 nonland permanent card
 *     (Centaur Courser) is a legal target and is returned to the battlefield untapped.
 *  2. With only 2 permanent cards in the graveyard (cap = 2), a MV-3 permanent card
 *     (Centaur Courser) is above the cap — the CastSpell action is rejected.
 *  3. A nonpermanent card in the graveyard (Lightning Bolt, an instant) cannot be targeted even
 *     when its mana value is within the cap — the CastSpell action is rejected.
 */
class SquirmingEmergenceScenarioTest : ScenarioTestBase() {
    init {
        context("Squirming Emergence") {

            // ------------------------------------------------------------------
            // Test 1: cap met exactly (3 permanent cards -> cap 3), MV-3 target
            //         returned to the battlefield untapped.
            // ------------------------------------------------------------------
            test("returns a MV-3 nonland permanent card when the graveyard holds 3 permanent cards") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Squirming Emergence")
                    .withCardInGraveyard(1, "Centaur Courser") // {2}{G}, MV 3 — the target
                    .withCardInGraveyard(1, "Goblin Guide")    // {R}, MV 1 — permanent card #2
                    .withCardInGraveyard(1, "Savannah Lions")  // {W}, MV 1 — permanent card #3
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingGraveyardCard(1, "Squirming Emergence", 1, "Centaur Courser")
                game.resolveStack()

                withClue("Centaur Courser should be on the battlefield after reanimation") {
                    game.isOnBattlefield("Centaur Courser") shouldBe true
                }
                withClue("Centaur Courser should no longer be in the graveyard") {
                    game.isInGraveyard(1, "Centaur Courser") shouldBe false
                }
                val id = game.findPermanent("Centaur Courser")
                    ?: error("Centaur Courser not found on battlefield")
                withClue("Squirming Emergence returns the card untapped") {
                    (game.state.getEntity(id)?.has<TappedComponent>() ?: false) shouldBe false
                }
            }

            // ------------------------------------------------------------------
            // Test 2: target above the dynamic cap (2 permanent cards -> cap 2,
            //         target MV 3) — CastSpell rejected, card stays in graveyard.
            // ------------------------------------------------------------------
            test("cannot target a permanent card whose mana value exceeds the graveyard permanent count") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Squirming Emergence")
                    .withCardInGraveyard(1, "Centaur Courser") // {2}{G}, MV 3 — above cap of 2
                    .withCardInGraveyard(1, "Goblin Guide")    // {R}, MV 1 — permanent card #2
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val result = game.castSpellTargetingGraveyardCard(1, "Squirming Emergence", 1, "Centaur Courser")

                withClue("Targeting a MV-3 card with a cap of 2 should be rejected") {
                    result.isSuccess shouldBe false
                }
                withClue("Centaur Courser should remain in the graveyard") {
                    game.isInGraveyard(1, "Centaur Courser") shouldBe true
                }
            }

            // ------------------------------------------------------------------
            // Test 3: nonpermanent card cannot be targeted even within the cap.
            // ------------------------------------------------------------------
            test("cannot target a nonpermanent (instant) card in the graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Squirming Emergence")
                    .withCardInGraveyard(1, "Lightning Bolt")  // {R} instant, MV 1 — not a permanent card
                    .withCardInGraveyard(1, "Goblin Guide")    // {R}, MV 1 — permanent card #1
                    .withCardInGraveyard(1, "Savannah Lions")  // {W}, MV 1 — permanent card #2
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val result = game.castSpellTargetingGraveyardCard(1, "Squirming Emergence", 1, "Lightning Bolt")

                withClue("Targeting a nonpermanent card should be rejected") {
                    result.isSuccess shouldBe false
                }
                withClue("Lightning Bolt should remain in the graveyard") {
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe true
                }
            }
        }
    }
}
