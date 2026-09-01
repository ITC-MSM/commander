package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.EtherealWhiskergill
import com.wingedsheep.mtg.sets.definitions.lrw.cards.IncandescentSoulstoke
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Incandescent Soulstoke (LRW #178) — "Other Elemental creatures you control get +1/+1.
 * {1}{R}, {T}: You may put an Elemental creature card from your hand onto the battlefield. That
 * creature gains haste until end of turn. Sacrifice it at the beginning of the next end step."
 *
 * Three claims worth pinning, and each of them is a place a plausible mis-wiring would still look
 * right on the card: the lord must skip the Soulstoke itself and every Elemental the opponent
 * controls; the "you may" must be genuinely declinable without the delayed sacrifice firing on
 * nothing; and the creature that *was* cheated in has to still be remembered when the end step
 * arrives, long after the activation's context is gone.
 */
class IncandescentSoulstokeScenarioTest : FunSpec({

    val stokeAbility = IncandescentSoulstoke.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(IncandescentSoulstoke, EtherealWhiskergill))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the lord pumps other Elementals you control, not itself and not theirs") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)

        val stoke = d.putCreatureOnBattlefield(me, "Incandescent Soulstoke")
        val mine = d.putCreatureOnBattlefield(me, "Ethereal Whiskergill")
        val theirs = d.putCreatureOnBattlefield(opponent, "Ethereal Whiskergill")

        withClue("a 4/3 Elemental you control becomes 5/4") {
            d.state.projectedState.getPower(mine) shouldBe 5
            d.state.projectedState.getToughness(mine) shouldBe 4
        }
        withClue("\"other\" excludes the Soulstoke itself") {
            d.state.projectedState.getPower(stoke) shouldBe 2
            d.state.projectedState.getToughness(stoke) shouldBe 2
        }
        withClue("\"you control\" excludes the opponent's Elemental") {
            d.state.projectedState.getPower(theirs) shouldBe 4
        }
    }

    test("it cheats an Elemental in with haste, and sacrifices it at the next end step") {
        val d = driver()
        val me = d.activePlayer!!

        val stoke = d.putCreatureOnBattlefield(me, "Incandescent Soulstoke")
        d.removeSummoningSickness(stoke)
        val whiskergill = d.putCardInHand(me, "Ethereal Whiskergill")
        d.giveMana(me, Color.RED, 2)

        d.submit(ActivateAbility(me, stoke, stokeAbility)).isSuccess shouldBe true
        d.bothPass()
        d.submitCardSelection(me, listOf(whiskergill))

        val inPlay = d.findPermanent(me, "Ethereal Whiskergill").shouldNotBeNull()
        withClue("\"gains haste until end of turn\"") {
            d.state.projectedState.hasKeyword(inPlay, Keyword.HASTE) shouldBe true
        }

        d.passPriorityUntil(Step.END)
        d.bothPass()

        d.findPermanent(me, "Ethereal Whiskergill") shouldBe null
        d.getGraveyard(me) shouldContain whiskergill
    }

    test("declining the optional put leaves the card in hand and sacrifices nothing") {
        val d = driver()
        val me = d.activePlayer!!

        val stoke = d.putCreatureOnBattlefield(me, "Incandescent Soulstoke")
        d.removeSummoningSickness(stoke)
        val whiskergill = d.putCardInHand(me, "Ethereal Whiskergill")
        d.giveMana(me, Color.RED, 2)

        d.submit(ActivateAbility(me, stoke, stokeAbility)).isSuccess shouldBe true
        d.bothPass()
        d.submitCardSelection(me, emptyList())

        d.findPermanent(me, "Ethereal Whiskergill") shouldBe null
        d.getHand(me) shouldContain whiskergill

        d.passPriorityUntil(Step.END)
        d.bothPass()

        withClue("nothing was put onto the battlefield, so nothing is sacrificed") {
            d.getHand(me) shouldContain whiskergill
            d.getGraveyard(me).contains(whiskergill) shouldBe false
        }
    }
})
