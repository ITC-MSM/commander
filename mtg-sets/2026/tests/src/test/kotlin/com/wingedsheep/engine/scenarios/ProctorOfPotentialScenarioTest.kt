package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Proctor of Potential (FRA #145) — surveils when it or another creature you control enters; its
 * graveyard return is activatable only if you've scried or surveilled this turn.
 */
class ProctorOfPotentialScenarioTest : ScenarioTestBase() {
    init {
        val returnAbility = cardRegistry.getCard("Proctor of Potential")!!.script.activatedAbilities.single().id

        fun game() = scenario().withPlayers()
            .withCardInGraveyard(1, "Proctor of Potential")
            .withCardInHand(1, "Opt")
            .withLandsOnBattlefield(1, "Island", 2)
            .withLandsOnBattlefield(1, "Plains", 1)
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("the graveyard return needs a scry or surveil this turn, then returns with a finality counter") {
            val game = game()
            val proctor = game.findCardsInGraveyard(1, "Proctor of Potential").single()
            withClue("no scry or surveil yet this turn") {
                game.execute(ActivateAbility(game.player1Id, proctor, returnAbility)).error shouldNotBe null
            }

            game.castSpell(1, "Opt").error shouldBe null
            game.resolveStack()
            // Scry 1 keeps the card on top: decline the bottom pick, then keep the (one-card) order.
            while (game.hasPendingDecision()) {
                when (game.getPendingDecision()) {
                    is SelectCardsDecision -> game.skipSelection().error shouldBe null
                    else -> game.keepLibraryOrder().error shouldBe null
                }
            }
            game.state.stack shouldBe emptyList()

            game.execute(ActivateAbility(game.player1Id, proctor, returnAbility)).error shouldBe null
            game.resolveStack()
            val onField = game.findPermanent("Proctor of Potential")!!
            game.state.getEntity(onField)!!.get<CountersComponent>()!!
                .getCount(CounterType.FINALITY) shouldBe 1

            withClue("its own entry triggers the surveil") {
                game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
            }
        }
    }
}
