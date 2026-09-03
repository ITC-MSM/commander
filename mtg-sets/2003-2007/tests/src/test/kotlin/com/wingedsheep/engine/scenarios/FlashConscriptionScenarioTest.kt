package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.FlashConscription
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Flash Conscription (RAV #124) — "Untap target creature and gain control of it until end of turn.
 * That creature gains haste until end of turn. If {W} was spent to cast this spell, the creature
 * gains 'Whenever this creature deals combat damage, you gain that much life' until end of turn."
 *
 * Two things are worth pinning. The threaten half must actually *untap* — a tapped blocker you
 * steal is worthless if the control grant alone is applied — and the haste must let it attack the
 * turn it changes hands. The white rider is a **payment** question, not a colour requirement: the
 * card is mono-red, so the same spell resolves with and without the life-gain depending only on
 * which mana paid for it. Both branches are cast here off the same board.
 */
class FlashConscriptionScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + FlashConscription)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /**
     * Cast Flash Conscription at [victim], paying its generic half with [white] white mana and the
     * rest in red. {5}{R} is six mana; the {R} is a real colour requirement, the rest is generic.
     */
    fun GameTestDriver.conscript(victim: EntityId, white: Int) {
        if (white > 0) giveMana(player1, Color.WHITE, white)
        giveMana(player1, Color.RED, 6 - white)
        val card = putCardInHand(player1, "Flash Conscription")
        castSpellWithTargets(player1, card, listOf(ChosenTarget.Permanent(victim))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("the stolen creature is untapped, changes hands, and can attack the same turn") {
        val d = driver()
        val opp = d.player2
        val guide = d.putCreatureOnBattlefield(opp, "Goblin Guide")   // 2/1, tapped and sick
        d.tapPermanent(guide)

        d.conscript(guide, white = 0)

        withClue("'untap target creature' is a real half of the spell, not flavour on the control grant") {
            d.isTapped(guide) shouldBe false
        }
        withClue("control change is a Layer-2 continuous effect — read it off projected state") {
            d.state.projectedState.getController(guide) shouldBe d.player1
        }

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        withClue("haste lets the freshly-stolen creature attack immediately") {
            d.declareAttackers(d.player1, listOf(guide), opp).error shouldBe null
        }
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opp).error shouldBe null
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        withClue("no white was spent, so no life came back off the 2 combat damage") {
            d.getLifeTotal(d.player1) shouldBe 20
            d.getLifeTotal(opp) shouldBe 18
        }
    }

    test("white mana in the payment attaches the lifelink-style rider to the stolen creature") {
        val d = driver()
        val opp = d.player2
        val guide = d.putCreatureOnBattlefield(opp, "Goblin Guide")   // 2/1

        d.conscript(guide, white = 1)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(guide), opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opp).error shouldBe null
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        withClue("the granted trigger pays its life to whoever controls the creature — me, this turn") {
            d.getLifeTotal(d.player1) shouldBe 22
            d.getLifeTotal(opp) shouldBe 18
        }
    }
})
