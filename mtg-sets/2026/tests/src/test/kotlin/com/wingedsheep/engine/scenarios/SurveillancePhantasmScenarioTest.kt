package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.player.ScriedOrSurveiledThisTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Surveillance Phantasm (FRA #42) — defender that can attack as long as you've scried or surveilled
 * this turn.
 *
 * Pins `TurnTracker.SCRIED_OR_SURVEILED`: unset at the start of the turn, set by a surveil even when
 * the looked-at card stays on top, and read by `CanAttackDespiteDefender`.
 */
class SurveillancePhantasmScenarioTest : ScenarioTestBase() {
    init {
        fun game() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Surveillance Phantasm")
            .withLandsOnBattlefield(1, "Island", 4)
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("it can't attack before you've scried or surveilled this turn") {
            val game = game()
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Surveillance Phantasm" to 2)).error shouldNotBe null
        }

        test("after surveilling it can attack, even keeping the card on top") {
            val game = game()
            val phantasm = game.findPermanent("Surveillance Phantasm")!!
            val surveil = cardRegistry.getCard("Surveillance Phantasm")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, phantasm, surveil)).error shouldBe null
            game.resolveStack()
            game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
            game.skipSelection().error shouldBe null
            game.resolveStack()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            withClue("a surveil that put nothing in the graveyard still counts") {
                game.declareAttackers(mapOf("Surveillance Phantasm" to 2)).error shouldBe null
            }

            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            withClue("the record is turn history — cleared at end of turn") {
                game.state.activePlayerId shouldBe game.player2Id
                game.state.getEntity(game.player1Id)!!.has<ScriedOrSurveiledThisTurnComponent>() shouldBe false
            }
        }
    }
}
