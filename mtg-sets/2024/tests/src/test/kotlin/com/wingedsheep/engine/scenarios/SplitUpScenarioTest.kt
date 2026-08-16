package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.SplitUp
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Split Up — {1}{W}{W} Sorcery (DSK).
 *
 * Choose one —
 * • Destroy all tapped creatures.
 * • Destroy all untapped creatures.
 *
 * The sweep is one effect: it gathers the matching creatures and destroys the collection, so they
 * leave the battlefield together. It used to iterate the group and destroy one at a time, which is
 * the same board in the easy cases and a different card whenever anything watches the order — a
 * dies-trigger would see the rest of the sweep still on the battlefield.
 */
class SplitUpScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SplitUp))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castSplitUp(caster: EntityId, mode: Int) {
        giveMana(caster, Color.WHITE, 3)
        val spell = putCardInHand(caster, "Split Up")
        submit(CastSpell(playerId = caster, cardId = spell, chosenModes = listOf(mode))).error shouldBe null
        bothPass()
    }

    test("mode 1 destroys every tapped creature and leaves the untapped ones") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val myTapped = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        val theirTapped = driver.putCreatureOnBattlefield(opp, "Savannah Lions")
        val untapped = driver.putCreatureOnBattlefield(opp, "Centaur Courser")
        driver.tapPermanent(myTapped)
        driver.tapPermanent(theirTapped)

        driver.castSplitUp(me, mode = 0)

        driver.findPermanent(me, "Centaur Courser") shouldBe null
        driver.findPermanent(opp, "Savannah Lions") shouldBe null
        driver.findPermanent(opp, "Centaur Courser") shouldBe untapped
    }

    test("mode 2 destroys every untapped creature and leaves the tapped ones") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val tapped = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.putCreatureOnBattlefield(opp, "Savannah Lions")
        driver.tapPermanent(tapped)

        driver.castSplitUp(me, mode = 1)

        driver.findPermanent(me, "Centaur Courser") shouldBe tapped
        driver.findPermanent(opp, "Savannah Lions") shouldBe null
    }

    // The property the iteration broke: the creatures the sweep matched are gathered before any of
    // them is destroyed, so a creature that arrives *during* the sweep is not part of it — and,
    // more importantly here, one leaving does not change what the rest of the sweep hits. Two
    // Death Trigger Test Creatures die to one Split Up and both dies-triggers fire.
    test("the whole sweep resolves as one destruction, and every dies-trigger fires") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(me, "Death Trigger Test Creature")
        driver.putCreatureOnBattlefield(opp, "Death Trigger Test Creature")

        driver.castSplitUp(me, mode = 1)

        var guard = 0
        while ((driver.stackSize > 0 || driver.isPaused) && guard++ < 30) {
            if (driver.isPaused) driver.autoResolveDecision() else driver.bothPass()
        }

        driver.findPermanent(me, "Death Trigger Test Creature") shouldBe null
        driver.findPermanent(opp, "Death Trigger Test Creature") shouldBe null
        // Each controller's own creature gained them 3 life as it died.
        driver.assertLifeTotal(me, 23)
        driver.assertLifeTotal(opp, 23)
    }
})
