package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.MichelangeloImproviser
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Michelangelo, Improviser (TMT #119) — "Whenever Michelangelo deals combat damage to a player,
 * you may put a creature card and/or a land card from your hand onto the battlefield."
 *
 * Puts both a creature and a land from hand onto the battlefield via two independent up-to-one
 * selections.
 */
class MichelangeloImproviserTest : FunSpec({

    val bear = card("Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    test("combat damage puts a creature and a land from hand onto the battlefield") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MichelangeloImproviser, bear))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val mike = driver.putCreatureOnBattlefield(player, "Michelangelo, Improviser")
        driver.removeSummoningSickness(mike)
        driver.putCardInHand(player, "Test Bear") // the creature; opening hand supplies the land

        fun landsInPlay() = driver.getPermanents(player).count {
            driver.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                ?.name == "Forest"
        }
        val landsBefore = landsInPlay()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(mike), opponent)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, emptyMap())

        var guard = 0
        while (driver.state.step != Step.POSTCOMBAT_MAIN && guard++ < 40) {
            val decision = driver.pendingDecision
            val holder = driver.state.priorityPlayerId
            if (decision is SelectCardsDecision) {
                // Select the single available card for this up-to-one slot (creature, then land).
                driver.submitCardSelection(decision.playerId, decision.options.take(1))
            } else if (driver.state.stack.isNotEmpty()) {
                driver.bothPass()
            } else if (holder != null) {
                driver.passPriority(holder)
            } else {
                break
            }
        }

        driver.findPermanent(player, "Test Bear") shouldNotBe null
        landsInPlay() shouldBe landsBefore + 1
    }
})
