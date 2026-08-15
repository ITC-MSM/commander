package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.SplatterTechnique
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Splatter Technique {1}{U}{U}{R}{R} Sorcery (SOS canonical).
 *
 * Choose one —
 * • Draw four cards.
 * • Splatter Technique deals 4 damage to each creature and planeswalker.
 */
class SplatterTechniqueScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SplatterTechnique))
        return driver
    }

    fun payManaFor(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) {
        driver.giveMana(player, Color.BLUE, 3) // {U}{U} + {1} generic
        driver.giveMana(player, Color.RED, 2)  // {R}{R}
    }

    test("mode 0 — draws four cards") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Mountain" to 20), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        payManaFor(driver, me)
        val spell = driver.putCardInHand(me, "Splatter Technique")
        val handWithSpell = driver.getHandSize(me)

        driver.submit(
            CastSpell(playerId = me, cardId = spell, chosenModes = listOf(0))
        ).isSuccess shouldBe true
        driver.bothPass()

        // -1 for casting the spell, +4 for the draw.
        driver.getHandSize(me) shouldBe (handWithSpell - 1 + 4)
    }

    test("mode 1 — deals 4 damage to each creature and planeswalker (both sides)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Mountain" to 20), startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 1/1 (dies to 4) on my side; 5/5 (survives 4) on opponent's side.
        driver.putCreatureOnBattlefield(me, "Savannah Lions")
        driver.putCreatureOnBattlefield(opp, "Force of Nature") // 5/5

        payManaFor(driver, me)
        val spell = driver.putCardInHand(me, "Splatter Technique")

        driver.submit(
            CastSpell(playerId = me, cardId = spell, chosenModes = listOf(1))
        ).isSuccess shouldBe true
        driver.bothPass()

        // 1/1 is dead; 5/5 survives 4 damage.
        driver.findPermanent(me, "Savannah Lions") shouldBe null
        driver.findPermanent(opp, "Force of Nature") shouldNotBe null
        // Players are untouched (creatures/planeswalkers only).
        driver.getLifeTotal(me) shouldBe 20
        driver.getLifeTotal(opp) shouldBe 20
    }
})
