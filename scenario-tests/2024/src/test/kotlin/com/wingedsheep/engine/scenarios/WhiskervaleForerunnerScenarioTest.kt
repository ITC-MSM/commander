package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class WhiskervaleForerunnerScenarioTest : ScenarioTestBase() {

    init {
        test("Polliwallop targeting Whiskervale Forerunner triggers Valiant") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Polliwallop")
                .withCardOnBattlefield(1, "Whiskervale Forerunner")
                .withCardOnBattlefield(2, "Craw Wurm")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forerunner = game.findPermanent("Whiskervale Forerunner")!!
            val opponentCreature = game.findPermanent("Craw Wurm")!!
            val polliwallop = game.findCardsInHand(1, "Polliwallop").single()

            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = polliwallop,
                    targets = listOf(
                        ChosenTarget.Permanent(forerunner),
                        ChosenTarget.Permanent(opponentCreature)
                    )
                )
            ).error shouldBe null

            game.state.stack.size shouldBe 2
            game.state.getEntity(game.state.stack.first())
                ?.get<CardComponent>()?.name shouldBe "Polliwallop"
            game.state.getEntity(game.state.stack.last())
                ?.get<TriggeredAbilityOnStackComponent>()?.sourceName shouldBe "Whiskervale Forerunner"

            game.resolveStack()
            val revealDecision = game.getPendingDecision() as SelectCardsDecision
            val bears = revealDecision.options.single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
            }
            game.selectCards(listOf(bears)).error shouldBe null

            val destinationDecision = game.getPendingDecision() as SelectCardsDecision
            game.selectCards(listOf(destinationDecision.options.single())).error shouldBe null

            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.state.stack.single() shouldBe polliwallop
        }

        test("Valiant shows all five cards when none can be selected") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Polliwallop")
                .withCardOnBattlefield(1, "Whiskervale Forerunner")
                .withCardOnBattlefield(2, "Craw Wurm")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forerunner = game.findPermanent("Whiskervale Forerunner")!!
            val opponentCreature = game.findPermanent("Craw Wurm")!!
            val polliwallop = game.findCardsInHand(1, "Polliwallop").single()

            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = polliwallop,
                    targets = listOf(
                        ChosenTarget.Permanent(forerunner),
                        ChosenTarget.Permanent(opponentCreature)
                    )
                )
            ).error shouldBe null

            game.resolveStack()

            val decision = game.getPendingDecision() as SelectCardsDecision
            decision.options shouldBe emptyList()
            decision.nonSelectableOptions.size shouldBe 5
            decision.maxSelections shouldBe 0

            game.selectCards(emptyList()).error shouldBe null

            game.state.stack.single() shouldBe polliwallop
            game.findCardsInLibrary(1, "Forest").size shouldBe 5
        }
    }
}
