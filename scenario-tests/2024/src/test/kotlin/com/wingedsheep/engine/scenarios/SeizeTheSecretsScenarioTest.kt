package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.SeizeTheSecrets
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Seize the Secrets:
 *  - "{2}{U} Sorcery — This spell costs {1} less to cast if you've committed a crime this turn.
 *     Draw two cards."
 *
 * Pins the turn-scoped crime tracker end-to-end: committing a crime (casting a spell at an
 * opponent's permanent) flips `playersWhoCommittedCrimeThisTurn`, which the cost-reduction gate
 * (`CostGating.OnlyIf(YouCommittedCrimeThisTurn)`) reads at cast time. Only the player who
 * committed the crime gets the discount, and it must have happened *before* this cast (Seize the
 * Secrets has no target of its own, so it never commits a crime).
 */
class SeizeTheSecretsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SeizeTheSecrets)
        return driver
    }

    test("full cost {2}{U} = 3 when no crime has been committed this turn") {
        val registry = CardRegistry()
        registry.register(TestCards.all)
        registry.register(SeizeTheSecrets)
        val calculator = CostCalculator(registry)

        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val def = registry.requireCard("Seize the Secrets")
        calculator.calculateEffectiveCost(driver.state, def, you).cmc shouldBe 3
    }

    test("costs {1} less = 2 once you've committed a crime this turn") {
        val registry = CardRegistry()
        registry.register(TestCards.all)
        registry.register(SeizeTheSecrets)
        val calculator = CostCalculator(registry)

        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = if (you == driver.player1) driver.player2 else driver.player1

        // The opponent controls a creature; targeting it with a spell is a crime.
        val victim = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")

        // Cast Lightning Bolt at the opponent's creature → commits a crime.
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, listOf(victim)).isSuccess shouldBe true

        driver.state.playersWhoCommittedCrimeThisTurn.contains(you) shouldBe true

        val def = registry.requireCard("Seize the Secrets")
        calculator.calculateEffectiveCost(driver.state, def, you).cmc shouldBe 2
    }

    test("opponent's crime does not discount your Seize the Secrets") {
        val registry = CardRegistry()
        registry.register(TestCards.all)
        registry.register(SeizeTheSecrets)
        val calculator = CostCalculator(registry)

        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = if (you == driver.player1) driver.player2 else driver.player1

        // Simulate the opponent (not you) having committed a crime this turn.
        driver.replaceState(
            driver.state.copy(
                playersWhoCommittedCrimeThisTurn = setOf(opponent),
            ),
        )

        val def = registry.requireCard("Seize the Secrets")
        // You haven't committed a crime, so no discount.
        calculator.calculateEffectiveCost(driver.state, def, you).cmc shouldBe 3
    }
})
