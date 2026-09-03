package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.InciteHysteria
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Incite Hysteria (RAV #132) — "Radiance — Until end of turn, target creature and each other
 * creature that shares a color with it gain 'This creature can't block.'"
 *
 * The radiance group here carries a *restriction* rather than damage or a pump, so the proof has
 * to run through the block declaration itself: every red creature on the far side must be refused
 * as a blocker while a creature of another colour is still legal. The target is restricted by the
 * direct half of the effect and the rest by the group half — a `CantBlock` that only reached the
 * group would leave the named creature free to block, which is the failure this pins down.
 */
class InciteHysteriaScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + InciteHysteria)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.incite(victim: EntityId) {
        giveMana(player1, Color.RED, 3)
        val card = putCardInHand(player1, "Incite Hysteria")
        castSpellWithTargets(player1, card, listOf(ChosenTarget.Permanent(victim))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("every red creature is locked out of blocking; a white one still blocks") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val attacker = d.putCreatureOnBattlefield(me, "Centaur Courser")
        d.removeSummoningSickness(attacker)
        val guide = d.putCreatureOnBattlefield(opp, "Goblin Guide")            // {R} — the target
        val prospector = d.putCreatureOnBattlefield(opp, "Test Hasty Prospector") // {R} — radiated onto
        val lions = d.putCreatureOnBattlefield(opp, "Savannah Lions")          // {W} — untouched

        d.incite(guide)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(attacker), opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("the named target itself must be restricted, not only the radiance group") {
            d.declareBlockers(opp, mapOf(guide to listOf(attacker))).error shouldNotBe null
        }
        withClue("the other red creature was caught by the radiance group") {
            d.declareBlockers(opp, mapOf(prospector to listOf(attacker))).error shouldNotBe null
        }
        withClue("white shares no color with a red target, so it can still block") {
            d.declareBlockers(opp, mapOf(lions to listOf(attacker))).error shouldBe null
        }
    }

    test("a colorless target is restricted alone — colorless shares a color with nothing") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val attacker = d.putCreatureOnBattlefield(me, "Centaur Courser")
        d.removeSummoningSickness(attacker)
        val golem = d.putCreatureOnBattlefield(opp, "Artifact Creature")   // colorless — the target
        val myr = d.putCreatureOnBattlefield(opp, "Palladium Myr")         // another colorless

        d.incite(golem)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(attacker), opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("the colorless target is still restricted by the direct half") {
            d.declareBlockers(opp, mapOf(golem to listOf(attacker))).error shouldNotBe null
        }
        withClue("colorless creatures don't share a color even with each other") {
            d.declareBlockers(opp, mapOf(myr to listOf(attacker))).error shouldBe null
        }
    }
})
