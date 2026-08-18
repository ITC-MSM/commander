package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Judith, Carnage Connoisseur (MKM #210) — {3}{B}{R} 3/4 Legendary Human Shaman.
 *
 * "Whenever you cast an instant or sorcery spell, choose one —
 *  • That spell gains deathtouch and lifelink.
 *  • Create a 2/2 red Imp creature token with "When this token dies, it deals 2 damage to each
 *    opponent.""
 *
 * Mode one is the reason this file exists. Keywords granted to a *spell* live on the stack object,
 * which static keyword projection never reaches — so a damage path that reads deathtouch or
 * lifelink only through `projected.hasKeyword` sees nothing, and the mode silently does nothing.
 * Lifelink already consulted the spell-grant channel; deathtouch did not, and now does
 * (`DamageUtils.sourceHasDeathtouch`). The Shock-into-a-3/3 test is the shape that catches it: a
 * 3/3 survives 2 damage unless deathtouch applied, so the kill *is* the assertion.
 *
 * The same test run without Judith is the control — it proves the 3/3 dying is the grant's doing
 * and not something about Shock or the harness.
 *
 * Mode two's Imp carries its own death trigger, so the drain has to survive the token leaving the
 * battlefield: it is last-known information about a token that no longer exists when the ability
 * resolves.
 */
class JudithCarnageConnoisseurScenarioTest : ScenarioTestBase() {

    init {
        context("Judith, Carnage Connoisseur") {

            test("mode one: the spell gains deathtouch, so 2 damage kills a 3/3") {
                val game = scenario()
                    .withPlayers("Judith", "Opponent")
                    .withCardOnBattlefield(1, "Judith, Carnage Connoisseur", summoningSickness = false)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val lifeBefore = game.getLifeTotal(1)

                val cast = game.castSpell(1, "Shock", courser)
                withClue("casting Shock should succeed: ${cast.error}") { cast.error shouldBe null }

                // Judith's trigger goes on the stack above Shock and resolves first.
                game.resolveStack()
                val modeDecision = game.getPendingDecision() as? ChooseOptionDecision
                    ?: error("expected a mode choice for Judith's trigger; got ${game.getPendingDecision()}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 0))

                game.resolveStack()
                game.checkStateBasedActions()

                withClue("a 3/3 dealt 2 deathtouch damage is destroyed as a state-based action") {
                    game.isInGraveyard(2, "Centaur Courser") shouldBe true
                }
                withClue("and lifelink on the same spell gains its controller 2") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 2
                }
            }

            test("control: without Judith, Shock leaves the 3/3 alive and gains nothing") {
                val game = scenario()
                    .withPlayers("Burner", "Opponent")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Shock", courser).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("2 damage does not kill a 3/3 on its own") {
                    game.isInGraveyard(2, "Centaur Courser") shouldBe false
                }
                withClue("and no life is gained") { game.getLifeTotal(1) shouldBe lifeBefore }
            }

            test("mode two: creates a 2/2 red Imp whose death drains each opponent for 2") {
                val game = scenario()
                    .withPlayers("Judith", "Opponent")
                    .withCardOnBattlefield(1, "Judith, Carnage Connoisseur", summoningSickness = false)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Point Judith's own Shock at the opponent so the only creature that can die is the Imp.
                val cast = game.castSpellTargetingPlayer(1, "Shock", 2)
                withClue("casting Shock should succeed: ${cast.error}") { cast.error shouldBe null }

                game.resolveStack()
                val modeDecision = game.getPendingDecision() as? ChooseOptionDecision
                    ?: error("expected a mode choice for Judith's trigger; got ${game.getPendingDecision()}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 1))
                game.resolveStack()

                val imp = game.findPermanent("Imp Token")
                withClue("mode two put an Imp token on the battlefield") { (imp != null) shouldBe true }

                val opponentLifeBefore = game.getLifeTotal(2)

                // The opponent kills the token; their own Shock never triggers Judith ("you cast").
                game.passPriority()
                val kill = game.castSpell(2, "Shock", imp!!)
                withClue("the opponent can Shock the Imp: ${kill.error}") { kill.error shouldBe null }
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("the 2/2 Imp died to 2 damage") { game.findPermanent("Imp Token") shouldBe null }
                withClue("its death trigger drained each opponent of its controller for 2") {
                    game.getLifeTotal(2) shouldBe opponentLifeBefore - 2
                }
            }
        }
    }
}
