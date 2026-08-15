package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.GilgameshMasterAtArms
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Gilgamesh, Master-at-Arms (FIN #139).
 *
 * Gilgamesh, Master-at-Arms {4}{R}{R} Legendary Creature — Human Samurai 6/6
 * Whenever Gilgamesh enters or attacks, look at the top six cards of your library. You may put
 * any number of Equipment cards from among them onto the battlefield. Put the rest on the
 * bottom of your library in a random order. When you put one or more Equipment onto the
 * battlefield this way, you may attach one of them to a Samurai you control.
 *
 * Covers both halves of "enters or attacks" (two sibling triggered abilities), the filtered
 * any-number selection (only Equipment among the six is offered as selectable), the
 * "When you put... you may attach" sub-pipeline gated on at least one Equipment actually
 * entering, and both "may" declines (no Equipment chosen at all; Equipment chosen but the
 * optional attach declined).
 */
class GilgameshMasterAtArmsScenarioTest : FunSpec({

    val stateProjector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + GilgameshMasterAtArms)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("ETB: chooses the lone Equipment among six, rest to bottom, then attaches it to Gilgamesh") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        // Library top six (last push = top): five Plains + one Equipment on top.
        val deep1 = driver.putCardOnTopOfLibrary(you, "Plains")
        val deep2 = driver.putCardOnTopOfLibrary(you, "Plains")
        val deep3 = driver.putCardOnTopOfLibrary(you, "Plains")
        val deep4 = driver.putCardOnTopOfLibrary(you, "Plains")
        val deep5 = driver.putCardOnTopOfLibrary(you, "Plains")
        val sword = driver.putCardOnTopOfLibrary(you, "Buster Sword")

        val gilgameshCard = driver.putCardInHand(you, "Gilgamesh, Master-at-Arms")
        driver.giveMana(you, Color.RED, 2)
        driver.giveColorlessMana(you, 4)

        driver.castSpell(you, gilgameshCard)
        driver.bothPass() // resolve Gilgamesh onto the battlefield, ETB trigger goes on the stack
        driver.bothPass() // begin resolving the ETB trigger

        val gilgamesh = driver.findPermanent(you, "Gilgamesh, Master-at-Arms")!!

        // Pauses to choose any number of the six — only Buster Sword is selectable (Equipment filter).
        driver.isPaused shouldBe true
        val selectDecision = driver.pendingDecision as SelectCardsDecision
        selectDecision.options shouldBe listOf(sword)
        driver.submitCardSelection(you, listOf(sword))

        // Buster Sword went onto the battlefield → the "When you put one or more..." sub-pipeline
        // pauses to choose which Equipment (the lone candidate) to attach.
        driver.isPaused shouldBe true
        val equipmentChoice = driver.pendingDecision as SelectCardsDecision
        equipmentChoice.options shouldBe listOf(sword)
        driver.submitCardSelection(you, listOf(sword))

        // Then which Samurai you control to attach it to — only Gilgamesh qualifies.
        driver.isPaused shouldBe true
        val samuraiChoice = driver.pendingDecision as SelectCardsDecision
        samuraiChoice.options shouldBe listOf(gilgamesh)
        driver.submitCardSelection(you, listOf(gilgamesh))

        driver.isPaused shouldBe false

        driver.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe gilgamesh
        // 6/6 base + Buster Sword's +3/+2 = 9/8.
        stateProjector.project(driver.state).getPower(gilgamesh) shouldBe 9
        stateProjector.project(driver.state).getToughness(gilgamesh) shouldBe 8

        // The five Plains are still in the library (bottomed, not lost).
        val library = driver.state.getZone(ZoneKey(you, Zone.LIBRARY))
        listOf(deep1, deep2, deep3, deep4, deep5).forEach { library shouldContain it }
    }

    test("ETB: declining the any-number selection puts nothing onto the battlefield, no further decision") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        val filler = (1..5).map { driver.putCardOnTopOfLibrary(you, "Plains") }
        val sword = driver.putCardOnTopOfLibrary(you, "Buster Sword")

        val gilgameshCard = driver.putCardInHand(you, "Gilgamesh, Master-at-Arms")
        driver.giveMana(you, Color.RED, 2)
        driver.giveColorlessMana(you, 4)

        driver.castSpell(you, gilgameshCard)
        driver.bothPass()
        driver.bothPass()

        driver.isPaused shouldBe true
        driver.submitCardSelection(you, emptyList())

        // Nothing was put onto the battlefield this way → no "you may attach" sub-pipeline runs.
        driver.isPaused shouldBe false
        driver.findPermanent(you, "Buster Sword") shouldBe null

        val library = driver.state.getZone(ZoneKey(you, Zone.LIBRARY))
        library shouldContain sword
        filler.forEach { library shouldContain it }
    }

    test("ETB: Equipment enters the battlefield even when the optional attach is declined") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        repeat(5) { driver.putCardOnTopOfLibrary(you, "Plains") }
        val sword = driver.putCardOnTopOfLibrary(you, "Buster Sword")

        val gilgameshCard = driver.putCardInHand(you, "Gilgamesh, Master-at-Arms")
        driver.giveMana(you, Color.RED, 2)
        driver.giveColorlessMana(you, 4)

        driver.castSpell(you, gilgameshCard)
        driver.bothPass()
        driver.bothPass()

        val gilgamesh = driver.findPermanent(you, "Gilgamesh, Master-at-Arms")!!

        driver.isPaused shouldBe true
        driver.submitCardSelection(you, listOf(sword))

        // Decline "one of them" — no Equipment chosen to attach.
        driver.isPaused shouldBe true
        driver.submitCardSelection(you, emptyList())

        // Still asked which Samurai (even though the attach will no-op without an Equipment).
        driver.isPaused shouldBe true
        driver.submitCardSelection(you, listOf(gilgamesh))

        driver.isPaused shouldBe false

        driver.findPermanent(you, "Buster Sword") shouldNotBe null
        driver.state.getEntity(sword)?.get<AttachedToComponent>() shouldBe null
        stateProjector.project(driver.state).getPower(gilgamesh) shouldBe 6
        stateProjector.project(driver.state).getToughness(gilgamesh) shouldBe 6
    }

    test("attacks: the same look-at-top-six pipeline runs and attaches the chosen Equipment") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        // Direct-to-battlefield helper bypasses ETB triggers, so only the attack half is exercised.
        val gilgamesh = driver.putCreatureOnBattlefield(you, "Gilgamesh, Master-at-Arms")
        driver.removeSummoningSickness(gilgamesh)

        repeat(5) { driver.putCardOnTopOfLibrary(you, "Plains") }
        val sword = driver.putCardOnTopOfLibrary(you, "Buster Sword")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(gilgamesh), opponent)
        driver.bothPass() // begin resolving the attack trigger

        driver.isPaused shouldBe true
        val selectDecision = driver.pendingDecision as SelectCardsDecision
        selectDecision.options shouldBe listOf(sword)
        driver.submitCardSelection(you, listOf(sword))

        driver.isPaused shouldBe true
        driver.submitCardSelection(you, listOf(sword))

        driver.isPaused shouldBe true
        driver.submitCardSelection(you, listOf(gilgamesh))

        driver.isPaused shouldBe false

        driver.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe gilgamesh
    }
})
