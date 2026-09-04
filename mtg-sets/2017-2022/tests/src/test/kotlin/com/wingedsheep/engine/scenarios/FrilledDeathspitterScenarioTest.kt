package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Frilled Deathspitter (RIX #104) — {2}{R} Creature — Dinosaur, 3/2.
 *
 *   Enrage — Whenever this creature is dealt damage, it deals 2 damage to target opponent or
 *   planeswalker.
 *
 * Two things are first-of-their-kind here and neither is covered elsewhere in the corpus:
 * `Targets.OpponentOrPlaneswalker` has no other card user, and the enrage trigger is a
 * SELF-binding `DamageReceivedEvent` whose payload targets a *player* rather than the damage
 * source. The printed ruling that lethal damage still fires the trigger is asserted as its own
 * case — the Dinosaur is already in the graveyard when the 2 damage lands.
 *
 * The trigger targets even when only one opponent is legal, so each case answers the
 * `ChooseTargetsDecision` rather than expecting an auto-pick.
 */
class FrilledDeathspitterScenarioTest : ScenarioTestBase() {

    init {
        context("Frilled Deathspitter") {

            test("non-lethal damage fires enrage, dealing 2 to the chosen opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Frilled Deathspitter")
                    .withCardInHand(1, "Giant Growth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val deathspitter = game.findPermanent("Frilled Deathspitter")!!
                val lifeBefore = game.getLifeTotal(2)

                // Player 2 aims 3 damage at the 3/2; Player 1 responds with Giant Growth, so the
                // Dinosaur survives at 6/5 and the enrage trigger resolves with its source alive.
                game.castSpell(2, "Lightning Bolt", deathspitter).error shouldBe null
                game.passPriority() // Player 2 keeps priority after casting; hand it to Player 1.
                game.castSpell(1, "Giant Growth", deathspitter).error shouldBe null
                game.resolveStack()

                withClue("the pumped 6/5 survives 3 damage") {
                    game.isOnBattlefield("Frilled Deathspitter") shouldBe true
                }

                game.getPendingDecision().shouldNotBeNull()
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("enrage deals 2 to the chosen opponent") {
                    game.getLifeTotal(2) shouldBe lifeBefore - 2
                }
            }

            test("lethal damage still fires enrage, after the Dinosaur has died") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Frilled Deathspitter")
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val deathspitter = game.findPermanent("Frilled Deathspitter")!!
                val lifeBefore = game.getLifeTotal(2)

                game.castSpell(2, "Lightning Bolt", deathspitter).error shouldBe null
                game.resolveStack()

                withClue("3 damage is lethal to a 3/2, and it dies before the trigger resolves") {
                    game.isOnBattlefield("Frilled Deathspitter") shouldBe false
                    game.isInGraveyard(1, "Frilled Deathspitter") shouldBe true
                }

                game.getPendingDecision().shouldNotBeNull()
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("the enrage trigger resolves anyway (the printed ruling)") {
                    game.getLifeTotal(2) shouldBe lifeBefore - 2
                }
            }
        }
    }
}
