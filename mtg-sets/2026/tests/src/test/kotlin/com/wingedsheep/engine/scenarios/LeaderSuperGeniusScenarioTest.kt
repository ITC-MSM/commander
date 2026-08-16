package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.LeaderSuperGenius
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Leader, Super-Genius (MSH #64).
 *
 * "If a creature you control would connive, instead you draw a card, then that creature connives."
 * + "At the beginning of combat on your turn, target creature you control connives."
 *
 * The discriminating observation is the *hand count at the moment the discard is chosen*: the
 * replacement's draw and connive's own draw are both in hand before the player picks a card to
 * discard, so the pending discard decision sees hand + 2. A "whenever a creature you control
 * connives, draw a card" trigger — the tempting wrong model — would leave hand + 1 there and only
 * catch up afterwards, and would use the stack. Net across the whole connive: +2 drawn, −1
 * discarded.
 *
 * The mechanism itself (any connive source, filter scoping, stacking, the connive event) is covered
 * by `ConniveReplacementTest`; this file is about the printed card.
 */
class LeaderSuperGeniusScenarioTest : FunSpec({

    fun plusOne(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LeaderSuperGenius))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Runs the beginning-of-combat trigger: targets [conniver], discards [toDiscard], and returns
     * the hand size seen at the discard decision (i.e. after both draws). Nothing draws between
     * the precombat main phase and the trigger, so the caller's pre-combat hand size is the
     * baseline to compare against.
     */
    fun runCombatTrigger(
        driver: GameTestDriver,
        player: EntityId,
        conniver: EntityId,
        toDiscard: EntityId
    ): Int {
        driver.passPriorityUntil(Step.BEGIN_COMBAT)

        var handAtDiscard = -1
        var guard = 0
        while (guard++ < 60) {
            val decision = driver.pendingDecision
            when {
                decision is ChooseTargetsDecision ->
                    driver.submitTargetSelection(player, listOf(conniver))

                decision is SelectCardsDecision -> {
                    handAtDiscard = driver.getHandSize(player)
                    driver.submitDecision(
                        player,
                        CardsSelectedResponse(
                            decisionId = decision.id,
                            selectedCards = listOf(toDiscard)
                        )
                    )
                }

                decision != null -> driver.autoResolveDecision()
                driver.state.stack.isNotEmpty() -> driver.bothPass()
                else -> break
            }
        }
        return handAtDiscard
    }

    test("the combat trigger connives the target, and the replacement draws a card first") {
        val driver = setup()
        val player = driver.activePlayer!!
        driver.putCreatureOnBattlefield(player, "Leader, Super-Genius")
        val bear = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val nonland = driver.putCardInHand(player, "Grizzly Bears")
        val handBefore = driver.getHandSize(player)

        val handAtDiscard = runCombatTrigger(driver, player, bear, nonland)

        withClue("replacement draw + connive draw are both in hand before the discard is chosen") {
            handAtDiscard shouldBe handBefore + 2
        }
        // Net: two drawn, one discarded.
        driver.getHandSize(player) shouldBe handBefore + 1
        driver.getGraveyard(player).contains(nonland) shouldBe true
        // A nonland was discarded, so the +1/+1 counter lands on the conniving creature (CR 701.50a),
        // not on Leader.
        plusOne(driver, bear) shouldBe 1
    }

    test("discarding a land still draws both cards but places no counter") {
        val driver = setup()
        val player = driver.activePlayer!!
        driver.putCreatureOnBattlefield(player, "Leader, Super-Genius")
        val bear = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val land = driver.putCardInHand(player, "Island")
        val handBefore = driver.getHandSize(player)

        val handAtDiscard = runCombatTrigger(driver, player, bear, land)

        handAtDiscard shouldBe handBefore + 2
        driver.getGraveyard(player).contains(land) shouldBe true
        plusOne(driver, bear) shouldBe 0
    }

    test("Leader can connive itself, taking the counter") {
        val driver = setup()
        val player = driver.activePlayer!!
        val leader = driver.putCreatureOnBattlefield(player, "Leader, Super-Genius")
        val nonland = driver.putCardInHand(player, "Grizzly Bears")
        val handBefore = driver.getHandSize(player)

        val handAtDiscard = runCombatTrigger(driver, player, leader, nonland)

        handAtDiscard shouldBe handBefore + 2
        plusOne(driver, leader) shouldBe 1
    }
})
