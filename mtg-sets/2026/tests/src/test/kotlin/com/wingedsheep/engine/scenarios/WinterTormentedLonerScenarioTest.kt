package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Winter, Tormented Loner (FRA #240) — a reflexive "you may sacrifice … when you do, each
 * opponent sacrifices a creature", and +1/+0 per creature and planeswalker card in your graveyard.
 */
class WinterTormentedLonerScenarioTest : ScenarioTestBase() {

    init {
        fun castWinter(): TestGame {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Winter, Tormented Loner")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInGraveyard(1, "Lightning Bolt")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            game.castSpell(1, "Winter, Tormented Loner").error shouldBe null
            game.resolveStack() // Winter resolves, her enters trigger goes on the stack
            game.resolveStack() // the trigger resolves and asks whether to sacrifice
            return game
        }

        context("Winter, Tormented Loner") {
            test("sacrificing a creature makes each opponent sacrifice one, and Winter grows") {
                val game = castWinter()
                val winter = game.findPermanent("Winter, Tormented Loner").shouldNotBeNull()
                withClue("a noncreature card in the graveyard doesn't count") {
                    game.state.projectedState.getPower(winter) shouldBe 0
                }

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true).error shouldBe null
                val choice = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                withClue("Winter herself is also a legal sacrifice") { choice.options.contains(winter) shouldBe true }
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack() // the reflexive trigger

                game.isOnBattlefield("Grizzly Bears") shouldBe false
                game.isOnBattlefield("Hill Giant") shouldBe false
                withClue("the sacrificed Bears count toward Winter's power") {
                    game.state.projectedState.getPower(winter) shouldBe 1
                }
            }

            test("declining leaves the opponent's creatures alone") {
                val game = castWinter()

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe true
                game.isOnBattlefield("Hill Giant") shouldBe true
            }
        }
    }
}
