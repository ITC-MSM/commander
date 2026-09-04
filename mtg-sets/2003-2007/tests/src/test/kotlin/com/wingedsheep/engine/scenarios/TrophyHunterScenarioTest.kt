package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.TrophyHunter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Trophy Hunter (RAV #187) — "{1}{G}: This creature deals 1 damage to target creature with flying.
 * Whenever a creature with flying dealt damage by this creature this turn dies, put a +1/+1 counter
 * on this creature."
 *
 * The trigger's two halves are independent, and the pair of tests here separates them. The damage
 * tracker (a creature Trophy Hunter damaged this turn died) was already engine behaviour; what is
 * new is the `dyingFilter` narrowing it to a creature *with flying*, read off the dying creature's
 * last-known information. So: a flyer Trophy Hunter pinged earns the counter, and a non-flyer it
 * killed in combat — same tracker, same turn, same source — does not.
 */
class TrophyHunterScenarioTest : FunSpec({

    val pingAbility = TrophyHunter.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + TrophyHunter)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("pinging a flyer to death puts a +1/+1 counter on Trophy Hunter") {
        val d = driver()
        val me = d.player1
        val opp = d.getOpponent(me)

        val hunter = d.putCreatureOnBattlefield(me, "Trophy Hunter")
        d.removeSummoningSickness(hunter)
        val bird = d.putCreatureOnBattlefield(opp, "Birds of Paradise")  // 0/1 flyer — 1 damage is lethal

        d.giveMana(me, Color.GREEN, 2)
        d.submit(
            ActivateAbility(me, hunter, pingAbility, targets = listOf(ChosenTarget.Permanent(bird)))
        ).isSuccess shouldBe true
        d.passPriority(me)
        // The ping resolves, the bird dies to SBAs, and the death trigger then needs its own
        // resolution round.
        var guard = 0
        while (d.stackSize > 0 && guard++ < 10) d.bothPass()

        withClue("the bird died to the ping") {
            d.state.getEntity(bird)?.let { d.state.getBattlefield().contains(bird) } shouldBe false
        }
        withClue("a flyer Trophy Hunter damaged this turn died, so the trigger resolved") {
            d.plusOneCounters(hunter) shouldBe 1
        }
    }

    test("a non-flying creature Trophy Hunter killed in combat earns no counter") {
        val d = driver()
        val me = d.player1
        val opp = d.getOpponent(me)

        val hunter = d.putCreatureOnBattlefield(me, "Trophy Hunter")     // 2/3
        d.removeSummoningSickness(hunter)
        val lions = d.putCreatureOnBattlefield(opp, "Savannah Lions")    // 2/1, no flying

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(hunter), opp)
        d.bothPass()
        d.declareBlockers(opp, mapOf(lions to listOf(hunter)))
        d.bothPass() // end of declare blockers
        d.bothPass() // first strike damage (nobody has it)
        d.bothPass() // combat damage

        withClue("the blocker died to Trophy Hunter's combat damage") {
            d.state.getBattlefield().contains(lions) shouldBe false
        }
        withClue("the damage tracker fired, but the dying creature had no flying") {
            d.plusOneCounters(hunter) shouldBe 0
        }
    }

    test("a flyer Trophy Hunter never damaged earns no counter when it dies") {
        val d = driver()
        val me = d.player1
        val opp = d.getOpponent(me)

        val hunter = d.putCreatureOnBattlefield(me, "Trophy Hunter")
        d.removeSummoningSickness(hunter)
        val bird = d.putCreatureOnBattlefield(opp, "Birds of Paradise")

        d.moveToGraveyard(bird)
        d.bothPass()

        withClue("the flying half is satisfied, the damaged-by-this half is not") {
            d.plusOneCounters(hunter) shouldBe 0
        }
    }
})
