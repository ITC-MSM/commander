package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.Spawnbroker
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

class SpawnbrokerScenarioTest : FunSpec({
    val cards = TestCards.all + Spawnbroker
    fun driver(): GameTestDriver = GameTestDriver().also {
        it.registerCards(cards)
        it.initMirrorMatch(Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        it.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }
    fun GameTestDriver.castBroker(): EntityId {
        val card = putCardInHand(player1, "Spawnbroker")
        giveMana(player1, Color.BLUE, 3)
        castSpell(player1, card).error shouldBe null
        bothPass().error shouldBe null
        state.pendingDecision shouldNotBe null
        return card
    }
    fun GameTestDriver.legalTargets(): List<EntityId> =
        (state.pendingDecision as ChooseTargetsDecision).legalTargets.values.flatten()
    fun GameTestDriver.resolveExchange(accept: Boolean = true) {
        bothPass().error shouldBe null
        (state.pendingDecision is YesNoDecision) shouldBe true
        submitYesNo(player1, accept).error shouldBe null
    }
    fun GameTestDriver.controller(id: EntityId) = state.projectedState.getController(id)

    test("equal power can be exchanged and choices are restricted by the first target") {
        val d = driver()
        val yours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val theirs = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val larger = d.putCreatureOnBattlefield(d.player2, "Hill Giant")
        val broker = d.castBroker()
        d.legalTargets() shouldContain yours
        d.legalTargets() shouldNotContain broker
        d.submitTargetSelection(d.player1, listOf(yours)).error shouldBe null
        d.legalTargets() shouldBe listOf(theirs)
        d.submitTargetSelection(d.player1, listOf(larger)).isSuccess shouldBe false
        d.submitTargetSelection(d.player1, listOf(theirs)).error shouldBe null
        d.resolveExchange()
        d.controller(yours) shouldBe d.player2
        d.controller(theirs) shouldBe d.player1
    }

    test("the exchange may be declined after choosing both targets") {
        val d = driver()
        val theirs = d.putCreatureOnBattlefield(d.player2, "Llanowar Elves")
        val broker = d.castBroker()
        d.submitTargetSelection(d.player1, listOf(broker)).error shouldBe null
        d.submitTargetSelection(d.player1, listOf(theirs)).error shouldBe null
        d.resolveExchange(accept = false)
        d.controller(broker) shouldBe d.player1
        d.controller(theirs) shouldBe d.player2
    }

    test("a power increase in response prevents the whole exchange") {
        val d = driver()
        val yours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val theirs = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.castBroker()
        d.submitTargetSelection(d.player1, listOf(yours)).error shouldBe null
        d.submitTargetSelection(d.player1, listOf(theirs)).error shouldBe null
        val growth = d.putCardInHand(d.player1, "Giant Growth")
        d.giveMana(d.player1, Color.GREEN)
        d.castSpell(d.player1, growth, listOf(theirs)).error shouldBe null
        d.bothPass().error shouldBe null
        // One target remains legal, so the optional effect still resolves, but cannot exchange.
        d.resolveExchange()
        d.controller(yours) shouldBe d.player1
        d.controller(theirs) shouldBe d.player2
    }

    test("a projected power bonus qualifies a creature while choosing targets") {
        val d = driver()
        val yours = d.putCreatureOnBattlefield(d.player1, "Llanowar Elves")
        val growth = d.putCardInHand(d.player1, "Giant Growth")
        d.giveMana(d.player1, Color.GREEN)
        d.castSpell(d.player1, growth, listOf(yours)).error shouldBe null
        d.bothPass().error shouldBe null
        val theirs = d.putCreatureOnBattlefield(d.player2, "Hill Giant")
        d.castBroker()
        d.legalTargets() shouldContain yours
        d.submitTargetSelection(d.player1, listOf(yours)).error shouldBe null
        d.legalTargets() shouldContain theirs
        d.submitTargetSelection(d.player1, listOf(theirs)).error shouldBe null
        d.resolveExchange()
        d.controller(yours) shouldBe d.player2
        d.controller(theirs) shouldBe d.player1
    }

    test("removing either target prevents the exchange without failing resolution") {
        for (removeYours in listOf(true, false)) {
            val d = driver()
            val yours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            val theirs = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
            d.castBroker()
            d.submitTargetSelection(d.player1, listOf(yours)).error shouldBe null
            d.submitTargetSelection(d.player1, listOf(theirs)).error shouldBe null
            val bounce = d.putCardInHand(d.player1, "Unsummon")
            d.giveMana(d.player1, Color.BLUE)
            d.castSpell(d.player1, bounce, listOf(if (removeYours) yours else theirs)).error shouldBe null
            d.bothPass().error shouldBe null
            d.resolveExchange()
            d.controller(if (removeYours) theirs else yours) shouldBe if (removeYours) d.player2 else d.player1
            d.state.stack shouldBe emptyList()
        }
    }

    test("the exchange survives its source leaving when both targets remain legal") {
        val d = driver()
        val yours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val theirs = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val broker = d.castBroker()
        d.submitTargetSelection(d.player1, listOf(yours)).error shouldBe null
        d.submitTargetSelection(d.player1, listOf(theirs)).error shouldBe null
        val bounce = d.putCardInHand(d.player1, "Unsummon")
        d.giveMana(d.player1, Color.BLUE)
        d.castSpell(d.player1, bounce, listOf(broker)).error shouldBe null
        d.bothPass().error shouldBe null
        d.resolveExchange()
        d.controller(yours) shouldBe d.player2
        d.controller(theirs) shouldBe d.player1
    }

    test("no trigger is put on the stack if no legal pair exists") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player2, "Hill Giant")
        val card = d.putCardInHand(d.player1, "Spawnbroker")
        d.giveMana(d.player1, Color.BLUE, 3)
        d.castSpell(d.player1, card).error shouldBe null
        d.bothPass().error shouldBe null
        d.state.pendingDecision shouldBe null
        d.state.stack shouldBe emptyList()
    }
})
