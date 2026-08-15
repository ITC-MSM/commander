package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eoe.cards.RaybladeTrooper
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Rayblade Trooper — {2}{W} 2/2 Creature — Human Soldier
 *
 * "Whenever a nontoken creature you control with a +1/+1 counter on it dies, create a 1/1 white
 *  Human Soldier creature token."
 *
 * The death trigger gates on a `HasCounter(+1/+1)` state predicate. Because the creature has
 * already left the battlefield when the trigger gates, the predicate must be evaluated against the
 * dying creature's last-known counters — not the (now counter-less) live state. The bug being
 * guarded here: the `HasCounter` predicate failed open in the zone-change gating path, so a token
 * was created even when the creature had no +1/+1 counter.
 */
class RaybladeTrooperScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(RaybladeTrooper)
        return driver
    }

    test("does NOT create a token when a creature WITHOUT a +1/+1 counter dies") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Rayblade Trooper")
        val courser = driver.putCreatureOnBattlefield(player, "Centaur Courser")

        // Kill the counter-less creature with Doom Blade (destroys regardless of P/T).
        val doomBlade = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 2)
        driver.castSpell(player, doomBlade, targets = listOf(courser))
        driver.bothPass() // resolve Doom Blade -> Centaur Courser dies

        driver.findPermanent(player, "Centaur Courser") shouldBe null
        // The death trigger must NOT fire — no token on the stack and none created.
        driver.stackSize shouldBe 0
        driver.getCreatures(player).count { driver.getCardName(it) == "Human Soldier Token" } shouldBe 0
    }

    test("DOES create a token when a creature WITH a +1/+1 counter dies") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Rayblade Trooper")
        val courser = driver.putCreatureOnBattlefield(player, "Centaur Courser")
        driver.addComponent(courser, CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 1))

        val doomBlade = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 2)
        driver.castSpell(player, doomBlade, targets = listOf(courser))
        driver.bothPass() // resolve Doom Blade -> Centaur Courser dies -> death trigger on stack
        driver.bothPass() // resolve the death trigger -> token created

        driver.findPermanent(player, "Centaur Courser") shouldBe null
        driver.getCreatures(player).count { driver.getCardName(it) == "Human Soldier Token" } shouldBe 1
    }
})
