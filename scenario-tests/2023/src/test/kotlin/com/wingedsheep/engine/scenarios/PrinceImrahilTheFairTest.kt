package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.Divination
import com.wingedsheep.mtg.sets.definitions.ltr.cards.PrinceImrahilTheFair
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Prince Imrahil the Fair (LTR #219)
 * {W}{U} Legendary Creature — Human Noble 2/2
 * Whenever you draw your second card each turn, create a 1/1 white Human Soldier creature token.
 */
class PrinceImrahilTheFairTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PrinceImrahilTheFair, Divination))
        return driver
    }

    fun GameTestDriver.countSoldierTokens(playerId: EntityId): Int {
        val projected = state.projectedState
        return state.getBattlefield().count { id ->
            projected.getController(id) == playerId &&
                projected.isCreature(id) &&
                projected.hasSubtype(id, "Soldier")
        }
    }

    test("creates a Soldier token when you draw your second card via Divination") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Prince Imrahil the Fair")
        val before = driver.countSoldierTokens(p1)

        val divination = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, divination)
        driver.bothPass()
        driver.bothPass()

        driver.countSoldierTokens(p1) shouldBe before + 1
    }

    test("does not create a token when only one card is drawn this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Prince Imrahil the Fair")
        val before = driver.countSoldierTokens(p1)

        // Turn-1 active player skipped the draw step; no other draws happen this turn.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.countSoldierTokens(p1) shouldBe before
    }

    test("does not trigger from an opponent's draws") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Prince Imrahil the Fair")
        val before = driver.countSoldierTokens(p1)

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val divination = driver.putCardInHand(p2, "Divination")
        driver.giveMana(p2, Color.BLUE, 1)
        driver.giveColorlessMana(p2, 2)
        driver.castSpell(p2, divination)
        driver.bothPass()
        driver.bothPass()

        driver.countSoldierTokens(p1) shouldBe before
    }
})
