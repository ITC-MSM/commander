package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AshlingThePilgrim
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Ashling the Pilgrim (LRW #149) — "{1}{R}: Put a +1/+1 counter on Ashling. If this is the third
 * time this ability has resolved this turn, remove all +1/+1 counters from Ashling, and it deals
 * that much damage to each creature and each player."
 *
 * Two claims that a card built from existing primitives can still get wrong, and neither is
 * visible in the snapshot golden:
 *
 *  - **"That much" is the count *before* the removal.** The damage reads a
 *    `StoreNumber` captured ahead of `RemoveAllCountersOfType`; wiring it to read the counters
 *    directly would evaluate after the removal and deal 0 to everything. Three damage vs. zero is
 *    the whole assertion.
 *  - **The stored number has to survive the per-player loop.** `ForEachPlayerEffect` resets stored
 *    *collections* per iteration; if it reset stored *numbers* too, creatures would take 3 and
 *    players 0. Asserting both halves of "each creature and each player" is what separates them.
 *
 * The first test also pins the equality on the *second* resolution: a `>= 3` mis-wiring reads
 * identically on the card and only shows up there.
 */
class AshlingThePilgrimScenarioTest : FunSpec({

    val flareAbility = AshlingThePilgrim.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(AshlingThePilgrim))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Priority does not reliably revert to the activator after a resolution; normalise. */
    fun handPriorityTo(d: GameTestDriver, player: EntityId) {
        d.priorityPlayer?.takeIf { it != player }?.let { d.passPriority(it) }
    }

    fun flare(d: GameTestDriver, me: EntityId, ashling: EntityId) {
        handPriorityTo(d, me)
        d.submit(ActivateAbility(me, ashling, flareAbility)).isSuccess shouldBe true
        d.bothPass()
        handPriorityTo(d, me)
    }

    test("counters accumulate, and the third resolution converts all of them into damage") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        val ashling = d.putCreatureOnBattlefield(me, "Ashling the Pilgrim")
        val courser = d.putCreatureOnBattlefield(opponent, "Centaur Courser")
        val forceOfNature = d.putCreatureOnBattlefield(opponent, "Force of Nature")
        d.giveMana(me, Color.RED, 12)

        flare(d, me, ashling)
        withClue("first resolution: a counter, nothing else") {
            d.state.projectedState.getPower(ashling) shouldBe 2
            d.getLifeTotal(me) shouldBe 20
            d.getLifeTotal(opponent) shouldBe 20
        }

        flare(d, me, ashling)
        withClue("second resolution: the payoff is an equality, so still no damage") {
            d.state.projectedState.getPower(ashling) shouldBe 3
            d.state.projectedState.getToughness(ashling) shouldBe 3
            d.getLifeTotal(me) shouldBe 20
            d.getLifeTotal(opponent) shouldBe 20
            d.getGraveyardCardNames(opponent) shouldNotContain "Centaur Courser"
        }

        flare(d, me, ashling)
        withClue("third resolution: three counters removed, three damage everywhere") {
            d.getLifeTotal(me) shouldBe 17
            d.getLifeTotal(opponent) shouldBe 17
        }
        withClue("the 3/3 dies to it and the 5/5 does not — the damage really is 3, not lethal-to-all") {
            d.getGraveyardCardNames(opponent) shouldContain "Centaur Courser"
            d.state.projectedState.getToughness(forceOfNature) shouldBe 5
            d.getGraveyardCardNames(opponent) shouldNotContain "Force of Nature"
        }
        withClue("Ashling is a 1/1 again when the damage lands, so it kills itself (2023-07-28 ruling)") {
            d.getGraveyardCardNames(me) shouldContain "Ashling the Pilgrim"
        }
    }

    test("the tally is per-entity, so a second Ashling starts from zero") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        val ashling = d.putCreatureOnBattlefield(me, "Ashling the Pilgrim")
        d.giveMana(me, Color.RED, 12)

        repeat(3) { flare(d, me, ashling) }
        d.getLifeTotal(opponent) shouldBe 17
        d.getGraveyardCardNames(me) shouldContain "Ashling the Pilgrim"

        // "Abilities from other creatures with the same name don't count towards the total."
        val second = d.putCreatureOnBattlefield(me, "Ashling the Pilgrim")
        flare(d, me, second)
        withClue("a fresh Ashling is on its own first resolution — one counter, no damage") {
            d.state.projectedState.getPower(second) shouldBe 2
            d.getLifeTotal(opponent) shouldBe 17
        }
    }
})
