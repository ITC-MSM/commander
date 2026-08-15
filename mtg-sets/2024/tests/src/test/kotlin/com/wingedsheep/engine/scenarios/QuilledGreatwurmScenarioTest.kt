package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.DistributedCounterRemoval
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Quilled Greatwurm (FDN #111) — {4}{G}{G} 7/7 Creature — Wurm.
 *
 * "Trample
 *  Whenever a creature you control deals combat damage during your turn, put that many +1/+1
 *  counters on it. (It must survive to get the counters.)
 *  You may cast this card from your graveyard by removing six counters from among creatures you
 *  control in addition to paying its other costs."
 *
 * Proven here:
 *  - the counters trigger is battlefield-wide, not self-only: another creature you control that
 *    connects gets counters equal to the damage it dealt;
 *  - it does not fire on an opponent's turn (the "during your turn" gate);
 *  - the graveyard-cast permission works once six counters are available to remove, and the counters
 *    are actually spent;
 *  - without six counters among your creatures, the graveyard cast isn't affordable.
 */
class QuilledGreatwurmScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun GameTestDriver.givePlusOneCounters(id: EntityId, count: Int) {
        replaceState(
            state.updateEntity(id) { c ->
                c.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to count)))
            }
        )
    }

    /**
     * Attack unblocked with [attackers], push combat damage through, and resolve every resulting
     * trigger — including the trigger-ordering prompt two simultaneous counter triggers produce.
     */
    fun GameTestDriver.swingUnblocked(player: EntityId, opponent: EntityId, attackers: List<EntityId>) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(player, attackers, opponent)
        passPriorityUntil(Step.DECLARE_BLOCKERS)
        passPriorityUntil(Step.COMBAT_DAMAGE)
        if (pendingDecision is CombatResolutionDecision) confirmCombatDamage()
        var guard = 0
        while (guard++ < 30) {
            val decision = pendingDecision
            when {
                decision is CombatResolutionDecision -> confirmCombatDamage()
                decision is OrderObjectsDecision ->
                    submitOrderedResponse(decision.playerId, decision.objects)
                decision != null -> autoResolveDecision()
                state.stack.isNotEmpty() -> bothPass()
                else -> return
            }
        }
    }

    fun canCastFromGraveyard(driver: GameTestDriver, player: EntityId, cardId: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        return enumerator.enumerate(driver.state, player, EnumerationMode.FULL).any {
            it.sourceZone == "GRAVEYARD" && it.affordable && (it.action as? CastSpell)?.cardId == cardId
        }
    }

    test("has trample") {
        val d = newDriver()
        val wurm = d.putCreatureOnBattlefield(d.player1, "Quilled Greatwurm")
        d.state.projectedState.hasKeyword(wurm, Keyword.TRAMPLE) shouldBe true
    }

    test("a creature you control that deals combat damage on your turn gets that many +1/+1 counters") {
        val d = newDriver()
        val you = d.player1
        val opponent = d.player2

        val wurm = d.putCreatureOnBattlefield(you, "Quilled Greatwurm")
        d.removeSummoningSickness(wurm)
        // A second, unrelated creature you control — the trigger is battlefield-wide.
        val bears = d.putCreatureOnBattlefield(you, "Grizzly Bears") // 2/2
        d.removeSummoningSickness(bears)

        d.swingUnblocked(you, opponent, listOf(wurm, bears))

        withClue("The Wurm's 7 combat damage becomes seven +1/+1 counters on itself") {
            d.plusOneCounters(wurm) shouldBe 7
        }
        withClue("Grizzly Bears' 2 combat damage becomes two +1/+1 counters on the Bears") {
            d.plusOneCounters(bears) shouldBe 2
        }
        withClue("The opponent still took the printed damage (7 + 2)") {
            d.getLifeTotal(opponent) shouldBe 11
        }
    }

    test("does not trigger on an opponent's turn") {
        val d = newDriver()
        val you = d.player1
        val opponent = d.player2

        val wurm = d.putCreatureOnBattlefield(you, "Quilled Greatwurm")
        val theirBears = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        d.removeSummoningSickness(theirBears)

        // Hand the turn over, then let the opponent attack you.
        d.passPriorityUntil(Step.UPKEEP)
        withClue("It should now be the opponent's turn") {
            (d.activePlayer == opponent) shouldBe true
        }
        d.swingUnblocked(opponent, you, listOf(theirBears))

        withClue("The Wurm's trigger only watches YOUR creatures, and only on your turn") {
            d.plusOneCounters(wurm) shouldBe 0
            d.plusOneCounters(theirBears) shouldBe 0
        }
    }

    test("castable from the graveyard by removing six counters from among your creatures") {
        val d = newDriver()
        val you = d.player1

        val wurm = d.putCardInGraveyard(you, "Quilled Greatwurm")
        val bears = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val elf = d.putCreatureOnBattlefield(you, "Elvish Warrior")
        d.givePlusOneCounters(bears, 4)
        d.givePlusOneCounters(elf, 2)
        d.giveMana(you, Color.GREEN, 6)

        withClue("Six counters across two creatures is enough") {
            canCastFromGraveyard(d, you, wurm) shouldBe true
        }

        val result = d.submit(
            CastSpell(
                playerId = you,
                cardId = wurm,
                targets = emptyList(),
                paymentStrategy = PaymentStrategy.FromPool,
                additionalCostPayment = AdditionalCostPayment(
                    distributedCounterRemovals = listOf(
                        DistributedCounterRemoval(bears, "+1/+1", 4),
                        DistributedCounterRemoval(elf, "+1/+1", 2)
                    )
                )
            )
        )
        withClue("Casting from the graveyard should succeed") { result.isSuccess shouldBe true }
        d.bothPass()

        withClue("The Wurm resolved onto the battlefield") {
            (d.findPermanent(you, "Quilled Greatwurm") != null) shouldBe true
        }
        withClue("All six counters were actually removed") {
            d.plusOneCounters(bears) shouldBe 0
            d.plusOneCounters(elf) shouldBe 0
        }
    }

    test("not castable from the graveyard without six counters to remove") {
        val d = newDriver()
        val you = d.player1

        val wurm = d.putCardInGraveyard(you, "Quilled Greatwurm")
        val bears = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        d.givePlusOneCounters(bears, 5)
        d.giveMana(you, Color.GREEN, 6)

        withClue("Five counters is one short") {
            canCastFromGraveyard(d, you, wurm) shouldBe false
        }
        d.submitExpectFailure(
            CastSpell(
                playerId = you,
                cardId = wurm,
                targets = emptyList(),
                paymentStrategy = PaymentStrategy.FromPool,
                additionalCostPayment = AdditionalCostPayment(
                    distributedCounterRemovals = listOf(
                        DistributedCounterRemoval(bears, "+1/+1", 5)
                    )
                )
            )
        )
    }
})
