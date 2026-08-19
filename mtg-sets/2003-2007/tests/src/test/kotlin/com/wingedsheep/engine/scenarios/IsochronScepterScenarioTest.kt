package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.IsochronScepter
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Isochron Scepter (MRD #188) — "Imprint — When this artifact enters, you may exile an instant card
 * with mana value 2 or less from your hand. {2}, {T}: You may copy the exiled card. If you do, you
 * may cast the copy without paying its mana cost."
 *
 * The point of the card is that the imprinted card *stays* exiled: the ability copies it rather
 * than casting it, so the same Lightning Bolt is available again next turn. These tests pin that,
 * plus the two ways the linked-exile pile can be empty — a declined imprint, and a hand with
 * nothing legal in it.
 */
class IsochronScepterScenarioTest : FunSpec({

    val scepterAbility = IsochronScepter.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + IsochronScepter)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Cast the Scepter from hand so the imprint trigger actually fires, and return it. */
    fun GameTestDriver.castScepter(): EntityId {
        val inHand = putCardInHand(player1, "Isochron Scepter")
        giveColorlessMana(player1, 2)
        castSpell(player1, inHand).error shouldBe null
        // One pass resolves the artifact, the next resolves the imprint trigger it put on the stack.
        repeat(4) { if (state.pendingDecision == null) bothPass() }
        return findPermanent(player1, "Isochron Scepter")!!
    }

    test("imprints a Lightning Bolt and casts a free copy without spending the exiled card") {
        val d = driver()
        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        // Counterspell ({U}{U}, mana value 2) is also legal, so the imprint is a real choice.
        d.putCardInHand(d.player1, "Counterspell")
        val scepter = d.castScepter()

        withClue("the imprint is a 'may', so it asks first") {
            d.submitYesNo(d.player1, true).error shouldBe null
        }
        d.submitCardSelection(d.player1, listOf(bolt)).error shouldBe null
        d.getExileCardNames(d.player1) shouldBe listOf("Lightning Bolt")

        d.giveColorlessMana(d.player1, 2)
        d.submit(ActivateAbility(d.player1, scepter, scepterAbility)).error shouldBe null
        d.bothPass()
        d.submitYesNo(d.player1, true).error shouldBe null
        d.submitTargetSelection(d.player1, listOf(d.player2)).error shouldBe null
        d.bothPass()

        withClue("the copy resolved for free") {
            d.getLifeTotal(d.player2) shouldBe 17
        }
        withClue("the imprinted card is still exiled — the Scepter copies, it doesn't spend") {
            d.getExileCardNames(d.player1) shouldBe listOf("Lightning Bolt")
        }
        withClue("only the copy was cast, so the real Bolt never hit the graveyard") {
            d.getGraveyardCardNames(d.player1).contains("Lightning Bolt") shouldBe false
        }
    }

    test("declining the imprint leaves the Scepter with nothing to copy") {
        val d = driver()
        d.putCardInHand(d.player1, "Lightning Bolt")
        val scepter = d.castScepter()

        d.submitYesNo(d.player1, false).error shouldBe null
        d.getExileCardNames(d.player1) shouldBe emptyList()

        d.giveColorlessMana(d.player1, 2)
        d.submit(ActivateAbility(d.player1, scepter, scepterAbility)).error shouldBe null
        d.bothPass()
        d.submitYesNo(d.player1, true).error shouldBe null

        withClue("an empty linked-exile pile copies nothing and casts nothing") {
            d.state.pendingDecision shouldBe null
            d.getLifeTotal(d.player2) shouldBe 20
        }
    }

    test("a hand with no instant of mana value 2 or less imprints nothing") {
        val d = driver()
        // A creature card, not an instant — every other card in hand is a basic land.
        val bears = d.putCardInHand(d.player1, "Grizzly Bears")
        d.castScepter()

        d.submitYesNo(d.player1, true).error shouldBe null

        withClue("there were no legal candidates, so nothing was exiled") {
            d.getExileCardNames(d.player1) shouldBe emptyList()
            d.findCardInHand(d.player1, "Grizzly Bears") shouldBe bears
        }
    }
})
