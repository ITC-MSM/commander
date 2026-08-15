package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.ValkyriesCall
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Valkyrie's Call (FDN) — {3}{W}{W} Enchantment.
 *
 * Whenever a nontoken, non-Angel creature you control dies, return that card to the
 * battlefield under its owner's control with a +1/+1 counter on it. It has flying and is
 * an Angel in addition to its other types.
 */
class ValkyriesCallScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + ValkyriesCall)
        return driver
    }

    test("a nontoken creature you control that dies returns as a 4/4 flying Angel with a +1/+1 counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Valkyrie's Call")
        driver.putCreatureOnBattlefield(player, "Centaur Courser") // 3/3 nontoken, not an Angel

        // Kill your own Centaur Courser with Lightning Bolt.
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        val courser = driver.findPermanent(player, "Centaur Courser")!!
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, listOf(courser))
        driver.bothPass() // resolve Bolt → Courser dies → Valkyrie's Call trigger goes on the stack
        driver.bothPass() // resolve the trigger → Courser returns transformed

        // No longer in the graveyard...
        driver.getGraveyard(player).mapNotNull { driver.getCardName(it) } shouldNotContain "Centaur Courser"

        // ...back on the battlefield under its owner's control, transformed.
        val returned = driver.findPermanent(player, "Centaur Courser")!!
        val projected = projector.project(driver.state)
        projected.getPower(returned) shouldBe 4      // 3 + the +1/+1 counter
        projected.getToughness(returned) shouldBe 4
        projected.hasKeyword(returned, Keyword.FLYING) shouldBe true
        projected.hasSubtype(returned, "Angel") shouldBe true
        // The original Centaur subtype is retained ("in addition to its other types").
        projected.hasSubtype(returned, "Centaur") shouldBe true

        driver.state.getEntity(returned)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("an opponent's creature dying does not trigger Valkyrie's Call") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Valkyrie's Call")
        driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        val courser = driver.findPermanent(opponent, "Centaur Courser")!!
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, listOf(courser))
        driver.bothPass()
        driver.bothPass()

        // "creature you control" — the opponent's creature stays dead.
        driver.getGraveyard(opponent).mapNotNull { driver.getCardName(it) } shouldContain "Centaur Courser"
        driver.findPermanent(opponent, "Centaur Courser") shouldBe null
        driver.findPermanent(player, "Centaur Courser") shouldBe null
    }
})
