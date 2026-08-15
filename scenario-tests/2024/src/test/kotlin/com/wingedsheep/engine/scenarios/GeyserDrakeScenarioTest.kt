package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.GeyserDrake
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Geyser Drake — {2}{U} Drake 2/3.
 * "During turns other than yours, spells you cast cost {1} less to cast."
 *
 * Verifies that the [com.wingedsheep.sdk.scripting.ModifySpellCost] static ability gated by
 * OnlyIf(IsNotYourTurn) reduces the generic component of the controller's spells only on
 * opponents' turns, and never reduces colored mana.
 */
class GeyserDrakeScenarioTest : FunSpec({

    fun createDriver(): Pair<GameTestDriver, CardRegistry> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GeyserDrake))

        val registry = CardRegistry()
        registry.register(TestCards.all)
        registry.register(GeyserDrake)

        return driver to registry
    }

    test("no reduction on your own turn") {
        val (driver, registry) = createDriver()
        val calculator = CostCalculator(registry)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(active, "Geyser Drake")

        // It is the active player's own turn, so no reduction applies.
        val bearsDef = registry.requireCard("Centaur Courser") // {2}{G}
        val cost = calculator.calculateEffectiveCost(driver.state, bearsDef, active)
        cost.genericAmount shouldBe 2
    }

    test("reduces generic by 1 during an opponent's turn") {
        val (driver, registry) = createDriver()
        val calculator = CostCalculator(registry)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(opponent, "Geyser Drake")

        // From the opponent's perspective it is NOT their turn, so their spells cost {1} less.
        val bearsDef = registry.requireCard("Centaur Courser") // {2}{G}
        val cost = calculator.calculateEffectiveCost(driver.state, bearsDef, opponent)
        cost.genericAmount shouldBe 1
    }

    test("does not reduce colored mana") {
        val (driver, registry) = createDriver()
        val calculator = CostCalculator(registry)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(opponent, "Geyser Drake")

        // Counterspell is {U}{U} — all colored, no generic. Reduction can't touch it.
        val counterspell = registry.requireCard("Counterspell")
        val cost = calculator.calculateEffectiveCost(driver.state, counterspell, opponent)
        cost.genericAmount shouldBe 0
        cost.cmc shouldBe 2
    }
})
