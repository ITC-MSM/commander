package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.cards.PiratePeddlers
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pirate Peddlers: "Whenever you sacrifice another permanent, put a +1/+1 counter on this creature."
 *
 * This is a *per-permanent* trigger (CR 603.2c): sacrificing several permanents simultaneously fires
 * it once per permanent, so it should add one counter each — not a single counter for the whole
 * batch. Uses `Triggers.YouSacrificeAnother` (OTHER binding), which also excludes Pirate Peddlers
 * sacrificing itself.
 */
class PiratePeddlersTest : FunSpec({

    // Sorcery that sacrifices both of your Cats at once (one event, two permanents).
    val cullTheCats = card("Cull the Cats") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        oracleText = "Sacrifice two Cats."
        spell {
            effect = SacrificeEffect(GameObjectFilter.Creature.withSubtype("Cat"), count = 2)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PiratePeddlers, cullTheCats))
        return driver
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    // Drain the whole stack — the sorcery resolves into two per-permanent sacrifice triggers.
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            bothPass()
            guard++
        }
    }

    test("sacrificing two permanents at once adds two counters (one per permanent)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val peddlers = driver.putCreatureOnBattlefield(player, "Pirate Peddlers")
        // Two Cats to feed the sacrifice; the filter excludes Pirate Peddlers (a Pirate, not a Cat).
        driver.putCreatureOnBattlefield(player, "Savannah Lions")
        driver.putCreatureOnBattlefield(player, "Savannah Lions")
        val spell = driver.putCardInHand(player, "Cull the Cats")
        driver.giveMana(player, Color.BLACK, 1)

        driver.castSpell(player, spell)
        driver.resolveStack()   // sorcery sacrifices both Cats; two per-permanent triggers resolve

        plusCounters(driver, peddlers) shouldBe 2
    }
})
