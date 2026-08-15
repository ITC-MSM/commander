package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SidequestPlayBlitzball
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Sidequest: Play Blitzball // World Champion, Celestial Weapon (FIN).
 *
 * Front:
 *   - At the beginning of combat on your turn, target creature you control gets +2/+0 EOT.
 *   - At the end of combat on your turn, if a player was dealt 6+ combat damage this turn,
 *     transform this enchantment, then attach it to a creature you control.
 * Back — World Champion, Celestial Weapon: equipped creature gets +2/+0 and has double strike.
 *
 * Test 1 (transform path): a 6/6 attacks unblocked, gets +2/+0 at begin of combat (→8/6), deals 8
 *   combat damage (≥6). At end of combat the enchantment transforms into World Champion and attaches
 *   to the attacker, which then has double strike and the equipment's +2/+0.
 * Test 2 (threshold gate): a 2/2 attacks unblocked, buffed to 4/2, deals only 4 combat damage (<6),
 *   so the intervening-"if" fails and the enchantment does not transform.
 */
class SidequestPlayBlitzballScenarioTest : FunSpec({

    val bigBeast = card("Test Big Beast") {
        manaCost = "{6}"
        typeLine = "Creature — Beast"
        power = 6
        toughness = 6
    }
    val smallBeast = card("Test Small Beast") {
        manaCost = "{1}"
        typeLine = "Creature — Beast"
        power = 2
        toughness = 2
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SidequestPlayBlitzball, bigBeast, smallBeast))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return d
    }

    // Advance to declare-attackers, resolving the begin-of-combat +2/+0 trigger by targeting
    // [creature]. (passPriorityUntil can't auto-resolve the ChooseTargetsDecision it raises.)
    fun GameTestDriver.resolveBeginCombatBuff(me: EntityId, creature: EntityId) {
        var guard = 0
        while (state.step != Step.DECLARE_ATTACKERS && guard++ < 100) {
            val decision = pendingDecision
            when {
                decision is ChooseTargetsDecision -> submitTargetSelection(decision.playerId, listOf(creature))
                decision != null -> autoResolveDecision()
                state.priorityPlayerId != null -> passPriority(state.priorityPlayerId!!)
                else -> break
            }
        }
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard++ < 50) bothPass()
    }

    test("transforms into the Celestial Weapon and attaches when a player took 6+ combat damage") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)

        val enchant = d.putPermanentOnBattlefield(me, "Sidequest: Play Blitzball")
        val beast = d.putCreatureOnBattlefield(me, "Test Big Beast")
        d.removeSummoningSickness(beast)

        // Begin of combat: target creature you control gets +2/+0 (6/6 -> 8/6).
        d.resolveBeginCombatBuff(me, beast)
        d.state.projectedState.getPower(beast) shouldBe 8

        // Attack unblocked; 8 combat damage to the opponent (>= 6).
        d.declareAttackers(me, listOf(beast), opponent)
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opponent)
        // Pass through the combat-damage step (auto-resolves damage) to end of combat.
        d.passPriorityUntil(Step.END_COMBAT)
        d.assertLifeTotal(opponent, 12)

        // End of combat: the intervening-"if" holds, so it transforms and attaches to the beast.
        d.resolveStack()

        d.getCardName(enchant) shouldBe "World Champion, Celestial Weapon"
        d.state.getEntity(enchant)?.get<AttachedToComponent>()?.targetId shouldBe beast
        // The equipment's static abilities now apply to the equipped creature.
        d.state.projectedState.hasKeyword(beast, Keyword.DOUBLE_STRIKE) shouldBe true
    }

    test("does not transform when no player took 6 or more combat damage") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)

        val enchant = d.putPermanentOnBattlefield(me, "Sidequest: Play Blitzball")
        val beast = d.putCreatureOnBattlefield(me, "Test Small Beast")
        d.removeSummoningSickness(beast)

        // Begin of combat: 2/2 -> 4/2.
        d.resolveBeginCombatBuff(me, beast)
        d.state.projectedState.getPower(beast) shouldBe 4

        // Attack unblocked; only 4 combat damage (< 6).
        d.declareAttackers(me, listOf(beast), opponent)
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opponent)
        d.passPriorityUntil(Step.END_COMBAT)
        d.assertLifeTotal(opponent, 16)

        d.resolveStack()

        // Intervening-"if" failed → still the enchantment, unattached, untransformed.
        d.getCardName(enchant) shouldBe "Sidequest: Play Blitzball"
    }
})
