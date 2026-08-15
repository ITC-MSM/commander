package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.scg.cards.ForgottenAncient
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Forgotten Ancient:
 * {3}{G} Creature — Elemental 0/3
 * Whenever a player casts a spell, you may put a +1/+1 counter on Forgotten Ancient.
 * At the beginning of your upkeep, you may move any number of +1/+1 counters from
 * Forgotten Ancient onto other creatures.
 */
class ForgottenAncientTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ForgottenAncient)
        return driver
    }

    fun getCounters(driver: GameTestDriver, entityId: EntityId): Int {
        return driver.state.getEntity(entityId)
            ?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    test("gets a counter when controller casts a spell") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ancient = driver.putCreatureOnBattlefield(activePlayer, "Forgotten Ancient")

        // Cast a spell — triggers "whenever a player casts a spell"
        val bolt = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.giveMana(activePlayer, Color.RED, 1)
        driver.castSpell(activePlayer, bolt, listOf(opponent))

        // Trigger goes on stack above the spell — resolve it
        driver.bothPass()

        // MayEffect yes/no decision
        driver.submitYesNo(activePlayer, true)

        getCounters(driver, ancient) shouldBe 1

        // Resolve the actual bolt
        driver.bothPass()
    }

    test("gets a counter when opponent casts a spell") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ancient = driver.putCreatureOnBattlefield(activePlayer, "Forgotten Ancient")

        // Pass to opponent so they can cast
        driver.passPriority(activePlayer)

        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.castSpell(opponent, bolt, listOf(activePlayer))

        // Trigger for Forgotten Ancient — resolve it first
        driver.bothPass()

        // MayEffect — choose yes
        driver.submitYesNo(activePlayer, true)

        getCounters(driver, ancient) shouldBe 1
    }

    test("may decline the counter") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ancient = driver.putCreatureOnBattlefield(activePlayer, "Forgotten Ancient")

        val bolt = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.giveMana(activePlayer, Color.RED, 1)
        driver.castSpell(activePlayer, bolt, listOf(opponent))

        // Trigger resolves
        driver.bothPass()

        // Choose no — decline the counter
        driver.submitYesNo(activePlayer, false)

        getCounters(driver, ancient) shouldBe 0
    }
})
