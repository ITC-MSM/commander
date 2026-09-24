package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Way of the Mind Sculptor (FRA #224) — {4}{U} Legendary Enchantment:
 *   When Way of the Mind Sculptor enters, empower Jace 5.
 *   Whenever you activate a loyalty ability, if you removed two or more loyalty counters to
 *   activate it, draw a card.
 *
 * Pins the removed-counter threshold on the activation: a [−X] with X = 2 draws, X = 1 doesn't,
 * a fixed [−1] doesn't, a [+1] doesn't, and an opponent's big minus doesn't.
 */
class WayOfTheMindSculptorScenarioTest : ScenarioTestBase() {
    init {
        val chandraAbilities = cardRegistry.getCard("Chandra Nalaar")!!.script.activatedAbilities
        val minusX = chandraAbilities.single { it.cost == AbilityCost.LoyaltyX }.id
        val ajaniAbilities = cardRegistry.getCard("Ajani Goldmane")!!.script.activatedAbilities
        fun ajani(change: Int) = ajaniAbilities.single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

        fun base(controller: Int = 1) = scenario().withPlayers()
            .withCardOnBattlefield(1, "Way of the Mind Sculptor")
            .withCardOnBattlefield(controller, "Chandra Nalaar")
            .withCardOnBattlefield(controller, "Ajani Goldmane")
            .withCardOnBattlefield(3 - controller, "Grizzly Bears")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(controller)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("entering empowers Jace 5") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Way of the Mind Sculptor")
                .withLandsOnBattlefield(1, "Island", 5)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Way of the Mind Sculptor").error shouldBe null
            game.resolveStack()

            val token = game.findPermanents("Jace").single()
            game.state.getEntity(token)!!.get<CountersComponent>()!!.getCount(CounterType.LOYALTY) shouldBe 5
        }

        test("removing two loyalty counters with −X draws a card") {
            val game = base().build()
            val chandra = game.findPermanent("Chandra Nalaar")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(
                ActivateAbility(
                    game.player1Id, chandra, minusX,
                    targets = listOf(ChosenTarget.Permanent(bears)), xValue = 2,
                )
            ).error shouldBe null
            game.resolveStack()

            game.handSize(1) shouldBe 1
            game.isOnBattlefield("Grizzly Bears") shouldBe false
        }

        test("removing one loyalty counter doesn't draw — neither −X with X=1 nor a fixed −1") {
            val game = base().build()
            val chandra = game.findPermanent("Chandra Nalaar")!!
            val ajani = game.findPermanent("Ajani Goldmane")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(
                ActivateAbility(
                    game.player1Id, chandra, minusX,
                    targets = listOf(ChosenTarget.Permanent(bears)), xValue = 1,
                )
            ).error shouldBe null
            game.resolveStack()
            game.execute(ActivateAbility(game.player1Id, ajani, ajani(-1))).error shouldBe null
            game.resolveStack()

            game.handSize(1) shouldBe 0
        }

        test("a plus ability doesn't draw") {
            val game = base().build()
            val ajani = game.findPermanent("Ajani Goldmane")!!
            game.execute(ActivateAbility(game.player1Id, ajani, ajani(1))).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe 0
        }

        test("an opponent's loyalty ability doesn't draw") {
            val game = base(controller = 2).build()
            val chandra = game.findPermanent("Chandra Nalaar")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(
                ActivateAbility(
                    game.player2Id, chandra, minusX,
                    targets = listOf(ChosenTarget.Permanent(bears)), xValue = 2,
                )
            ).error shouldBe null
            game.resolveStack()

            withClue("\"you activate\" — only Way's controller's activations count") {
                game.handSize(1) shouldBe 0
                game.handSize(2) shouldBe 0
            }
        }
    }
}
