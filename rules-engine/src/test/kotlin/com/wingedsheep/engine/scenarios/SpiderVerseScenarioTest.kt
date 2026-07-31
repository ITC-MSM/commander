package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Spider-Verse (SPM) — "The 'legend rule' doesn't apply to Spiders you control." Pins the new
 * `LegendRuleDoesNotApplyTo(filter)` static + its consult hook in `LegendRuleCheck`. The second
 * Spider is *cast* so the legend-rule state-based action genuinely fires as it resolves.
 */
class SpiderVerseScenarioTest : FunSpec({

    val legendarySpider = card("Test Legendary Spider") {
        manaCost = "{1}{G}"
        typeLine = "Legendary Creature — Spider"
        power = 1
        toughness = 1
    }

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(legendarySpider))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    test("with Spider-Verse out, casting a second same-named legendary Spider keeps both") {
        val (driver, you) = newGame()
        driver.putPermanentOnBattlefield(you, "Spider-Verse")
        val s1 = driver.putCreatureOnBattlefield(you, "Test Legendary Spider")

        driver.giveMana(you, Color.GREEN, 2)
        val s2card = driver.putCardInHand(you, "Test Legendary Spider")
        driver.castSpell(you, s2card)
        resolveStack(driver)

        driver.state.getBattlefield().contains(s1) shouldBe true
        driver.state.getBattlefield().count {
            driver.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Test Legendary Spider"
        } shouldBe 2
        (driver.pendingDecision == null) shouldBe true // no legend-rule choice — exempt
    }

    test("without Spider-Verse, casting a second same-named legendary Spider triggers the legend rule") {
        val (driver, you) = newGame()
        driver.putCreatureOnBattlefield(you, "Test Legendary Spider")

        driver.giveMana(you, Color.GREEN, 2)
        val s2card = driver.putCardInHand(you, "Test Legendary Spider")
        driver.castSpell(you, s2card)
        resolveStack(driver)

        // Legend rule applies: either it paused for a choice, or only one copy remains.
        val remaining = driver.state.getBattlefield().count {
            driver.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Test Legendary Spider"
        }
        (driver.pendingDecision != null || remaining <= 1) shouldBe true
    }
})
