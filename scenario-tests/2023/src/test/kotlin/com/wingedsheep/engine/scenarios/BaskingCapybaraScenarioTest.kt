package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BaskingCapybara
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Basking Capybara (LCI #175): {1}{G} 1/3 Creature — Capybara
 * "Descend 4 — This creature gets +3/+0 as long as there are four or more permanent
 * cards in your graveyard."
 *
 * Tests:
 *  - With fewer than 4 permanent cards in the graveyard → base stats 1/3.
 *  - With exactly 4 permanent cards in the graveyard → boosted stats 4/3.
 */
class BaskingCapybaraScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BaskingCapybara))
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    test("base stats 1/3 when fewer than 4 permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val capybara = driver.putCreatureOnBattlefield(player, "Basking Capybara")

        // Put 3 permanent cards into the graveyard — one short of the Descend 4 threshold.
        repeat(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        driver.state.projectedState.getPower(capybara) shouldBe 1
        driver.state.projectedState.getToughness(capybara) shouldBe 3
    }

    test("gets +3/+0 (becomes 4/3) when four or more permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val capybara = driver.putCreatureOnBattlefield(player, "Basking Capybara")

        // Put exactly 4 permanent cards (creatures) into the graveyard — meets the threshold.
        repeat(4) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        driver.state.projectedState.getPower(capybara) shouldBe 4
        driver.state.projectedState.getToughness(capybara) shouldBe 3
    }
})
