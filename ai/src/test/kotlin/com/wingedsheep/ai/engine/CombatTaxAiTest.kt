package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.ActionProcessor
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.BairdStewardOfArgive
import com.wingedsheep.mtg.sets.definitions.ori.cards.ArchangelOfTithes
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The AI and combat taxes — Baird, Steward of Argive ("creatures can't attack you unless their
 * controller pays {1} for each of those creatures") and Archangel of Tithes' blocking half.
 *
 * The engine asks for the tax *after* the declaration, as a decision the declarer may decline, and
 * declining puts the game back at the declaration step with nothing changed. So an AI that declares
 * a combat it cannot pay for is handed back the position it just chose from — and, being
 * deterministic, chooses the same declaration again. Reported as "engine AI goes in an infinite loop
 * selecting mana sources"; the fix is [CombatTaxBudget], which prices the tax before proposing.
 */
class CombatTaxAiTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(BairdStewardOfArgive, ArchangelOfTithes, PredefinedTokens.Treasure))
        initMirrorMatch(Deck.of("Plains" to 20, "Forest" to 20))
    }

    /**
     * Two attackers for [attacker], Baird for their opponent.
     *
     * Unblockable ones, so that whether the AI attacks turns on the tax and nothing else: a
     * blockable creature facing Baird's own 2/4 body is a plan the advisor can reject on its merits.
     */
    fun GameTestDriver.taxedBoard(attacker: EntityId, defender: EntityId): List<EntityId> {
        val a1 = putCreatureOnBattlefield(attacker, "Phantom Warrior")
        val a2 = putCreatureOnBattlefield(attacker, "Phantom Warrior")
        removeSummoningSickness(a1)
        removeSummoningSickness(a2)
        putCreatureOnBattlefield(defender, "Baird, Steward of Argive")
        return listOf(a1, a2)
    }

    fun GameTestDriver.attackAction(playerId: EntityId) =
        LegalActionEnumerator.create(cardRegistry).enumerate(state, playerId)
            .single { it.actionType == "DeclareAttackers" }

    test("declares no attackers when it cannot pay the attack tax") {
        val driver = driver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.taxedBoard(p1, p2)
        // No untapped lands: the {2} Baird asks for two attackers is unpayable.
        val turn = driver.state.turnNumber
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.state.turnNumber shouldBe turn

        val ai = AIPlayer.create(driver.cardRegistry, p1)
        val chosen = ai.chooseFrom(driver.state, listOf(driver.attackAction(p1))).action as DeclareAttackers

        chosen.attackers.keys.shouldBeEmpty()
        driver.submit(chosen).isSuccess shouldBe true
        driver.state.pendingDecision shouldBe null
    }

    test("still attacks when it can pay the attack tax") {
        val driver = driver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.taxedBoard(p1, p2)
        repeat(2) { driver.putLandOnBattlefield(p1, "Forest") }
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        val ai = AIPlayer.create(driver.cardRegistry, p1)
        val chosen = ai.chooseFrom(driver.state, listOf(driver.attackAction(p1))).action as DeclareAttackers

        chosen.attackers.size shouldBe 2
        // The engine pauses for the {2}; the AI's own responder pays it and combat proceeds.
        val paused = driver.submit(chosen)
        paused.error shouldBe null
        val decision = driver.state.pendingDecision
        decision shouldNotBe null
        driver.submitDecision(p1, ai.respondToDecision(driver.state, decision!!)).isSuccess shouldBe true
        driver.state.pendingDecision shouldBe null
        driver.getUntappedLands(p1).shouldBeEmpty()
    }

    test("trims the attack down to the attackers it can pay for") {
        val driver = driver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.taxedBoard(p1, p2)
        // Three unblockable attackers, one Forest: {3} to send them all, and {1} in the bank.
        val third = driver.putCreatureOnBattlefield(p1, "Phantom Warrior")
        driver.removeSummoningSickness(third)
        driver.putLandOnBattlefield(p1, "Forest")
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        val ai = AIPlayer.create(driver.cardRegistry, p1)
        val chosen = ai.chooseFrom(driver.state, listOf(driver.attackAction(p1))).action as DeclareAttackers

        chosen.attackers.size shouldBe 1
        driver.submit(chosen).error shouldBe null
    }

    test("a Treasure does not count as attack-tax mana") {
        // `ManaSolver.canPay` counts Treasures, but combat-tax payment refuses sacrifice sources —
        // the prompt for a Treasure-only tax comes back with an empty auto-pay suggestion and
        // nothing to do but decline. So the AI must read this board as unpayable, not as payable.
        val driver = driver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.taxedBoard(p1, p2)
        driver.putPermanentOnBattlefield(p1, "Treasure")
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        val ai = AIPlayer.create(driver.cardRegistry, p1)
        val chosen = ai.chooseFrom(driver.state, listOf(driver.attackAction(p1))).action as DeclareAttackers

        chosen.attackers.keys.shouldBeEmpty()
        driver.submit(chosen).isSuccess shouldBe true
        driver.state.pendingDecision shouldBe null
    }

    test("an AI turn against an unpayable tax reaches the end step") {
        val driver = driver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.taxedBoard(p1, p2)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        val processor = ActionProcessor(driver.cardRegistry)
        val ai1 = AIPlayer.create(driver.cardRegistry, p1)
        val ai2 = AIPlayer.create(driver.cardRegistry, p2)

        // Before the fix this never left DECLARE_ATTACKERS: declare both warriors, get asked for the
        // tax, decline, repeat. `windows` is the livelock detector — the step advances in a handful.
        var state = driver.state
        var windows = 0
        while (!state.gameOver && state.step == Step.DECLARE_ATTACKERS && windows < 40) {
            state = when (state.priorityPlayerId) {
                p1 -> ai1.playPriorityWindow(state, processor)
                else -> ai2.playPriorityWindow(state, processor)
            }
            windows++
        }

        state.step shouldNotBe Step.DECLARE_ATTACKERS
        windows shouldBeLessThanOrEqual 10
    }

    test("declares no blockers when it cannot pay the block tax") {
        val driver = driver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Archangel of Tithes taxes every blocker {1} while it attacks; the Courser is what the AI
        // would otherwise want to block, since its own 5/5 eats it for free.
        val archangel = driver.putCreatureOnBattlefield(p1, "Archangel of Tithes")
        val courser = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        driver.removeSummoningSickness(archangel)
        driver.removeSummoningSickness(courser)
        val blocker = driver.putCreatureOnBattlefield(p2, "Force of Nature")
        driver.removeSummoningSickness(blocker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(archangel, courser), p2).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        val ai = AIPlayer.create(driver.cardRegistry, p2)
        val blockAction = LegalActionEnumerator.create(driver.cardRegistry).enumerate(driver.state, p2)
            .single { it.actionType == "DeclareBlockers" }
        val chosen = ai.chooseFrom(driver.state, listOf(blockAction)).action as DeclareBlockers

        chosen.blockers.filterValues { it.isNotEmpty() }.keys.shouldBeEmpty()
        driver.submit(chosen).isSuccess shouldBe true
        driver.state.pendingDecision shouldBe null
    }
})
