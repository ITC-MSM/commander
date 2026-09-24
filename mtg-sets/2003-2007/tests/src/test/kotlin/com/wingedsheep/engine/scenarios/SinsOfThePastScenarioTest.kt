package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.SinsOfThePast
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Sins of the Past — {4}{B}{B} Sorcery (Ravnica: City of Guilds #106)
 *
 * "Until end of turn, you may cast target instant or sorcery card from your graveyard without
 *  paying its mana cost. If that spell would be put into your graveyard, exile it instead.
 *  Exile Sins of the Past."
 */
class SinsOfThePastScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + SinsOfThePast)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.castSins(target: EntityId) {
        val sins = putCardInHand(player1, "Sins of the Past")
        giveMana(player1, Color.BLACK, 2)
        giveColorlessMana(player1, 4)
        castSpellWithTargets(player1, sins, listOf(ChosenTarget.Card(target, player1, Zone.GRAVEYARD)))
            .error shouldBe null
        bothPass()
        withClue("Exile Sins of the Past") {
            getExileCardNames(player1).contains("Sins of the Past") shouldBe true
            getGraveyardCardNames(player1).contains("Sins of the Past") shouldBe false
        }
    }

    fun GameTestDriver.canCastFromGraveyard(cardId: EntityId): Boolean =
        legalActions(player1).any { (it.action as? CastSpell)?.cardId == cardId && it.affordable }

    test("casts the instant from the graveyard for free, then exiles it") {
        val d = driver()
        val bolt = d.putCardInGraveyard(d.player1, "Lightning Bolt")
        d.castSins(bolt)

        withClue("the Bolt stays in the graveyard, castable") {
            (bolt in d.state.getGraveyard(d.player1)) shouldBe true
            d.canCastFromGraveyard(bolt) shouldBe true
        }

        d.castSpell(d.player1, bolt, listOf(d.player2)).error shouldBe null
        d.bothPass()

        withClue("the Bolt resolved without any mana") { d.getLifeTotal(d.player2) shouldBe 17 }
        withClue("it went to exile instead of the graveyard") {
            (bolt in d.state.getExile(d.player1)) shouldBe true
            (bolt in d.state.getGraveyard(d.player1)) shouldBe false
        }
    }

    test("a countered spell is exiled too, never returning to the graveyard") {
        val d = driver()
        val bolt = d.putCardInGraveyard(d.player1, "Lightning Bolt")
        d.castSins(bolt)

        d.castSpell(d.player1, bolt, listOf(d.player2)).error shouldBe null
        d.passPriority(d.player1)

        val counter = d.putCardInHand(d.player2, "Counterspell")
        d.giveMana(d.player2, Color.BLUE, 2)
        d.castSpellWithTargets(d.player2, counter, listOf(ChosenTarget.Spell(bolt))).error shouldBe null
        d.bothPass()

        d.getLifeTotal(d.player2) shouldBe 20
        withClue("the countered Bolt is exiled") {
            (bolt in d.state.getExile(d.player1)) shouldBe true
            (bolt in d.state.getGraveyard(d.player1)) shouldBe false
        }
    }

    test("the permission ends with the turn") {
        val d = driver()
        val bolt = d.putCardInGraveyard(d.player1, "Lightning Bolt")
        d.castSins(bolt)
        d.canCastFromGraveyard(bolt) shouldBe true

        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.UPKEEP)
        withClue("next turn the Bolt is just a graveyard card again") {
            (bolt in d.state.getGraveyard(d.player1)) shouldBe true
            d.state.mayPlayPermissions.any { bolt in it.cardIds } shouldBe false
        }
    }
})
