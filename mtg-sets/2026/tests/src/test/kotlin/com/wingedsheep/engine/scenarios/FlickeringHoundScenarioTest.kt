package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Flickering Hound (FRA #7, {3}{W}, 2/2).
 *
 *   Whenever you cast a creature spell, exile up to one other target creature you control, then
 *   return that card to the battlefield under its owner's control.
 */
class FlickeringHoundScenarioTest : ScenarioTestBase() {

    init {
        context("Flickering Hound") {

            test("casting a creature spell flickers another creature you control, re-running its ETB") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Flickering Hound")
                    .withCardOnBattlefield(1, "Elvish Visionary", tapped = true)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val visionary = game.findPermanent("Elvish Visionary")!!
                game.castSpell(1, "Grizzly Bears").error shouldBe null
                withClue("the cast trigger asks for up to one target") {
                    game.hasPendingDecision().shouldBeTrue()
                }
                game.selectTargets(listOf(visionary))
                val handBefore = game.handSize(1)
                game.resolveStack()

                val returned = game.findPermanent("Elvish Visionary")
                withClue("the Visionary came back untapped (a new object)") {
                    returned shouldNotBe null
                    game.state.getEntity(returned!!)!!.has<TappedComponent>().shouldBeFalse()
                }
                withClue("its enters trigger drew a card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
                game.isOnBattlefield("Grizzly Bears").shouldBeTrue()
                game.isOnBattlefield("Flickering Hound").shouldBeTrue()
            }

            test("choosing no target does nothing") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Flickering Hound")
                    .withCardOnBattlefield(1, "Elvish Visionary", tapped = true)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val visionary = game.findPermanent("Elvish Visionary")!!
                game.castSpell(1, "Grizzly Bears").error shouldBe null
                if (game.hasPendingDecision()) game.skipTargets()
                game.resolveStack()

                game.findPermanent("Elvish Visionary") shouldBe visionary
                game.state.getEntity(visionary)!!.has<TappedComponent>().shouldBeTrue()
            }

            test("a noncreature spell doesn't trigger it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Flickering Hound")
                    .withCardOnBattlefield(1, "Elvish Visionary")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
                game.hasPendingDecision().shouldBeFalse()
                game.resolveStack()
                game.getLifeTotal(2) shouldBe 17
            }
        }
    }
}
