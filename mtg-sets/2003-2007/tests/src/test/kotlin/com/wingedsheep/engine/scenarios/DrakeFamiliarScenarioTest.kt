package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.DrakeFamiliar
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Drake Familiar (RAV #44) — "When this creature enters, sacrifice it unless you return an
 * enchantment to its owner's hand."
 *
 * The 2005-10-01 ruling is the whole test: *any* enchantment on the battlefield qualifies, an
 * opponent's included, and the ability doesn't target, so an untargetable one would too. The cost
 * atom's `youControl` axis is what carries that — before it, the bounce pool was hardcoded to
 * permanents the payer controls. The other half of the same ruling is the decline: with no
 * enchantments, or with the choice refused, Drake Familiar is sacrificed.
 */
class DrakeFamiliarScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + DrakeFamiliar)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Casts Drake Familiar and resolves it up to the enters-the-battlefield decision, if any. */
    fun GameTestDriver.castDrake(caster: EntityId): EntityId {
        giveMana(caster, Color.BLUE, 2)
        val card = putCardInHand(caster, "Drake Familiar")
        castSpell(caster, card).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && pendingDecision == null && guard++ < 10) bothPass()
        return card
    }

    fun GameTestDriver.handOf(playerId: EntityId): List<EntityId> = state.getZone(ZoneKey(playerId, Zone.HAND))

    test("an opponent's enchantment is a legal payment, and it goes to its owner's hand") {
        val d = driver()
        val me = d.player1
        val opp = d.getOpponent(me)

        val theirs = d.putPermanentOnBattlefield(opp, "Test Enchantment")

        val drake = d.castDrake(me)
        val decision = d.pendingDecision as? SelectCardsDecision
            ?: error("Expected a bounce selection from Drake Familiar")

        withClue("the opponent's enchantment is offered — the ability is control-agnostic") {
            decision.options.contains(theirs) shouldBe true
        }

        d.submitCardSelection(me, listOf(theirs))
        var guard = 0
        while (d.stackSize > 0 && guard++ < 10) d.bothPass()

        withClue("Drake Familiar survives — the cost was paid") {
            d.state.getBattlefield().contains(drake) shouldBe true
        }
        withClue("the enchantment goes to its *owner's* hand, not the payer's") {
            d.state.getBattlefield().contains(theirs) shouldBe false
            d.handOf(opp).contains(theirs) shouldBe true
            d.handOf(me).contains(theirs) shouldBe false
        }
    }

    test("declining the payment sacrifices Drake Familiar") {
        val d = driver()
        val me = d.player1

        val mine = d.putPermanentOnBattlefield(me, "Test Enchantment")

        val drake = d.castDrake(me)
        d.pendingDecision as? SelectCardsDecision
            ?: error("Expected a bounce selection from Drake Familiar")

        // Selecting nothing is the decline — "if you choose not to return one, you must sacrifice it".
        d.submitCardSelection(me, emptyList())
        var guard = 0
        while (d.stackSize > 0 && guard++ < 10) d.bothPass()

        withClue("Drake Familiar is sacrificed") {
            d.state.getBattlefield().contains(drake) shouldBe false
            d.getGraveyardCardNames(me).contains("Drake Familiar") shouldBe true
        }
        withClue("the enchantment the player declined to return is untouched") {
            d.state.getBattlefield().contains(mine) shouldBe true
        }
    }

    test("with no enchantment on the battlefield there is no prompt at all — it is just sacrificed") {
        val d = driver()
        val me = d.player1

        val drake = d.castDrake(me)

        withClue("nothing legal to return, so the suffer half runs without asking") {
            d.pendingDecision shouldBe null
        }
        var guard = 0
        while (d.stackSize > 0 && guard++ < 10) d.bothPass()

        d.state.getBattlefield().contains(drake) shouldBe false
        d.getGraveyardCardNames(me).contains("Drake Familiar") shouldBe true
    }
})
