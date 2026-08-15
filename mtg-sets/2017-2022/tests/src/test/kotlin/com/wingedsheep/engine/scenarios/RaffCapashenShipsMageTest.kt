package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.*
import com.wingedsheep.sdk.model.*
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Raff Capashen, Ship's Mage
 * {2}{W}{U}
 * Legendary Creature — Human Wizard
 * 3/3
 * Flash, Flying
 * You may cast historic spells as though they had flash.
 * (Artifacts, legendaries, and Sagas are historic.)
 *
 * Key difference from Quick Sliver: controllerOnly = true,
 * so only the controller of Raff benefits from the flash-granting ability.
 */
class RaffCapashenShipsMageTest : FunSpec({

    // A permanent with controllerOnly GrantFlashToSpellType for historic spells
    val RaffProxy = CardDefinition.creature(
        name = "Raff Proxy",
        manaCost = ManaCost.parse("{2}{W}{U}"),
        subtypes = setOf(Subtype("Human"), Subtype("Wizard")),
        power = 3,
        toughness = 3,
        keywords = setOf(Keyword.FLASH, Keyword.FLYING),
        script = CardScript(
            staticAbilities = listOf(
                GrantFlashToSpellType(
                    filter = GameObjectFilter.Historic,
                    controllerOnly = true
                )
            )
        )
    )

    // A legendary creature (historic) without flash
    val LegendaryBear = CardDefinition.creature(
        name = "Legendary Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
        supertypes = setOf(Supertype.LEGENDARY)
    )

    // An artifact creature (historic) without flash
    val TestArtifact = CardDefinition(
        name = "Test Artifact",
        manaCost = ManaCost.parse("{2}"),
        typeLine = TypeLine.parse("Artifact Creature — Golem"),
        creatureStats = CreatureStats(2, 2)
    )

    // A regular non-historic creature
    val PlainBear = CardDefinition.creature(
        name = "Plain Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RaffProxy, LegendaryBear, TestArtifact, PlainBear))
        return driver
    }

    test("controller can cast legendary creature at instant speed with Raff on battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Plains" to 10, "Island" to 10),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Raff Proxy")

        val legendary = driver.putCardInHand(activePlayer, "Legendary Bear")
        driver.passPriorityUntil(Step.END)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5)
        driver.giveMana(activePlayer, Color.GREEN, 2)

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = legendary,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe true
    }

    test("controller can cast artifact at instant speed with Raff on battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Plains" to 10, "Island" to 10),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Raff Proxy")

        val artifact = driver.putCardInHand(activePlayer, "Test Artifact")
        driver.passPriorityUntil(Step.END)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5)
        driver.giveColorlessMana(activePlayer, 2)

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = artifact,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe true
    }

    test("controller cannot cast non-historic creature at instant speed with Raff on battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Raff Proxy")

        val bear = driver.putCardInHand(activePlayer, "Plain Bear")
        driver.passPriorityUntil(Step.END)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5); the cast must
        // still fail because a non-historic creature isn't granted flash, not for lack of mana
        driver.giveMana(activePlayer, Color.GREEN, 2)

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = bear,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe false
    }

    test("opponent cannot cast legendary creature at instant speed with Raff on opponent's battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Plains" to 10, "Island" to 10),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Raff is on active player's battlefield
        driver.putPermanentOnBattlefield(activePlayer, "Raff Proxy")

        // Opponent gets a legendary creature
        val legendary = driver.putCardInHand(opponent, "Legendary Bear")

        driver.passPriorityUntil(Step.END)
        driver.passPriority(activePlayer)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5); the cast must
        // still fail because the flash grant is controller-only, not for lack of mana
        driver.giveMana(opponent, Color.GREEN, 2)

        val result = driver.submit(
            CastSpell(
                playerId = opponent,
                cardId = legendary,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe false
    }
})
