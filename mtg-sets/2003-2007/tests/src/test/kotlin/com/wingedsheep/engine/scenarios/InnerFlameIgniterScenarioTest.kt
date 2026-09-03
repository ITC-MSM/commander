package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.InnerFlameIgniter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Inner-Flame Igniter (LRW #182) — "{2}{R}: Creatures you control get +1/+0 until end of turn. If
 * this is the third time this ability has resolved this turn, creatures you control gain first
 * strike until end of turn."
 *
 * The claim under test is the *equality*, not a threshold: the pump stacks on every resolution,
 * the first strike arrives on the third and on no earlier one. A `>= 3` mis-wiring reads
 * identically on the card and is only visible by checking the second resolution, so that
 * assertion is the point of the test rather than a nicety.
 *
 * The second test pins the group: "creatures you control" must skip the opponent's board, which
 * is the axis where the implicit "you control" fallback and an unfiltered group give opposite
 * answers.
 */
class InnerFlameIgniterScenarioTest : FunSpec({

    val igniteAbility = InnerFlameIgniter.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(InnerFlameIgniter))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Priority does not reliably revert to the activator after a resolution; normalise. */
    fun handPriorityTo(d: GameTestDriver, player: EntityId) {
        d.priorityPlayer?.takeIf { it != player }?.let { d.passPriority(it) }
    }

    fun ignite(d: GameTestDriver, me: EntityId, igniter: EntityId) {
        handPriorityTo(d, me)
        d.submit(ActivateAbility(me, igniter, igniteAbility)).isSuccess shouldBe true
        d.bothPass()
        handPriorityTo(d, me)
    }

    test("the pump stacks every resolution; first strike arrives on the third and not the second") {
        val d = driver()
        val me = d.activePlayer!!
        val igniter = d.putCreatureOnBattlefield(me, "Inner-Flame Igniter")
        val courser = d.putCreatureOnBattlefield(me, "Centaur Courser")
        d.giveMana(me, Color.RED, 9)

        ignite(d, me, igniter)
        withClue("first resolution: +1/+0 only") {
            d.state.projectedState.getPower(courser) shouldBe 4
            d.state.projectedState.getToughness(courser) shouldBe 3
            d.state.projectedState.hasKeyword(courser, Keyword.FIRST_STRIKE) shouldBe false
        }

        ignite(d, me, igniter)
        withClue("second resolution: the bonus is an equality, so still no first strike") {
            d.state.projectedState.getPower(courser) shouldBe 5
            d.state.projectedState.hasKeyword(courser, Keyword.FIRST_STRIKE) shouldBe false
        }

        ignite(d, me, igniter)
        withClue("third resolution: +1/+0 again, and now first strike") {
            d.state.projectedState.getPower(courser) shouldBe 6
            d.state.projectedState.hasKeyword(courser, Keyword.FIRST_STRIKE) shouldBe true
        }
        withClue("the Igniter pumps itself too — \"creatures you control\" is not \"other\"") {
            d.state.projectedState.getPower(igniter) shouldBe 5
            d.state.projectedState.hasKeyword(igniter, Keyword.FIRST_STRIKE) shouldBe true
        }
    }

    test("the opponent's creatures get nothing") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        val igniter = d.putCreatureOnBattlefield(me, "Inner-Flame Igniter")
        val theirs = d.putCreatureOnBattlefield(opponent, "Centaur Courser")
        d.giveMana(me, Color.RED, 9)

        repeat(3) { ignite(d, me, igniter) }

        withClue("\"creatures you control\" excludes the opponent's board on both clauses") {
            d.state.projectedState.getPower(theirs) shouldBe 3
            d.state.projectedState.hasKeyword(theirs, Keyword.FIRST_STRIKE) shouldBe false
        }
    }
})
