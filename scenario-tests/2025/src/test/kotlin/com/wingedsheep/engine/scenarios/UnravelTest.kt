package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eoe.cards.Unravel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Unravel: {1}{U}{U} Instant
 * "Counter target spell. If the amount of mana spent to cast that spell was
 *  less than its mana value, you draw a card."
 *
 * Verifies the new `EntityNumericProperty.ManaSpent` primitive: the conditional
 * draw fires iff the targeted spell's recorded `manaSpent{Color}` total is
 * strictly less than its `CardComponent.manaValue` (Rule 202.3 / oracle ruling:
 * cost reductions don't change mana value, but they do reduce mana spent).
 */
class UnravelTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Unravel)
        return driver
    }

    test("counters target spell but doesn't draw when full cost was paid") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // P2 casts Lightning Bolt at P1 — paid {R} in full so manaSpent == manaValue == 1.
        val bolt = driver.putCardInHand(p2, "Lightning Bolt")
        driver.giveMana(p2, Color.RED, 1)
        driver.passPriority(p1)
        driver.castSpell(p2, bolt, listOf(p1))
        val boltOnStack = driver.getTopOfStack()!!

        driver.passPriority(p2)

        // P1 casts Unravel targeting the Bolt.
        val handBefore = driver.getHand(p1).size
        val unravel = driver.putCardInHand(p1, "Unravel")
        driver.giveMana(p1, Color.BLUE, 3)
        driver.castSpellWithTargets(p1, unravel, listOf(ChosenTarget.Spell(boltOnStack)))

        driver.bothPass()

        // Bolt was countered into P2's graveyard.
        driver.getGraveyardCardNames(p2) shouldContain "Lightning Bolt"
        // No bonus draw — mana spent (1) was not less than mana value (1).
        driver.getHand(p1).size shouldBe handBefore
    }

    test("draws a card when target spell was cast for less than its mana value") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // P2 casts Lightning Bolt; we then patch the stack object to simulate the
        // spell having been cast for less than its mana value (the same shape any
        // cost-reducing keyword like affinity would produce).
        val bolt = driver.putCardInHand(p2, "Lightning Bolt")
        driver.giveMana(p2, Color.RED, 1)
        driver.passPriority(p1)
        driver.castSpell(p2, bolt, listOf(p1))
        val boltOnStack = driver.getTopOfStack()!!

        driver.replaceState(
            driver.state.updateEntity(boltOnStack) { container ->
                val spell = container.get<SpellOnStackComponent>()!!
                container.with(spell.copy(manaSpentRed = 0))
            }
        )

        driver.passPriority(p2)

        val handBefore = driver.getHand(p1).size
        val unravel = driver.putCardInHand(p1, "Unravel")
        driver.giveMana(p1, Color.BLUE, 3)
        driver.castSpellWithTargets(p1, unravel, listOf(ChosenTarget.Spell(boltOnStack)))

        driver.bothPass()

        driver.getGraveyardCardNames(p2) shouldContain "Lightning Bolt"
        // Mana spent (0) is less than mana value (1) — Unravel triggers its draw clause.
        driver.getHand(p1).size shouldBe handBefore + 1
    }
})
