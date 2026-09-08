package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.mtg.sets.definitions.rav.cards.CircuDimirLobotomist
import com.wingedsheep.mtg.sets.definitions.rav.cards.DimirCutpurse
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Circu, Dimir Lobotomist (RAV #196) — two "whenever you cast a blue/black spell" triggers that
 * exile the top card of target player's library, plus "Your opponents can't cast spells with the
 * same name as a card exiled with Circu."
 *
 * The new vocabulary is `CardPredicate.SharesNameWithLinkedExile` — the *pile-wide* name predicate,
 * the name axis of `SharesCardTypeWithLinkedExile` (Cemetery Illuminator). What these tests pin:
 *
 * - the pile is read **whole**, not at one index: a second exile has to lock out a second name
 *   while the first stays locked (an `EntityReference.LinkedExiledCard()` index could only ever
 *   name one of them);
 * - the prohibition is **opponents-only**, per the card's own second ruling — the controller can
 *   still cast a same-named card;
 * - a blue *and* black spell fires **both** triggers, per the first ruling;
 * - the lock is tied to *this* Circu: it lifts when the permanent leaves the battlefield.
 *
 * The last one is also the regression that matters for the engine change: the prohibition is read
 * through `CastPermissionUtils.blockedByPlayersCantCastSpells`, which had been building its
 * `PredicateContext` with no `sourceId` at all — so a source-relative spell filter in a
 * `PlayersCantCastSpells` static could only ever fail closed.
 */
class CircuDimirLobotomistScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + CircuDimirLobotomist + DimirCutpurse)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /**
     * Casts [spellName] from [caster]'s hand and points Circu's resulting trigger at [victim],
     * then drains the stack. The trigger's target is a player and there are two legal ones, so the
     * decision is always raised rather than auto-selected.
     */
    fun GameTestDriver.castAndExileTopOf(caster: EntityId, spellName: String, victim: EntityId) {
        val card = putCardInHand(caster, spellName)
        giveMana(caster, Color.BLUE, 2)
        giveMana(caster, Color.BLACK, 2)
        giveMana(caster, Color.RED, 2)
        giveColorlessMana(caster, 2)
        castSpell(caster, card).error shouldBe null

        var guard = 0
        while (guard++ < 20) {
            val decision = pendingDecision
            if (decision is ChooseTargetsDecision) {
                submitTargetSelection(decision.playerId, listOf(victim))
                continue
            }
            if (decision != null) error("unexpected decision: $decision")
            if (stackSize == 0) break
            bothPass()
        }
    }

    test("a card exiled with Circu can't be cast by an opponent, but can by its controller") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        d.putPermanentOnBattlefield(me, "Circu, Dimir Lobotomist")
        d.putCardOnTopOfLibrary(opp, "Lightning Bolt")

        d.castAndExileTopOf(me, "Phantom Warrior", opp)

        withClue("the opponent's top card was exiled") {
            d.getExileCardNames(opp) shouldBe listOf("Lightning Bolt")
        }

        // The opponent holds their own copy of the exiled card. Lightning Bolt is an instant, so
        // sorcery timing isn't what's stopping them — only the prohibition is.
        val oppBolt = d.putCardInHand(opp, "Lightning Bolt")
        val oppGrowth = d.putCardInHand(opp, "Giant Growth")
        d.giveMana(opp, Color.RED, 3)
        d.giveMana(opp, Color.GREEN, 3)

        val blocked = d.submit(
            CastSpell(
                playerId = opp,
                cardId = oppBolt,
                targets = listOf(ChosenTarget.Player(me)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        withClue("an opponent can't cast a spell sharing a name with a card exiled with Circu") {
            (blocked.error != null) shouldBe true
        }

        withClue("a differently-named spell is untouched") {
            d.legalActions(opp).any { legal ->
                val action = legal.action
                action is CastSpell && action.cardId == oppGrowth
            } shouldBe true
        }
        withClue("the blocked spell is not even offered") {
            d.legalActions(opp).any { legal ->
                val action = legal.action
                action is CastSpell && action.cardId == oppBolt
            } shouldBe false
        }

        // "…applies to all of its controller's opponents, not just the owner of the exiled card"
        // — and, by the same token, not to the controller.
        val myBolt = d.putCardInHand(me, "Lightning Bolt")
        d.giveMana(me, Color.RED, 3)
        withClue("the controller is not affected by their own Circu") {
            d.castSpell(me, myBolt, listOf(opp)).error shouldBe null
        }
    }

    test("the pile is read whole — a second exile locks a second name without unlocking the first") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        d.putPermanentOnBattlefield(me, "Circu, Dimir Lobotomist")
        d.putCardOnTopOfLibrary(opp, "Lightning Bolt")

        d.castAndExileTopOf(me, "Phantom Warrior", opp)
        // Second exile, a different name. An index-based "the card exiled with ~" would now name
        // only one of the two.
        d.putCardOnTopOfLibrary(opp, "Counterspell")
        d.castAndExileTopOf(me, "Black Creature", opp)

        withClue("both cards are in the linked pile") {
            d.getExileCardNames(opp).toSet() shouldBe setOf("Lightning Bolt", "Counterspell")
        }

        val oppBolt = d.putCardInHand(opp, "Lightning Bolt")
        val oppCounter = d.putCardInHand(opp, "Counterspell")
        d.giveMana(opp, Color.RED, 3)
        d.giveMana(opp, Color.BLUE, 3)

        val offered = d.legalActions(opp)
            .mapNotNull { (it.action as? CastSpell)?.cardId }
        withClue("the first exiled name is still locked") { offered.contains(oppBolt) shouldBe false }
        withClue("the second exiled name is locked too") { offered.contains(oppCounter) shouldBe false }
    }

    test("a blue and black spell fires both triggers, and each picks its own target") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        d.putPermanentOnBattlefield(me, "Circu, Dimir Lobotomist")
        d.putCardOnTopOfLibrary(opp, "Lightning Bolt")
        d.putCardOnTopOfLibrary(me, "Giant Growth")

        // Dimir Cutpurse is {1}{U}{B} — blue *and* black, so both triggers go on the stack. They
        // are pointed at different players, which is the printed ruling's other half. (A second
        // Circu would do the same job and then drag the legend rule into the test.)
        val card = d.putCardInHand(me, "Dimir Cutpurse")
        d.giveMana(me, Color.BLUE, 2)
        d.giveMana(me, Color.BLACK, 2)
        d.giveColorlessMana(me, 2)
        d.castSpell(me, card).error shouldBe null

        val victims = mutableListOf(opp, me)
        var guard = 0
        while (guard++ < 20) {
            val decision = d.pendingDecision
            if (decision is ChooseTargetsDecision) {
                d.submitTargetSelection(decision.playerId, listOf(victims.removeAt(0)))
                continue
            }
            if (decision != null) error("unexpected decision: $decision")
            if (d.stackSize == 0) break
            d.bothPass()
        }

        withClue("both triggers resolved — one exile in each library's owner's exile zone") {
            d.getExileCardNames(opp) shouldBe listOf("Lightning Bolt")
            d.getExileCardNames(me) shouldBe listOf("Giant Growth")
        }
    }

    test("the lock lifts when Circu leaves the battlefield") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        val circu = d.putPermanentOnBattlefield(me, "Circu, Dimir Lobotomist")
        d.putCardOnTopOfLibrary(opp, "Lightning Bolt")
        d.castAndExileTopOf(me, "Phantom Warrior", opp)

        val oppBolt = d.putCardInHand(opp, "Lightning Bolt")
        d.giveMana(opp, Color.RED, 3)
        withClue("locked while Circu is out") {
            d.legalActions(opp).any { legal ->
                val action = legal.action
                action is CastSpell && action.cardId == oppBolt
            } shouldBe false
        }

        d.moveToGraveyard(circu)

        withClue("the static is gone with the permanent that printed it") {
            d.legalActions(opp).any { legal ->
                val action = legal.action
                action is CastSpell && action.cardId == oppBolt
            } shouldBe true
        }
    }
})
