package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Alquist Proft, Master Sleuth (MKM #185) — {1}{W}{U} 3/3 Legendary Human Detective.
 *
 * "Vigilance
 *  When Alquist Proft enters, investigate.
 *  {X}{W}{U}{U}, {T}, Sacrifice a Clue: You draw X cards and gain X life."
 *
 * Three claims worth pinning:
 *
 *  - the enters trigger produces the Clue that the activated ability then eats, so the card is
 *    self-sufficient the turn after it lands;
 *  - `X` is announced once and read twice — the cards drawn and the life gained must agree, which a
 *    pair of independently-evaluated amounts would not guarantee;
 *  - **a Clue is an artifact type, not a token-ness test** (2024-02-02 ruling). Wrench is a printed
 *    `Artifact — Clue Equipment`, and it pays the cost exactly as an investigated token would. A
 *    filter written against tokens instead of the subtype would reject it, which is why the payment
 *    test deliberately uses Wrench rather than the Clue the creature makes for itself.
 */
class AlquistProftMasterSleuthScenarioTest : ScenarioTestBase() {

    private val proftAbility by lazy {
        cardRegistry.getCard("Alquist Proft, Master Sleuth")!!.script.activatedAbilities.single().id
    }

    init {
        context("Alquist Proft, Master Sleuth") {

            test("entering investigates for the Clue that fuels its own ability") {
                val game = scenario()
                    .withPlayers("Sleuth", "Opponent")
                    .withCardInHand(1, "Alquist Proft, Master Sleuth")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Alquist Proft, Master Sleuth")
                withClue("casting should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("the enters trigger investigates exactly once") {
                    game.findPermanents("Clue").size shouldBe 1
                }
            }

            test("X = 2 sacrifices a Clue artifact to draw two and gain two") {
                val game = scenario()
                    .withPlayers("Sleuth", "Opponent")
                    .withCardOnBattlefield(1, "Alquist Proft, Master Sleuth", summoningSickness = false)
                    .withCardOnBattlefield(1, "Wrench")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Centaur Courser")
                    .withCardInLibrary(1, "Savannah Lions")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val proft = game.findPermanent("Alquist Proft, Master Sleuth")!!
                val wrench = game.findPermanent("Wrench")!!
                val handBefore = game.handSize(1)
                val lifeBefore = game.getLifeTotal(1)

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = proft,
                        abilityId = proftAbility,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(wrench)),
                        xValue = 2
                    )
                )
                withClue("a printed Clue artifact pays the sacrifice: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("X = 2 draws two") { game.handSize(1) shouldBe handBefore + 2 }
                withClue("and the same X gains two") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 2
                }
                withClue("the Clue was spent as a cost") {
                    game.isInGraveyard(1, "Wrench") shouldBe true
                }
            }

            test("X = 0 is a legal activation that draws and gains nothing") {
                val game = scenario()
                    .withPlayers("Sleuth", "Opponent")
                    .withCardOnBattlefield(1, "Alquist Proft, Master Sleuth", summoningSickness = false)
                    .withCardOnBattlefield(1, "Wrench")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val proft = game.findPermanent("Alquist Proft, Master Sleuth")!!
                val wrench = game.findPermanent("Wrench")!!
                val handBefore = game.handSize(1)
                val lifeBefore = game.getLifeTotal(1)

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = proft,
                        abilityId = proftAbility,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(wrench)),
                        xValue = 0
                    )
                )
                withClue("X = 0 is a legal announcement: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("zero cards") { game.handSize(1) shouldBe handBefore }
                withClue("zero life") { game.getLifeTotal(1) shouldBe lifeBefore }
                withClue("but the Clue is still spent — costs are paid regardless") {
                    game.isInGraveyard(1, "Wrench") shouldBe true
                }
            }
        }
    }
}
