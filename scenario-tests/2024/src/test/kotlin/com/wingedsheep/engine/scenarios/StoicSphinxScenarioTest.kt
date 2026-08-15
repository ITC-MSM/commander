package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.StoicSphinx
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stoic Sphinx (OTJ) — {2}{U}{U} Sphinx 5/3, Flash, Flying.
 *
 * "This creature has hexproof as long as you haven't cast a spell this turn."
 *
 * The hexproof is a `ConditionalStaticAbility(GrantKeyword(HEXPROOF, Self), Not(YouCastSpellsThisTurn(1)))`.
 * These tests pin the toggle through the projector: hexproof while the controller's spell count is
 * zero, gone the moment they cast a spell. Flash and Flying are always present.
 */
class StoicSphinxScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + StoicSphinx)
        return driver
    }

    test("has flying and hexproof when no spell has been cast this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        // Place the Sphinx directly so casting it isn't counted toward the spell count.
        val sphinx = driver.putCreatureOnBattlefield(you, "Stoic Sphinx")

        val projected = projector.project(driver.state)
        projected.hasKeyword(sphinx, Keyword.FLYING) shouldBe true
        projected.hasKeyword(sphinx, Keyword.HEXPROOF) shouldBe true
    }

    test("loses hexproof once you've cast a spell this turn (flying stays)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val sphinx = driver.putCreatureOnBattlefield(you, "Stoic Sphinx")

        // Cast any spell this turn → controller's spell count becomes 1.
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, listOf(you)).isSuccess shouldBe true

        val projected = projector.project(driver.state)
        projected.hasKeyword(sphinx, Keyword.FLYING) shouldBe true
        projected.hasKeyword(sphinx, Keyword.HEXPROOF) shouldBe false
    }
})
