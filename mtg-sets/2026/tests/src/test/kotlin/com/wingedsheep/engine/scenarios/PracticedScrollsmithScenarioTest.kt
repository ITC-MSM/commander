package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.PracticedScrollsmith
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Practiced Scrollsmith (SOS) — {R}{R/W}{W} Creature — Dwarf Cleric 3/2, First strike.
 *
 * "When this creature enters, exile target noncreature, nonland card from your graveyard.
 *  Until the end of your next turn, you may cast that card."
 *
 * Exercises the ETB graveyard target → exile → may-cast-from-exile flow. A creature card or a
 * land in the graveyard is not a legal target.
 */
class PracticedScrollsmithScenarioTest : FunSpec({

    // A noncreature, nonland card (instant) that IS a legal target.
    val testInstant = card("Scrollsmith Test Bolt") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Scrollsmith Test Bolt deals 3 damage to any target."
    }
    // A creature card — must NOT be a legal target.
    val testCreature = card("Scrollsmith Test Bear") {
        manaCost = "{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PracticedScrollsmith)
        driver.registerCard(testInstant)
        driver.registerCard(testCreature)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castActionsFor(playerId: EntityId, cardId: EntityId): List<CastSpell> =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, playerId)
            .mapNotNull { it.action as? CastSpell }
            .filter { it.cardId == cardId }

    test("ETB exiles a targeted noncreature nonland card and grants may-cast from exile") {
        val driver = newDriver()
        val me = driver.player1

        val targetCard = driver.putCardInGraveyard(me, "Scrollsmith Test Bolt")

        // Mana to pay {R}{R/W}{W}.
        repeat(2) { driver.putLandOnBattlefield(me, "Mountain") }
        driver.putLandOnBattlefield(me, "Plains")

        // Cast Practiced Scrollsmith from hand so its ETB trigger fires.
        val scrollsmith = driver.putCardInHand(me, "Practiced Scrollsmith")
        driver.castSpell(me, scrollsmith).isSuccess shouldBe true

        // Resolve the trigger; choose the instant in my graveyard when the targeting decision appears.
        run {
            repeat(20) {
                if (driver.state.mayPlayPermissions.any { targetCard in it.cardIds }) return@run
                when (driver.pendingDecision) {
                    is ChooseTargetsDecision -> driver.submitTargetSelection(me, listOf(targetCard))
                    null -> driver.bothPass()
                    else -> driver.autoResolveDecision()
                }
            }
        }

        // The card moved from graveyard to exile, and I gained permission to cast it.
        driver.getGraveyard(me).shouldNotContain(targetCard)
        driver.state.getZone(me, Zone.EXILE).shouldContain(targetCard)
        driver.state.mayPlayPermissions.any { targetCard in it.cardIds } shouldBe true

        // The exiled instant is now castable via the granted permission.
        driver.castActionsFor(me, targetCard).isNotEmpty().shouldBeTrue()
    }

    test("a creature card in the graveyard is not a legal target") {
        val driver = newDriver()
        val me = driver.player1

        val creatureInGrave = driver.putCardInGraveyard(me, "Scrollsmith Test Bear")

        repeat(2) { driver.putLandOnBattlefield(me, "Mountain") }
        driver.putLandOnBattlefield(me, "Plains")
        val scrollsmith = driver.putCardInHand(me, "Practiced Scrollsmith")
        driver.castSpell(me, scrollsmith).isSuccess shouldBe true

        // Drive to end step; with no legal target the ETB ability never asks for a target and
        // nothing is exiled.
        driver.passPriorityUntil(Step.END)

        driver.getGraveyard(me).shouldContain(creatureInGrave)
        driver.state.mayPlayPermissions.any { creatureInGrave in it.cardIds } shouldBe false
        driver.state.getEntity(creatureInGrave)?.get<CardComponent>()?.name shouldBe "Scrollsmith Test Bear"
    }
})
