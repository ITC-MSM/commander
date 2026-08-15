package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.CloudMidgarMercenary
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Cloud, Midgar Mercenary:
 *  - ETB tutors an Equipment card into hand,
 *  - while equipped, an attached Equipment's triggered abilities trigger an additional time,
 *  - the gate ("as long as Cloud is equipped") turns the doubling off when nothing is attached,
 *  - the `alsoSource` leg of [AdditionalSourceTriggers] doubles a permanent's own triggers.
 */
class CloudMidgarMercenaryScenarioTest : FunSpec({

    // A minimal Equipment that gains its controller 1 life each upkeep — an observable trigger
    // whose source is the Equipment (so Cloud's doubling applies when it's attached to Cloud).
    val testAegis = card("Test Aegis") {
        manaCost = "{2}"
        typeLine = "Artifact — Equipment"
        oracleText = "At the beginning of your upkeep, you gain 1 life.\nEquip {2}"
        triggeredAbility {
            trigger = Triggers.YourUpkeep
            effect = Effects.GainLife(1)
        }
        equipAbility("{2}")
    }

    // A creature that doubles its OWN triggered abilities (alsoSource) — no filter match needed.
    val selfDoubler = card("Self Doubler") {
        manaCost = "{2}"
        typeLine = "Creature — Elemental"
        power = 1
        toughness = 1
        oracleText = "At the beginning of your upkeep, you gain 1 life.\n" +
            "If a triggered ability of this creature triggers, it triggers an additional time."
        triggeredAbility {
            trigger = Triggers.YourUpkeep
            effect = Effects.GainLife(1)
        }
        staticAbility {
            // sourceFilter matches nothing here; only the alsoSource leg contributes.
            ability = AdditionalSourceTriggers(
                sourceFilter = GameObjectFilter.Artifact.withSubtype("Equipment"),
                excludeSelf = true,
                alsoSource = true
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CloudMidgarMercenary, testAegis, selfDoubler))
        return driver
    }

    /** Advance to the active player's *next* precombat main (through their untap + upkeep). */
    fun advanceToNextTurnMain(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.END, maxPasses = 300)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)
    }

    fun GameTestDriver.attach(equipment: EntityId, creature: EntityId) {
        replaceState(
            state
                .updateEntity(equipment) { it.with(AttachedToComponent(creature)) }
                .updateEntity(creature) { it.with(AttachmentsComponent(listOf(equipment))) }
        )
    }

    test("ETB searches the library for an Equipment card and puts it into hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30, "Forest" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val aegis = driver.putCardOnTopOfLibrary(p1, "Test Aegis")
        driver.giveMana(p1, Color.WHITE, 2)
        val cloud = driver.putCardInHand(p1, "Cloud, Midgar Mercenary")
        driver.castSpell(p1, cloud)
        // Resolve Cloud, then its ETB trigger, until the library-search decision pauses the game.
        var guard = 0
        while (driver.pendingDecision !is SelectCardsDecision && guard++ < 10) {
            driver.bothPass()
        }

        driver.pendingDecision as SelectCardsDecision
        driver.submitCardSelection(p1, listOf(aegis))

        driver.findCardInHand(p1, "Test Aegis").shouldNotBeNull()
    }

    test("an Equipment attached to Cloud has its triggered ability doubled") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30, "Forest" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val cloud = driver.putCreatureOnBattlefield(p1, "Cloud, Midgar Mercenary")
        val aegis = driver.putPermanentOnBattlefield(p1, "Test Aegis")
        driver.attach(aegis, cloud)

        val before = driver.getLifeTotal(p1)
        advanceToNextTurnMain(driver) // opponent's turn
        advanceToNextTurnMain(driver) // back to p1 — through p1's upkeep once

        // The Equipment's "gain 1 life" upkeep trigger fired an additional time: +2.
        driver.getLifeTotal(p1) shouldBe before + 2
    }

    test("the doubling is off when Cloud is not equipped (gate)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30, "Forest" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cloud is on the battlefield but unequipped; the Equipment hangs off a vanilla creature.
        driver.putCreatureOnBattlefield(p1, "Cloud, Midgar Mercenary")
        val bear = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        val aegis = driver.putPermanentOnBattlefield(p1, "Test Aegis")
        driver.attach(aegis, bear)

        val before = driver.getLifeTotal(p1)
        advanceToNextTurnMain(driver)
        advanceToNextTurnMain(driver)

        // Not attached to Cloud (and Cloud isn't equipped): the trigger fires once, +1.
        driver.getLifeTotal(p1) shouldBe before + 1
    }

    test("alsoSource doubles a permanent's own triggered ability") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30, "Forest" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(p1, "Self Doubler")

        val before = driver.getLifeTotal(p1)
        advanceToNextTurnMain(driver)
        advanceToNextTurnMain(driver)

        driver.getLifeTotal(p1) shouldBe before + 2
    }
})
