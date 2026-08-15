package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.DreadwingScavenger
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dreadwing Scavenger {1}{U}{B} — Creature — Nightmare Bird 2/2
 *   Flying
 *   Whenever this creature enters or attacks, draw a card, then discard a card.
 *   Threshold — This creature gets +1/+1 and has deathtouch as long as there are seven or more
 *   cards in your graveyard.
 *
 * Proves the threshold continuous buff toggles with the graveyard count — a stat boost and a
 * keyword grant that both switch on at seven cards and off below it.
 */
class DreadwingScavengerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DreadwingScavenger))
        return driver
    }

    fun power(driver: GameTestDriver, id: EntityId) = StateProjector().getProjectedPower(driver.state, id)
    fun toughness(driver: GameTestDriver, id: EntityId) = StateProjector().getProjectedToughness(driver.state, id)
    fun keywords(driver: GameTestDriver, id: EntityId) = StateProjector().getProjectedKeywords(driver.state, id)

    test("below threshold: 2/2 flyer without deathtouch") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Island" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val bird = driver.putCreatureOnBattlefield(you, "Dreadwing Scavenger")
        repeat(6) { driver.putCardInGraveyard(you, "Swamp") }

        power(driver, bird) shouldBe 2
        toughness(driver, bird) shouldBe 2
        keywords(driver, bird).contains(Keyword.FLYING) shouldBe true
        keywords(driver, bird).contains(Keyword.DEATHTOUCH) shouldBe false
    }

    test("at threshold: 3/3 with deathtouch") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Island" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val bird = driver.putCreatureOnBattlefield(you, "Dreadwing Scavenger")
        repeat(7) { driver.putCardInGraveyard(you, "Swamp") }

        power(driver, bird) shouldBe 3
        toughness(driver, bird) shouldBe 3
        keywords(driver, bird).contains(Keyword.FLYING) shouldBe true
        keywords(driver, bird).contains(Keyword.DEATHTOUCH) shouldBe true
    }
})
