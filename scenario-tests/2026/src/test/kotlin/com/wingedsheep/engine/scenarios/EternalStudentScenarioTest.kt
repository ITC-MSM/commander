package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Eternal Student — graveyard-activated ability with an exile-self cost.
 *
 * "{1}{B}, Exile this card from your graveyard: Create two 1/1 white and black
 * Inkling creature tokens with flying."
 */
class EternalStudentScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("Eternal Student: pay {1}{B} + exile from graveyard creates two 1/1 flying Inkling tokens") {
            val game = scenario()
                .withPlayers()
                .withCardInGraveyard(1, "Eternal Student")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val student = game.findCardsInGraveyard(1, "Eternal Student").single()
            val ability = cardRegistry.getCard("Eternal Student")!!.script.activatedAbilities[0]

            game.execute(ActivateAbility(game.player1Id, student, ability.id)).error shouldBe null
            game.resolveStack()

            // The card paid the exile-self cost and is now in exile, not the graveyard.
            game.state.getExile(game.player1Id) shouldContain student
            game.state.getGraveyard(game.player1Id) shouldNotContain student

            // Two 1/1 flying Inkling tokens were created on the battlefield.
            val projected = projector.project(game.state)
            val tokens = game.state.getBattlefield(game.player1Id).filter { entity ->
                projected.isCreature(entity) &&
                    projected.getSubtypes(entity).contains("Inkling") &&
                    projected.getPower(entity) == 1 &&
                    projected.getToughness(entity) == 1 &&
                    projected.hasKeyword(entity, Keyword.FLYING)
            }
            withClue("Two 1/1 flying Inkling tokens should have been created") {
                tokens.size shouldBe 2
            }
        }
    }
}
