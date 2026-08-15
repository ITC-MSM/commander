package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.Cactarantula
import com.wingedsheep.mtg.sets.definitions.otj.cards.ConduitPylons
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Cactarantula's cost reduction (this spell costs {1} less to cast if you
 * control a Desert) via [com.wingedsheep.engine.mechanics.mana.CostCalculator].
 *
 * Cactarantula: {4}{G}{G}, Plant Spider 6/5, Reach.
 */
class CactarantulaCostScenarioTest : FunSpec({

    fun createDriver(): Pair<GameTestDriver, CardRegistry> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Cactarantula, ConduitPylons))

        val registry = CardRegistry()
        registry.register(TestCards.all)
        registry.register(Cactarantula)
        registry.register(ConduitPylons)
        return driver to registry
    }

    test("no Desert - full {4}{G}{G} cost") {
        val (driver, registry) = createDriver()
        val calculator = CostCalculator(registry)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val cost = calculator.calculateEffectiveCost(driver.state, registry.requireCard("Cactarantula"), active)
        cost.genericAmount shouldBe 4
    }

    test("control a Desert - costs {1} less") {
        val (driver, registry) = createDriver()
        val calculator = CostCalculator(registry)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(active, "Conduit Pylons")

        val cost = calculator.calculateEffectiveCost(driver.state, registry.requireCard("Cactarantula"), active)
        cost.genericAmount shouldBe 3
    }
})

/**
 * Scenario test for Cactarantula's targeting trigger:
 * "Whenever this creature becomes the target of a spell or ability an opponent controls,
 *  you may draw a card."
 *
 * Exercises the new self-bound [com.wingedsheep.sdk.dsl.Triggers.BecomesTargetByOpponent] facade.
 */
class CactarantulaTriggerScenarioTest : ScenarioTestBase() {

    init {
        context("Cactarantula targeting trigger") {

            test("opponent targeting it with a spell lets you may-draw a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    // Player 2 controls Cactarantula; Player 1 (active) targets it.
                    .withCardOnBattlefield(2, "Cactarantula", tapped = false, summoningSickness = false)
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .build()

                val cactarantula = game.findPermanent("Cactarantula")!!
                val libBefore = game.librarySize(2)

                val cast = game.castSpell(1, "Doom Blade", targetId = cactarantula)
                withClue("Doom Blade should cast targeting Cactarantula: ${cast.error}") {
                    cast.error shouldBe null
                }

                // The targeting trigger goes on the stack above Doom Blade and resolves first.
                game.resolveStack()

                // Cactarantula's controller (Player 2) may draw — accept.
                if (game.hasPendingDecision()) {
                    game.answerYesNo(true)
                }
                game.resolveStack()

                withClue("Player 2 drew a card from the may-draw trigger") {
                    game.librarySize(2) shouldBe (libBefore - 1)
                }
            }

            test("your own spell targeting it does not trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cactarantula", tapped = false, summoningSickness = false)
                    .withCardInHand(1, "Giant Growth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .build()

                val cactarantula = game.findPermanent("Cactarantula")!!
                val libBefore = game.librarySize(1)

                val cast = game.castSpell(1, "Giant Growth", targetId = cactarantula)
                withClue("Giant Growth should cast: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("no may-draw decision should be pending (own spell)") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("library unchanged - the trigger did not fire") {
                    game.librarySize(1) shouldBe libBefore
                }
            }
        }
    }
}
