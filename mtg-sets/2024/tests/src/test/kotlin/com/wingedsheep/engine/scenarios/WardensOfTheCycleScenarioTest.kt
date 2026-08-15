package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.matchers.shouldBe

/**
 * Wardens of the Cycle — "{1}{B}{G}{G} 3/4. Morbid — At the beginning of your end step, if a
 * creature died this turn, choose one — • You gain 2 life. • You draw a card and you lose 1 life."
 */
class WardensOfTheCycleScenarioTest : ScenarioTestBase() {

    init {
        // {0} sorcery to send a chosen creature to the graveyard, arming Morbid.
        val slay = card("Slay") {
            manaCost = "{0}"
            typeLine = "Sorcery"
            spell {
                val c = target("target creature", Targets.Creature)
                effect = Effects.Destroy(c)
            }
        }
        cardRegistry.register(listOf(slay))

        test("a creature died this turn: choose 'gain 2 life'") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Wardens of the Cycle")
                .withCardOnBattlefield(2, "Aegis Turtle")
                .withCardInHand(1, "Slay")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val turtle = game.findPermanent("Aegis Turtle")!!
            game.castSpell(1, "Slay", targetId = turtle).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Aegis Turtle") shouldBe false

            val lifeBefore = game.getLifeTotal(1)
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()   // drain the end-step trigger; pauses at the mode choice

            val dec = game.getPendingDecision() as? ChooseOptionDecision
                ?: error("expected a ChooseOptionDecision for the Morbid modal; got ${game.getPendingDecision()}")
            game.submitDecision(OptionChosenResponse(dec.id, optionIndex = 0))   // gain 2 life
            game.resolveStack()

            game.getLifeTotal(1) shouldBe lifeBefore + 2
        }

        test("no creature died: the Morbid trigger does not fire") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Wardens of the Cycle")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val lifeBefore = game.getLifeTotal(1)
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()

            game.hasPendingDecision() shouldBe false
            game.getLifeTotal(1) shouldBe lifeBefore
        }
    }
}
