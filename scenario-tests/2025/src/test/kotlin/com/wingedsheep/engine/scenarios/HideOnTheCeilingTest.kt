package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

/**
 * Tests for Hide on the Ceiling.
 *
 * {X}{U} Instant — Exile X target artifacts and/or creatures. Return the
 * exiled cards to the battlefield under their owners' control at the
 * beginning of the next end step.
 */
class HideOnTheCeilingTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Island" to 20,
                "Hide on the Ceiling" to 5
            ),
            skipMulligans = true
        )
        return driver
    }

    fun GameTestDriver.advanceToPlayer1PrecombatMain() {
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.PRECOMBAT_MAIN)
            safety++
        }
    }

    test("X=2 exiles two opponent permanents and they return at the next end step under their owner's control") {
        val driver = createDriver()

        // Two permanents on player2's battlefield: one creature, one artifact creature.
        val creature = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val artifact = driver.putCreatureOnBattlefield(driver.player2, "Artifact Creature")

        // Mana for X=2 ({2}{U} total).
        repeat(5) { driver.putLandOnBattlefield(driver.player1, "Island") }

        driver.advanceToPlayer1PrecombatMain()

        val spell = driver.findCardInHand(driver.player1, "Hide on the Ceiling")
            ?: driver.putCardInHand(driver.player1, "Hide on the Ceiling")
        val cast = driver.castXSpell(
            playerId = driver.player1,
            cardId = spell,
            xValue = 2,
            targets = listOf(creature, artifact)
        )
        cast.isSuccess shouldBe true

        // Resolve the spell.
        driver.bothPass()

        // Both targets are now in their owner's (player2's) exile zone, not on
        // the battlefield.
        val exileAfterCast = driver.state.getZone(ZoneKey(driver.player2, Zone.EXILE))
        exileAfterCast shouldContainAll listOf(creature, artifact)
        driver.getPermanents(driver.player2).contains(creature) shouldBe false
        driver.getPermanents(driver.player2).contains(artifact) shouldBe false

        // Advance to the end step; the delayed triggers fire and return them.
        // Two triggers go on the stack, so resolve them all by advancing into
        // the cleanup step (everything in END must resolve before then).
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.passPriorityUntil(Step.CLEANUP, maxPasses = 200)

        driver.getPermanents(driver.player2) shouldContainAll listOf(creature, artifact)
        val exileAfterReturn = driver.state.getZone(ZoneKey(driver.player2, Zone.EXILE))
        exileAfterReturn.contains(creature) shouldBe false
        exileAfterReturn.contains(artifact) shouldBe false

        // "Under their owners' control": player2 still owns and controls them.
        driver.getController(creature) shouldBe driver.player2
        driver.getController(artifact) shouldBe driver.player2
    }

    test("X=0 with no targets is a legal cast and exiles nothing") {
        val driver = createDriver()

        val bystander = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        repeat(3) { driver.putLandOnBattlefield(driver.player1, "Island") }

        driver.advanceToPlayer1PrecombatMain()

        val spell = driver.findCardInHand(driver.player1, "Hide on the Ceiling")
            ?: driver.putCardInHand(driver.player1, "Hide on the Ceiling")
        val cast = driver.castXSpell(
            playerId = driver.player1,
            cardId = spell,
            xValue = 0,
            targets = emptyList()
        )
        cast.isSuccess shouldBe true

        driver.bothPass()

        // The bystander is untouched.
        driver.getPermanents(driver.player2) shouldContainAll listOf(bystander)
    }
})
