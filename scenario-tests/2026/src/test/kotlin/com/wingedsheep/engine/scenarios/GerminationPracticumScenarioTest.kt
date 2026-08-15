package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.ParadigmComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.GerminationPracticum
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Germination Practicum (SOS) — {3}{G}{G} Sorcery — Lesson.
 *
 * "Put two +1/+1 counters on each creature you control. Paradigm (...)"
 *
 * Pins: two +1/+1 counters land on every creature the caster controls (and only theirs); the
 * resolved spell self-exiles with the Paradigm marker.
 */
class GerminationPracticumScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + GerminationPracticum)
        return driver
    }

    fun plus(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("two +1/+1 counters on each of your creatures, none on opponent's") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val mine1 = driver.putCreatureOnBattlefield(player, "Savannah Lions")
        val mine2 = driver.putCreatureOnBattlefield(player, "Black Creature")
        val theirs = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")

        val spell = driver.putCardInHand(player, "Germination Practicum")
        driver.giveColorlessMana(player, 3)
        driver.giveMana(player, Color.GREEN, 2)
        driver.castSpell(player, spell).isSuccess shouldBe true
        driver.bothPass()

        plus(driver, mine1) shouldBe 2
        plus(driver, mine2) shouldBe 2
        plus(driver, theirs) shouldBe 0

        // Paradigm: resolved spell self-exiles with the marker.
        val exiled = driver.state.getZone(player, Zone.EXILE)
            .mapNotNull { driver.state.getEntity(it) }
            .filter { it.get<CardComponent>()?.name == "Germination Practicum" }
        exiled.any { it.get<ParadigmComponent>() != null } shouldBe true
    }
})
