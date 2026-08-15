package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.KaraiFutureOfTheFoot
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Karai, Future of the Foot (TMT #151) — "Whenever Karai deals combat damage to a player, return
 * target creature card from your graveyard to your hand. If her sneak cost was paid this turn,
 * instead return that card to the battlefield."
 *
 * Both branches of the trigger's [com.wingedsheep.sdk.scripting.effects.ConditionalEffect] are
 * exercised: the default (cast normally → return to hand) and the "instead" branch (cast for her
 * sneak cost → put onto the battlefield, since a creature can only be put onto the battlefield by
 * its sneak cost on the very turn it's sneaked in, exactly when the ANDed `SourceEnteredThisTurn`
 * also holds).
 */
class KaraiFutureOfTheFootTest : FunSpec({

    val bear = card("Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    // A vanilla creature that declares as the unblocked attacker returned to pay Karai's sneak cost.
    val brawler = card("Plain Brawler") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    test("combat damage returns a creature card from your graveyard to hand when not sneaked") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(KaraiFutureOfTheFoot, bear))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val karai = driver.putCreatureOnBattlefield(player, "Karai, Future of the Foot")
        driver.removeSummoningSickness(karai)
        val bearInGy = driver.putCardInGraveyard(player, "Test Bear")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(karai), opponent)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, emptyMap())

        // Combat damage fires the trigger, which pauses to choose the graveyard creature card.
        var guard = 0
        while (driver.state.step != Step.POSTCOMBAT_MAIN && guard++ < 40) {
            val decision = driver.pendingDecision
            val holder = driver.state.priorityPlayerId
            if (decision is ChooseTargetsDecision) {
                driver.submitTargetSelection(decision.playerId, listOf(bearInGy))
            } else if (driver.state.stack.isNotEmpty()) {
                driver.bothPass()
            } else if (holder != null) {
                driver.passPriority(holder)
            } else {
                break
            }
        }

        // Not sneaked → the Bear returns to hand, not the battlefield.
        driver.getHand(player) shouldContain bearInGy
        driver.findPermanent(player, "Test Bear") shouldBe null
    }

    test("combat damage returns the creature card to the battlefield when Karai was sneaked in") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(KaraiFutureOfTheFoot, bear, brawler))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        // The Brawler provides the unblocked attacker returned to pay Karai's sneak cost; the Bear
        // waits in the graveyard for the reanimation branch.
        val attacker = driver.putCreatureOnBattlefield(player, "Plain Brawler")
        driver.removeSummoningSickness(attacker)
        val karai = driver.putCardInHand(player, "Karai, Future of the Foot")
        val bearInGy = driver.putCardInGraveyard(player, "Test Bear")

        // Open the sneak window: declare the Brawler, leave it unblocked.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(attacker), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, emptyMap()).isSuccess shouldBe true
        var pg = 0
        while (driver.state.priorityPlayerId != null && driver.state.priorityPlayerId != player &&
            driver.state.step == Step.DECLARE_BLOCKERS && pg++ < 4
        ) {
            driver.passPriority(driver.state.priorityPlayerId!!)
        }

        // Cast Karai for her sneak cost {2}{W}{B}, returning the unblocked Brawler. She enters
        // tapped and attacking the opponent.
        driver.giveMana(player, Color.WHITE, 2)
        driver.giveMana(player, Color.BLACK, 2)
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = karai,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.SNEAK,
                additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(attacker)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Karai deals combat damage the same turn she's sneaked in, firing the trigger; choose the
        // graveyard Bear as the target.
        var guard = 0
        while (driver.state.step != Step.POSTCOMBAT_MAIN && guard++ < 40) {
            val decision = driver.pendingDecision
            val holder = driver.state.priorityPlayerId
            if (decision is ChooseTargetsDecision) {
                driver.submitTargetSelection(decision.playerId, listOf(bearInGy))
            } else if (driver.state.stack.isNotEmpty()) {
                driver.bothPass()
            } else if (holder != null) {
                driver.passPriority(holder)
            } else {
                break
            }
        }

        // Sneak cost was paid this turn → the Bear is put onto the battlefield, not returned to hand.
        driver.findPermanent(player, "Test Bear") shouldNotBe null
        driver.getHand(player).contains(bearInGy) shouldBe false
    }
})
