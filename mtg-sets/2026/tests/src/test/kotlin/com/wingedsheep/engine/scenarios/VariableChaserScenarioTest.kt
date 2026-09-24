package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Variable Chaser // Arc of Fortune (FRA #47) — a flying, prowess 2/3 that enters prepared; its
 * prepared spell lets each player choose to discard their hand and draw seven.
 */
class VariableChaserScenarioTest : ScenarioTestBase() {

    init {
        context("Variable Chaser // Arc of Fortune") {
            test("each player chooses separately whether to wheel") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Variable Chaser")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(2, "Hill Giant")
                    .withCardInHand(2, "Hill Giant")
                    .withCardInHand(2, "Hill Giant")
                    .withLandsOnBattlefield(1, "Island", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(10) { b = b.withCardInLibrary(1, "Island") }
                repeat(10) { b = b.withCardInLibrary(2, "Island") }
                val game = b.build()

                game.castSpell(1, "Variable Chaser").error shouldBe null
                game.resolveStack()

                val chaser = game.findPermanent("Variable Chaser")!!
                val projected = game.state.projectedState
                projected.hasKeyword(chaser, Keyword.FLYING) shouldBe true
                projected.hasKeyword(chaser, Keyword.PROWESS) shouldBe true

                val copy = game.state.getExile(game.player1Id).firstOrNull { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Variable Chaser"
                }
                withClue("entering prepared makes an Arc of Fortune copy available") { copy shouldNotBe null }

                game.execute(CastSpell(game.player1Id, copy!!, faceIndex = 0)).error shouldBe null
                game.resolveStack()

                val first = game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                first.playerId shouldBe game.player1Id
                game.answerYesNo(true).error shouldBe null

                val second = game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                second.playerId shouldBe game.player2Id
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("you discarded two cards and drew seven") {
                    game.handSize(1) shouldBe 7
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 2
                }
                withClue("the opponent declined and kept their hand") {
                    game.handSize(2) shouldBe 3
                    game.graveyardSize(2) shouldBe 0
                }
            }
        }
    }
}
