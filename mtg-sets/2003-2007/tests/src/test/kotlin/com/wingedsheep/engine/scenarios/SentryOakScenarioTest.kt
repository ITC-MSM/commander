package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SentryOak

class SentryOakScenarioTest : FunSpec({
    val boulder = card("Clash Boulder") { manaCost = "{5}"; typeLine = "Artifact" }
    val pebble = card("Clash Pebble") { manaCost = "{0}"; typeLine = "Artifact" }
    fun driver(win: Boolean = true): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(SentryOak, boulder, pebble))
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        putCardOnTopOfLibrary(player1, if (win) "Clash Boulder" else "Clash Pebble")
        putCardOnTopOfLibrary(player2, if (win) "Clash Pebble" else "Clash Boulder")
    }
    fun GameTestDriver.answerClash() {
        repeat(2) {
            val decision = pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            submitCardSelection(decision.playerId, emptyList()).error shouldBe null
        }
        pendingDecision shouldBe null
    }
    for (win in listOf(true, false)) {
        test("clash win=$win changes power and defender only until end of turn") {
            val d = driver(win)
            val oak = d.putCreatureOnBattlefield(d.player1, "Sentry Oak")
            d.passPriorityUntil(Step.BEGIN_COMBAT)
            d.stackSize shouldBe 1
            d.bothPass().error shouldBe null
            d.submitYesNo(d.player1, true).error shouldBe null
            d.answerClash()
            d.state.projectedState.getPower(oak) shouldBe if (win) 5 else 3
            d.state.projectedState.getToughness(oak) shouldBe 5
            d.state.projectedState.hasKeyword(oak, Keyword.DEFENDER) shouldBe !win
            d.passPriorityUntil(Step.UPKEEP)
            d.state.projectedState.getPower(oak) shouldBe 3
            d.state.projectedState.hasKeyword(oak, Keyword.DEFENDER) shouldBe true
            d.passPriorityUntil(Step.BEGIN_COMBAT)
            d.stackSize shouldBe 0
            d.pendingDecision shouldBe null
        }
    }
    test("declining leaves the creature and both libraries unchanged") {
        val d = driver()
        val oak = d.putCreatureOnBattlefield(d.player1, "Sentry Oak")
        val zones = d.state.zones
        d.passPriorityUntil(Step.BEGIN_COMBAT)
        d.bothPass().error shouldBe null
        d.submitYesNo(d.player1, false).error shouldBe null
        d.pendingDecision shouldBe null
        d.state.zones shouldBe zones
        d.state.projectedState.getPower(oak) shouldBe 3
        d.state.projectedState.hasKeyword(oak, Keyword.DEFENDER) shouldBe true
    }
    for (returnSource in listOf(false, true)) {
        test("old combat trigger ignores an absent or returned source: returns=$returnSource") {
            val d = driver()
            val blink = card("Oak Blink") {
                manaCost = "{U}"
                typeLine = "Instant"
                spell {
                    val target = target("creature", com.wingedsheep.sdk.dsl.Targets.Creature)
                    effect = if (returnSource) com.wingedsheep.sdk.dsl.Effects.Exile(target)
                        .then(com.wingedsheep.sdk.dsl.Effects.PutOntoBattlefield(target))
                    else com.wingedsheep.sdk.dsl.Effects.Exile(target)
                }
            }
            d.registerCards(listOf(blink))
            val oak = d.putCardInHand(d.player1, "Sentry Oak")
            d.giveMana(d.player1, Color.WHITE, 5)
            d.castSpell(d.player1, oak).error shouldBe null
            d.bothPass().error shouldBe null
            d.passPriorityUntil(Step.BEGIN_COMBAT)
            val flicker = d.putCardInHand(d.player1, "Oak Blink")
            d.giveMana(d.player1, Color.BLUE, 1)
            d.castSpell(d.player1, flicker, listOf(oak)).error shouldBe null
            d.bothPass().error shouldBe null
            d.bothPass().error shouldBe null
            d.submitYesNo(d.player1, true).error shouldBe null
            d.answerClash()
            (oak in d.state.getBattlefield()) shouldBe returnSource
            if (returnSource) {
                d.state.projectedState.getPower(oak) shouldBe 3
                d.state.projectedState.hasKeyword(oak, Keyword.DEFENDER) shouldBe true
            }
        }
    }

})
