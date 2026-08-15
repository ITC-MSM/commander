package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Faebloom Trick (FDN #38) — {2}{U} Instant.
 *
 * "Create two 1/1 blue Faerie creature tokens with flying. When you do, tap target creature an
 * opponent controls."
 *
 * Verifies the reflexive "when you do" trigger: two Faerie tokens are created, and the reflexive
 * trigger taps a chosen opponent creature.
 */
class FaebloomTrickScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Enemy Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        context("Faebloom Trick") {

            test("creates two Faerie tokens and taps an opponent's creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Faebloom Trick")
                    .withCardOnBattlefield(2, "Enemy Bear", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Faebloom Trick")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // The reflexive "when you do" trigger goes on the stack and asks for its tap target.
                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the reflexive tap; got ${game.state.pendingDecision}")
                val enemyBear = game.findPermanent("Enemy Bear")!!
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(enemyBear))))
                game.resolveStack()

                withClue("Two 1/1 Faerie tokens should have been created") {
                    game.findPermanents("Faerie Token").size shouldBe 2
                }
                val bearCard = game.getClientState(1).cards.values.first { it.name == "Enemy Bear" }
                withClue("The opponent's Enemy Bear should be tapped") {
                    bearCard.isTapped shouldBe true
                }
            }
        }
    }
}
