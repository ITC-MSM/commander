package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.TolsimirMidnightsLight
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Tolsimir, Midnight's Light (MKM) — {2}{G}{W}{W} Legendary Creature — Elf Scout 3/2.
 *
 * Lifelink.
 * When Tolsimir enters, create Voja Fenstalker, a legendary 5/5 green and white Wolf creature
 * token with trample.
 * Whenever a Wolf you control attacks, if Tolsimir attacked this combat, target creature an
 * opponent controls blocks that Wolf this combat if able.
 *
 * The last ability is what `ForceBlockEffect.attacker` was added for: the requirement pins the
 * chosen blocker to the *triggering Wolf*, not to the ability's source. These tests prove exactly
 * that split — the blocker may not satisfy the requirement by blocking Tolsimir.
 */
class TolsimirMidnightsLightScenarioTest : FunSpec({

    val bear = CardDefinition.creature("Test Blocking Bear", ManaCost.parse("{1}{G}"), emptySet(), 2, 2)
    val ox = CardDefinition.creature("Test Second Ox", ManaCost.parse("{2}{W}"), emptySet(), 1, 4)

    // Stands in for Voja Fenstalker in the force-block tests: the ability keys off "a Wolf you
    // control", so a plain Wolf isolates the requirement from token creation. `putCreatureOnBattlefield`
    // bypasses enters triggers, so the token itself only appears in the cast test below.
    val fenwolf = CardDefinition.creature("Test Fenwolf", ManaCost.parse("{3}{G}"), setOf(Subtype.WOLF), 5, 5)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TolsimirMidnightsLight, bear, ox, fenwolf))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("entering creates Voja Fenstalker — a legendary 5/5 green and white Wolf with trample") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val card = driver.putCardInHand(me, "Tolsimir, Midnight's Light")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveMana(me, Color.WHITE, 2)
        driver.giveColorlessMana(me, 2)
        driver.castSpell(me, card).isSuccess shouldBe true
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()

        val voja = driver.findPermanent(me, "Voja Fenstalker")
        voja.shouldNotBeNull()
        val container = driver.state.getEntity(voja)!!
        container.has<TokenComponent>() shouldBe true

        val tokenCard = container.get<CardComponent>()!!
        tokenCard.typeLine.isLegendary shouldBe true
        tokenCard.typeLine.subtypes.contains(Subtype.WOLF) shouldBe true

        val projected = driver.state.projectedState
        projected.getPower(voja) shouldBe 5
        projected.getToughness(voja) shouldBe 5
        projected.hasKeyword(voja, Keyword.TRAMPLE) shouldBe true
        projected.getColors(voja) shouldBe setOf("GREEN", "WHITE")
    }

    test("the chosen blocker must block the triggering Wolf, not Tolsimir") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val tolsimir = driver.putCreatureOnBattlefield(me, "Tolsimir, Midnight's Light")
        val voja = driver.putCreatureOnBattlefield(me, "Test Fenwolf")
        driver.removeSummoningSickness(tolsimir)
        driver.removeSummoningSickness(voja)

        val blocker = driver.putCreatureOnBattlefield(opponent, "Test Blocking Bear")
        // A second creature that is under no requirement at all.
        val free = driver.putCreatureOnBattlefield(opponent, "Test Second Ox")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(tolsimir, voja), defendingPlayer = opponent).error shouldBe null

        // One trigger — Voja is the only attacking Wolf. Point it at the bear.
        var guard = 0
        while (driver.pendingDecision !is ChooseTargetsDecision && guard++ < 10) driver.bothPass()
        (driver.pendingDecision as ChooseTargetsDecision)
        driver.submitTargetSelection(me, listOf(blocker)).error shouldBe null
        guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        // Blocking nothing ignores the requirement.
        driver.declareBlockers(opponent, emptyMap()).isSuccess shouldBe false
        // Blocking *Tolsimir* does not satisfy it — the requirement names the Wolf.
        driver.declareBlockers(opponent, mapOf(blocker to listOf(tolsimir))).isSuccess shouldBe false
        // Blocking the Wolf does. The unpinned creature is free to do anything, including nothing.
        driver.declareBlockers(opponent, mapOf(blocker to listOf(voja))).error shouldBe null

        driver.state.getEntity(free).shouldNotBeNull()
    }

    test("no trigger when Tolsimir sits out the combat — the intervening if fails") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val tolsimir = driver.putCreatureOnBattlefield(me, "Tolsimir, Midnight's Light")
        val voja = driver.putCreatureOnBattlefield(me, "Test Fenwolf")
        driver.removeSummoningSickness(tolsimir)
        driver.removeSummoningSickness(voja)

        val blocker = driver.putCreatureOnBattlefield(opponent, "Test Blocking Bear")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        // Voja attacks alone; Tolsimir stays home.
        driver.declareAttackers(me, listOf(voja), defendingPlayer = opponent).error shouldBe null

        // CR 603.4: the condition is false when the trigger event happens, so nothing triggers —
        // no target prompt, and the bear is under no requirement.
        driver.pendingDecision shouldBe null
        driver.state.stack.size shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, emptyMap()).error shouldBe null
        driver.state.getEntity(blocker).shouldNotBeNull()
    }
})
