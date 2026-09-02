package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.SistersOfStoneDeath
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Sisters of Stone Death (RAV #231) —
 * "{G}: Target creature blocks Sisters of Stone Death this turn if able.
 *  {B}{G}: Exile target creature blocking or blocked by Sisters of Stone Death.
 *  {2}{B}: Put a creature card exiled with Sisters of Stone Death onto the battlefield under your
 *  control."
 *
 * The first test is the one the executor change is for: the lure is activated in the precombat
 * main phase, *before* the Sisters attack, and the requirement must still bind once they do.
 * The second walks the linked pair — exile a blocker mid-combat, then reanimate it under the
 * Sisters' controller.
 */
class SistersOfStoneDeathScenarioTest : FunSpec({

    val lure = SistersOfStoneDeath.activatedAbilities[0].id
    val petrify = SistersOfStoneDeath.activatedAbilities[1].id
    val reanimate = SistersOfStoneDeath.activatedAbilities[2].id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + SistersOfStoneDeath)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    fun GameTestDriver.activate(me: EntityId, sisters: EntityId, ability: AbilityId, targets: List<EntityId> = emptyList()) {
        val result = submit(
            ActivateAbility(
                playerId = me,
                sourceId = sisters,
                abilityId = ability,
                targets = targets.map { ChosenTarget.Permanent(it) },
            )
        )
        withClue(result.error ?: "activation failed") { result.isSuccess shouldBe true }
        resolveStack()
    }

    test("the lure activated before attackers are declared still binds once the Sisters attack") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val sisters = d.putCreatureOnBattlefield(me, "Sisters of Stone Death")
        d.removeSummoningSickness(sisters)
        val bear = d.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val free = d.putCreatureOnBattlefield(opp, "Centaur Courser")

        // Precombat main: nothing is attacking yet.
        d.giveMana(me, Color.GREEN, 1)
        d.activate(me, sisters, lure, listOf(bear))

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(sisters), defendingPlayer = opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("the bear is under a requirement to block the Sisters") {
            d.declareBlockers(opp, emptyMap()).isSuccess shouldBe false
            d.declareBlockers(opp, mapOf(bear to listOf(sisters))).error shouldBe null
        }
        d.state.getEntity(free).shouldNotBeNull()
    }

    test("exile a creature blocking the Sisters, then put it onto the battlefield under your control") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val sisters = d.putCreatureOnBattlefield(me, "Sisters of Stone Death")
        d.removeSummoningSickness(sisters)
        val bear = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(sisters), defendingPlayer = opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(opp, mapOf(bear to listOf(sisters))).error shouldBe null
        // Priority in the declare-blockers step may sit with the defender first; a single pass on
        // an empty stack hands it to the active player without leaving the step.
        if (d.priorityPlayer != me) d.passPriority(opp).error shouldBe null
        d.priorityPlayer shouldBe me

        // Before damage: exile the blocker, linked to the Sisters.
        d.giveMana(me, Color.BLACK, 1)
        d.giveMana(me, Color.GREEN, 1)
        d.activate(me, sisters, petrify, listOf(bear))
        withClue("the blocker is in its owner's exile, not the graveyard") {
            d.findPermanent(opp, "Grizzly Bears") shouldBe null
            d.getExileCardNames(opp) shouldContain "Grizzly Bears"
            d.getGraveyardCardNames(opp) shouldNotContain "Grizzly Bears"
        }

        // The linked return: the only creature card in the pile comes back under my control.
        d.giveMana(me, Color.BLACK, 1)
        d.giveColorlessMana(me, 2)
        d.activate(me, sisters, reanimate)
        val returned = d.findPermanent(me, "Grizzly Bears")
        withClue("the exiled creature is on the battlefield under the Sisters' controller") {
            returned.shouldNotBeNull()
            d.getController(returned) shouldBe me
            d.getExileCardNames(opp) shouldNotContain "Grizzly Bears"
        }
    }

    test("a creature only blocked by the Sisters is not a legal petrify target while not paired") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val sisters = d.putCreatureOnBattlefield(me, "Sisters of Stone Death")
        d.removeSummoningSickness(sisters)
        val bystander = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        // Precombat main — nothing is blocking or blocked by the Sisters, so there is no target.
        d.giveMana(me, Color.BLACK, 1)
        d.giveMana(me, Color.GREEN, 1)
        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = sisters,
                abilityId = petrify,
                targets = listOf(ChosenTarget.Permanent(bystander)),
            )
        ).isSuccess shouldBe false
        d.findPermanent(opp, "Grizzly Bears").shouldNotBeNull()
    }
})
