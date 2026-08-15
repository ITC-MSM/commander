package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Rollercrusher Ride (DSK).
 *
 * Oracle:
 *  - Delirium — If a source you control would deal noncombat damage to a permanent or player while
 *    there are four or more card types among cards in your graveyard, it deals double that damage
 *    instead.
 *  - When The Rollercrusher Ride enters, it deals X damage to each of up to X target creatures.
 *
 * Damage is asserted via lethality (toughness thresholds) rather than marked damage, because marked
 * damage is wiped at cleanup: the contrast between a 3/3 surviving the base X = 2 (no delirium) and
 * dying once X is doubled to 4 (with delirium) pins the doubling precisely.
 *
 * Covers: the `{X}` riding onto the permanent ([DynamicAmount.CastX]) so the enters trigger targets
 * *up to X* creatures and deals X to each; the new delirium-gated [DoubleDamage.restrictions]; X = 0.
 */
class TheRollercrusherRideScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply { registerCards(TestCards.all) }

    fun GameTestDriver.isAlive(id: EntityId): Boolean = getController(id) != null

    /** Put a 4th-or-more card type into your graveyard so [Conditions.Delirium] holds (4 types). */
    fun GameTestDriver.giveDelirium(playerId: EntityId) {
        putCardInGraveyard(playerId, "Artifact Creature") // artifact + creature
        putCardInGraveyard(playerId, "Lightning Bolt")    // instant
        putCardInGraveyard(playerId, "Test Enchantment")  // enchantment
    }

    fun GameTestDriver.resolveStack(maxPasses: Int = 8) {
        repeat(maxPasses) { if (pendingDecision == null) bothPass() }
    }

    test("ETB deals X damage to each of up to X target creatures (no delirium → not doubled)") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val small = d.putCreatureOnBattlefield(active, "Black Creature")   // 2/2
        val medium = d.putCreatureOnBattlefield(active, "Centaur Courser") // 3/3

        val ride = d.putCardInHand(active, "The Rollercrusher Ride")
        d.giveMana(active, Color.RED, 5) // {X=2}{2}{R}
        d.castXSpell(active, ride, xValue = 2).isSuccess shouldBe true
        d.resolveStack()

        // The enters trigger asks for up to X (=2) target creatures.
        val decision = d.pendingDecision as? ChooseTargetsDecision
            ?: error("Expected ChooseTargetsDecision, got ${d.pendingDecision}")
        d.submitTargetSelection(active, listOf(small, medium))
        d.resolveStack()

        withClue("X = 2 to each: the 2/2 dies, the 3/3 survives (took exactly 2, undoubled)") {
            d.isAlive(small) shouldBe false
            d.isAlive(medium) shouldBe true
        }
    }

    test("with delirium, the noncombat ETB damage is doubled (3/3 now dies)") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.giveDelirium(active)

        val medium = d.putCreatureOnBattlefield(active, "Centaur Courser") // 3/3

        val ride = d.putCardInHand(active, "The Rollercrusher Ride")
        d.giveMana(active, Color.RED, 5) // {X=2}{2}{R}
        d.castXSpell(active, ride, xValue = 2).isSuccess shouldBe true
        d.resolveStack()

        (d.pendingDecision as? ChooseTargetsDecision)
            ?: error("Expected ChooseTargetsDecision, got ${d.pendingDecision}")
        d.submitTargetSelection(active, listOf(medium))
        d.resolveStack()

        withClue("X = 2 doubled to 4 by delirium kills the 3/3") {
            d.isAlive(medium) shouldBe false
        }
    }

    test("X = 0 enters with no targets and deals no damage") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val medium = d.putCreatureOnBattlefield(active, "Centaur Courser") // 3/3

        val ride = d.putCardInHand(active, "The Rollercrusher Ride")
        d.giveMana(active, Color.RED, 3) // {X=0}{2}{R}
        d.castXSpell(active, ride, xValue = 0).isSuccess shouldBe true
        d.resolveStack()

        withClue("With X = 0 there is nothing to target and no creature is harmed") {
            d.isAlive(medium) shouldBe true
        }
    }
})
