package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.BoggartForager
import com.wingedsheep.mtg.sets.definitions.lrw.cards.LowlandOaf
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Lowland Oaf (LRW #184) — "{T}: Target Goblin creature you control gets +1/+0 and gains flying
 * until end of turn. Sacrifice that creature at the beginning of the next end step."
 *
 * The delayed sacrifice is the half that can silently do nothing: it has to remember *which*
 * creature was targeted after the activation's context is gone. `CreateDelayedTriggerExecutor`
 * bakes the bound target into a concrete entity id at scheduling time, so the test drives it all
 * the way to the end step rather than stopping at the pump.
 *
 * The negative half is the target restriction — a Goblin an *opponent* controls is not a legal
 * target, and both readings ("you control" vs. the implicit fallback) look identical on the card.
 */
class LowlandOafScenarioTest : FunSpec({

    val oafAbility = LowlandOaf.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(LowlandOaf, BoggartForager))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the launched Goblin gets +1/+0 and flying, then is sacrificed at the next end step") {
        val d = driver()
        val me = d.activePlayer!!
        val oaf = d.putCreatureOnBattlefield(me, "Lowland Oaf")
        d.removeSummoningSickness(oaf)
        val goblin = d.putCreatureOnBattlefield(me, "Boggart Forager")

        d.submit(
            ActivateAbility(me, oaf, oafAbility, targets = listOf(ChosenTarget.Permanent(goblin)))
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("a 1/1 Goblin becomes a 2/1 flier") {
            d.state.projectedState.getPower(goblin) shouldBe 2
            d.state.projectedState.getToughness(goblin) shouldBe 1
            d.state.projectedState.hasKeyword(goblin, Keyword.FLYING) shouldBe true
        }
        withClue("nothing is sacrificed while the ability resolves") {
            d.getGraveyard(me).contains(goblin) shouldBe false
        }

        d.passPriorityUntil(Step.END)
        d.bothPass()

        d.findPermanent(me, "Boggart Forager") shouldBe null
        d.getGraveyard(me) shouldContain goblin
    }

    test("a Goblin an opponent controls is not a legal target") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        val oaf = d.putCreatureOnBattlefield(me, "Lowland Oaf")
        d.removeSummoningSickness(oaf)
        val theirGoblin = d.putCreatureOnBattlefield(opponent, "Boggart Forager")

        d.submitExpectFailure(
            ActivateAbility(me, oaf, oafAbility, targets = listOf(ChosenTarget.Permanent(theirGoblin)))
        ).isSuccess shouldBe false
        withClue("the opponent's Goblin is untouched") {
            d.state.projectedState.getPower(theirGoblin) shouldBe 1
        }
    }

    test("a non-Goblin creature you control is not a legal target") {
        val d = driver()
        val me = d.activePlayer!!
        val oaf = d.putCreatureOnBattlefield(me, "Lowland Oaf")
        d.removeSummoningSickness(oaf)
        val bear = d.putCreatureOnBattlefield(me, "Grizzly Bears")

        d.submitExpectFailure(
            ActivateAbility(me, oaf, oafAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe false
    }
})
