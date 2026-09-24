package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Ajani Resolute (FRA #195) — {1}{W} Legendary Planeswalker — Ajani, loyalty 2.
 *
 *   Whenever you gain life, put a loyalty counter on Ajani.
 *   0: You gain 1 life.
 *   −4: Create a 2/2 white Cat Soldier creature token named Ajani's Pridemate with "Whenever you
 *       gain life, put a +1/+1 counter on this token."
 *   −10: You get an emblem with "Creatures you control get +2/+2."
 */
class AjaniResoluteScenarioTest : ScenarioTestBase() {

    private fun abilityId(change: Int) = cardRegistry.getCard("Ajani Resolute")!!.script.activatedAbilities
        .single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

    private fun TestGame.counters(id: EntityId, type: CounterType): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    private fun TestGame.setLoyalty(id: EntityId, loyalty: Int) {
        state = state.updateEntity(id) { it.with(CountersComponent(mapOf(CounterType.LOYALTY to loyalty))) }
    }

    private fun board() = scenario().withPlayers()
        .withCardOnBattlefield(1, "Ajani Resolute")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withCardInLibrary(1, "Plains")
        .withCardInLibrary(2, "Plains")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("0: gain 1 life, and the life gain puts a loyalty counter back on Ajani") {
            val game = board()
            val ajani = game.findPermanent("Ajani Resolute")!!

            game.execute(ActivateAbility(game.player1Id, ajani, abilityId(0))).error shouldBe null
            game.resolveStack()

            game.getLifeTotal(1) shouldBe 21
            withClue("starting loyalty 2, 0 ability costs nothing, life gain adds one") {
                game.counters(ajani, CounterType.LOYALTY) shouldBe 3
            }
        }

        test("−4 creates Ajani's Pridemate, which grows whenever you gain life") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Ajani Resolute")
                .withCardInHand(1, "Sacred Nectar")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val ajani = game.findPermanent("Ajani Resolute")!!
            game.setLoyalty(ajani, 5)

            game.execute(ActivateAbility(game.player1Id, ajani, abilityId(-4))).error shouldBe null
            game.resolveStack()

            val pridemate = game.findPermanent("Ajani's Pridemate")
            pridemate shouldNotBe null
            game.state.projectedState.getPower(pridemate!!) shouldBe 2
            game.state.projectedState.getToughness(pridemate) shouldBe 2
            game.counters(ajani, CounterType.LOYALTY) shouldBe 1

            // Next gain (Sacred Nectar, "You gain 4 life."): both Ajani and the token trigger once.
            game.castSpell(1, "Sacred Nectar").error shouldBe null
            game.resolveStack()

            game.getLifeTotal(1) shouldBe 24
            game.counters(pridemate, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            game.state.projectedState.getPower(pridemate) shouldBe 3
            game.counters(ajani, CounterType.LOYALTY) shouldBe 2
        }

        test("−10 emblem gives creatures you control +2/+2, including ones that arrive later") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Ajani Resolute")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInHand(1, "Savannah Lions")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val ajani = game.findPermanent("Ajani Resolute")!!
            game.setLoyalty(ajani, 10)

            game.execute(ActivateAbility(game.player1Id, ajani, abilityId(-10))).error shouldBe null
            game.resolveStack()

            val bears = game.findPermanent("Grizzly Bears")!!
            val giant = game.findPermanent("Hill Giant")!!
            game.state.projectedState.getPower(bears) shouldBe 4
            game.state.projectedState.getToughness(bears) shouldBe 4
            withClue("the opponent's creature is unaffected") {
                game.state.projectedState.getPower(giant) shouldBe 3
            }

            game.castSpell(1, "Savannah Lions").error shouldBe null
            game.resolveStack()
            val lions = game.findPermanent("Savannah Lions")!!
            // The test-registry Savannah Lions is a vanilla 1/1.
            game.state.projectedState.getPower(lions) shouldBe 3
            game.state.projectedState.getToughness(lions) shouldBe 3
        }
    }
}
