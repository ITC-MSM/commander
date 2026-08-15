package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.PromptoArgentum
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Prompto Argentum (FIN #148) — {1}{R} Legendary Creature — Human Scout 2/2
 *   Haste
 *   Selfie Shot — Whenever you cast a noncreature spell, if at least four mana was spent to cast it,
 *   create a Treasure token.
 *
 * Mirrors Sahagin's trigger shape: a 4-mana noncreature spell creates a Treasure; a 1-mana one does
 * not (the intervening-if reads the mana actually spent).
 */
class PromptoArgentumScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all +
                com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens +
                listOf(PromptoArgentum)
        )
        return driver
    }

    fun treasureCount(driver: GameTestDriver, owner: com.wingedsheep.sdk.model.EntityId): Int =
        driver.state.getBattlefield().count { id ->
            val card = driver.state.getEntity(id)?.get<CardComponent>()
            card?.name == "Treasure" &&
                driver.state.getEntity(id)
                    ?.get<com.wingedsheep.engine.state.components.identity.ControllerComponent>()
                    ?.playerId == owner
        }

    test("casting a 4-mana noncreature spell creates a Treasure token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Prompto Argentum")
        treasureCount(driver, active) shouldBe 0

        // Stoke the Flames ({2}{R}{R}) — a 4-mana instant — paid in full.
        val stoke = driver.putCardInHand(active, "Stoke the Flames")
        driver.giveMana(active, Color.RED, 2)
        driver.giveColorlessMana(active, 2)
        driver.castSpellWithTargets(
            active,
            stoke,
            listOf(entityIdToChosenTarget(driver.state, opponent)),
        ).isSuccess shouldBe true
        // Resolve Prompto's trigger (on top of the stack), then the spell.
        driver.bothPass()
        driver.bothPass()

        treasureCount(driver, active) shouldBe 1
    }

    test("casting a sub-4-mana noncreature spell creates no Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Prompto Argentum")

        // Lightning Bolt ({R}) — a 1-mana noncreature spell, below the four-mana threshold.
        val bolt = driver.putCardInHand(active, "Lightning Bolt")
        driver.giveMana(active, Color.RED, 1)
        driver.castSpellWithTargets(
            active,
            bolt,
            listOf(entityIdToChosenTarget(driver.state, opponent)),
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        treasureCount(driver, active) shouldBe 0
    }
})
