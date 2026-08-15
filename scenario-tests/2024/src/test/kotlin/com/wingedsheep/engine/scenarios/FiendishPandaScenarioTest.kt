package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.FiendishPanda
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Fiendish Panda (FDN) — {2}{W}{B} Creature — Bear Demon, 3/2.
 *
 * "Whenever you gain life, put a +1/+1 counter on this creature.
 *  When this creature dies, return another target non-Bear creature card with mana value less
 *  than or equal to this creature's power from your graveyard to the battlefield."
 */
class FiendishPandaScenarioTest : FunSpec({

    // A {W} instant that gains 3 life for its controller, to fire the life-gain trigger.
    val TestHealer = CardDefinition.instant(
        name = "Test Healer",
        manaCost = ManaCost.parse("{W}"),
        oracleText = "You gain 3 life.",
        script = CardScript.spell(effect = Effects.GainLife(3))
    )

    // A cheap Bear (mana value within the Panda's power) — excluded by the "non-Bear" clause.
    val TestBear = CardDefinition.creature(
        name = "Test Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(FiendishPanda)
        driver.registerCard(TestHealer)
        driver.registerCard(TestBear)
        return driver
    }

    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("gaining life puts a +1/+1 counter on Fiendish Panda") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val panda = driver.putCreatureOnBattlefield(controller, "Fiendish Panda")
        plusOneCounters(driver, panda) shouldBe 0

        val healer = driver.putCardInHand(controller, "Test Healer")
        driver.giveMana(controller, Color.WHITE, 1)
        driver.castSpell(controller, healer)
        driver.bothPass() // resolve Test Healer → controller gains 3 life
        if (driver.stackSize > 0) driver.bothPass() // resolve the life-gain trigger

        plusOneCounters(driver, panda) shouldBe 1
    }

    test("when it dies, it reanimates a valid non-Bear creature within its power from your graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30))
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val panda = driver.putCreatureOnBattlefield(controller, "Fiendish Panda")
        // Centaur Courser: {2}{G} → mana value 3, not a Bear → within the 3-power Panda's reach.
        driver.putCardInGraveyard(controller, "Centaur Courser")

        // Kill the Panda with a bolt (3 damage to a 3/2).
        val bolt = driver.putCardInHand(controller, "Lightning Bolt")
        driver.giveMana(controller, Color.RED, 1)
        driver.castSpell(controller, bolt, listOf(panda)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt → Panda dies, dies trigger wants a target

        // The dies trigger asks for its reanimation target; choose the Centaur Courser.
        val courser = driver.getGraveyard(controller).first {
            driver.state.getEntity(it)
                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Centaur Courser"
        }
        driver.pendingDecision.shouldNotBeNull()
        driver.submitTargetSelection(controller, listOf(courser))

        // Resolve the triggered ability now that its target is locked in.
        while (driver.stackSize > 0) driver.bothPass()

        driver.findPermanent(controller, "Centaur Courser").shouldNotBeNull()
    }

    test("the dies trigger finds no target when only a Bear or an over-costed creature is available") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30))
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val panda = driver.putCreatureOnBattlefield(controller, "Fiendish Panda")
        // Bear (excluded by non-Bear) and Force of Nature ({3}{G}{G} → MV 5 > power 3, excluded by cap).
        driver.putCardInGraveyard(controller, "Test Bear")
        driver.putCardInGraveyard(controller, "Force of Nature")

        val bolt = driver.putCardInHand(controller, "Lightning Bolt")
        driver.giveMana(controller, Color.RED, 1)
        driver.castSpell(controller, bolt, listOf(panda)).isSuccess shouldBe true
        driver.bothPass() // resolve bolt → Panda dies; no legal target so nothing reanimates
        if (driver.stackSize > 0) driver.bothPass()

        // Neither fodder creature was returned; both remain in the graveyard.
        driver.pendingDecision shouldBe null
        driver.findPermanent(controller, "Test Bear") shouldBe null
        driver.findPermanent(controller, "Force of Nature") shouldBe null
        driver.getGraveyardCardNames(controller) shouldContain "Test Bear"
        driver.getGraveyardCardNames(controller) shouldContain "Force of Nature"
    }
})
