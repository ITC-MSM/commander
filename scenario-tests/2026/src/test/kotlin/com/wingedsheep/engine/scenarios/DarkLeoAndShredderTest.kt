package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.DarkLeoAndShredder
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dark Leo & Shredder (TMT #142) — "Whenever Dark Leo & Shredder deal combat damage to a player,
 * create a 1/1 black Ninja creature token. Then if you control five or more Ninjas, that player
 * loses half their life, rounded up."
 *
 * The freshly-created token is itself a Ninja and is counted toward the five, so the second clause
 * fires when the token brings the controller's Ninja count up to exactly five.
 */
class DarkLeoAndShredderTest : FunSpec({

    val testNinja = card("Test Ninja") {
        manaCost = "{B}"
        typeLine = "Creature — Ninja"
        power = 1
        toughness = 1
    }

    test("token created on combat damage counts toward five Ninjas and drains half the player's life") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DarkLeoAndShredder, testNinja))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val darkLeo = driver.putCreatureOnBattlefield(player, "Dark Leo & Shredder")
        driver.removeSummoningSickness(darkLeo)
        // Dark Leo + 3 other Ninjas = 4 Ninjas; the created token pushes the count to five.
        repeat(3) { driver.putCreatureOnBattlefield(player, "Test Ninja") }
        val permanentsBefore = driver.getPermanents(player).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(darkLeo), opponent)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // 20 - 1 (combat) = 19; then lose half of 19 rounded up (10) = 9.
        driver.assertLifeTotal(opponent, 9)
        // A 1/1 Ninja token was created.
        driver.getPermanents(player).size shouldBe permanentsBefore + 1
    }

    test("below five Ninjas the player only takes combat damage") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DarkLeoAndShredder, testNinja))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val darkLeo = driver.putCreatureOnBattlefield(player, "Dark Leo & Shredder")
        driver.removeSummoningSickness(darkLeo)
        // Dark Leo + 1 other Ninja = 2; even after the token (3) the count stays below five.
        driver.putCreatureOnBattlefield(player, "Test Ninja")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(darkLeo), opponent)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Only the 1 combat damage; no life-drain rider.
        driver.assertLifeTotal(opponent, 19)
    }
})
