package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.TheAncientOne
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The Ancient One — {U}{B} 8/8 Legendary Creature — Spirit God (LCI #222).
 *
 *  - Descend 8: can't attack or block unless there are eight or more permanent cards in your
 *    graveyard. Modeled as two [com.wingedsheep.sdk.scripting.ConditionalStaticAbility] gates
 *    (CantAttack / CantBlock) that apply while you have FEWER than eight permanent cards there.
 *  - {2}{U}{B}: Draw a card, then discard a card. When you discard a card this way, target player
 *    mills cards equal to its mana value — a reflexive trigger reading the discarded card's mana
 *    value ([DynamicAmount.StoredCardManaValue]) after it has moved to the graveyard.
 */
class TheAncientOneScenarioTest : FunSpec({

    val activatedAbilityId = TheAncientOne.activatedAbilities.first().id

    fun createDriver(startingPlayer: Int = 0): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TheAncientOne)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20, startingPlayer = startingPlayer)
        return driver
    }

    // -------------------------------------------------------------------------
    // Descend 8 — can't attack unless 8+ permanent cards in graveyard
    // -------------------------------------------------------------------------

    test("can't attack with fewer than eight permanent cards in your graveyard") {
        val driver = createDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val ancientOne = driver.putCreatureOnBattlefield(you, "The Ancient One")
        driver.removeSummoningSickness(ancientOne)
        // Seven permanent cards in the graveyard — one short of Descend 8.
        repeat(7) { driver.putCardInGraveyard(you, "Swamp") }

        // Advance to the declare-attackers step directly rather than via
        // passPriorityUntil(DECLARE_ATTACKERS): with The Ancient One restricted and no
        // other creature able to attack, the engine legitimately *skips* the
        // declare-attackers step, so passPriorityUntil would cross turn boundaries — and the
        // active player's cleanup discards would push the graveyard past eight, masking the
        // restriction. Mirrors CantAttackGroupTest's "prevents attack declarations" case.
        driver.replaceState(driver.state.copy(phase = Phase.COMBAT, step = Step.DECLARE_ATTACKERS))

        withClue("Descend 8 unmet (7 permanent cards) → can't attack") {
            driver.declareAttackers(you, listOf(ancientOne), opponent).error shouldNotBe null
        }
    }

    test("can attack once eight or more permanent cards are in your graveyard") {
        val driver = createDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val ancientOne = driver.putCreatureOnBattlefield(you, "The Ancient One")
        driver.removeSummoningSickness(ancientOne)
        // Eight permanent cards in the graveyard — Descend 8 satisfied.
        repeat(8) { driver.putCardInGraveyard(you, "Swamp") }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        withClue("Descend 8 met (8 permanent cards) → can attack") {
            driver.declareAttackers(you, listOf(ancientOne), opponent).error shouldBe null
        }
    }

    // -------------------------------------------------------------------------
    // Descend 8 — can't block unless 8+ permanent cards in graveyard
    // -------------------------------------------------------------------------

    test("can't block with fewer than eight permanent cards, can once threshold met") {
        // Restricted: 7 permanent cards → The Ancient One can't block.
        run {
            val driver = createDriver()
            val attacker = driver.player1
            val defender = driver.getOpponent(attacker)

            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            val ancientOne = driver.putCreatureOnBattlefield(defender, "The Ancient One")
            val bear = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
            driver.removeSummoningSickness(bear)
            repeat(7) { driver.putCardInGraveyard(defender, "Swamp") }

            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            driver.declareAttackers(attacker, listOf(bear), defender)
            driver.bothPass()
            withClue("Descend 8 unmet (7 permanent cards) → can't block") {
                driver.declareBlockers(defender, mapOf(ancientOne to listOf(bear))).error shouldNotBe null
            }
        }

        // Lifted: 8 permanent cards → The Ancient One can block.
        run {
            val driver = createDriver()
            val attacker = driver.player1
            val defender = driver.getOpponent(attacker)

            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            val ancientOne = driver.putCreatureOnBattlefield(defender, "The Ancient One")
            val bear = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
            driver.removeSummoningSickness(bear)
            repeat(8) { driver.putCardInGraveyard(defender, "Swamp") }

            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            driver.declareAttackers(attacker, listOf(bear), defender)
            driver.bothPass()
            withClue("Descend 8 met (8 permanent cards) → can block") {
                driver.declareBlockers(defender, mapOf(ancientOne to listOf(bear))).error shouldBe null
            }
        }
    }

    // -------------------------------------------------------------------------
    // {2}{U}{B}: Draw, discard, target player mills equal to discarded card's MV
    // -------------------------------------------------------------------------

    test("activated ability: target player mills cards equal to the discarded card's mana value") {
        val driver = createDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val ancientOne = driver.putCreatureOnBattlefield(you, "The Ancient One")

        // A known mana-value-3 card to discard ({2}{G} Centaur Courser).
        val discardTarget = driver.putCardInHand(you, "Centaur Courser")

        // {2}{U}{B}: one blue for {U}, three black for {B} + two generic.
        driver.giveMana(you, Color.BLUE, 1)
        driver.giveMana(you, Color.BLACK, 3)

        driver.submit(ActivateAbility(playerId = you, sourceId = ancientOne, abilityId = activatedAbilityId))
            .isSuccess shouldBe true

        // Resolve the ability: choose the {2}{G} card to discard, then aim the mill at the opponent.
        var guard = 0
        while (guard++ < 60 && (driver.state.stack.isNotEmpty() || driver.state.pendingDecision != null)) {
            when (val decision = driver.state.pendingDecision) {
                is SelectCardsDecision -> driver.submitCardSelection(decision.playerId, listOf(discardTarget))
                is ChooseTargetsDecision -> driver.submitTargetSelection(decision.playerId, listOf(opponent))
                else -> driver.bothPass()
            }
        }

        withClue("the discarded card is in your graveyard") {
            driver.getGraveyardCardNames(you).contains("Centaur Courser") shouldBe true
        }
        withClue("opponent milled cards equal to the discarded card's mana value (Centaur Courser = 3)") {
            driver.getGraveyard(opponent).size shouldBe 3
            driver.getGraveyardCardNames(opponent).all { it == "Swamp" } shouldBe true
        }
    }
})
