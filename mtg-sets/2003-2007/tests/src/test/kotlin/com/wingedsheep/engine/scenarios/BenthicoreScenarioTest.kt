package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Benthicore
import com.wingedsheep.mtg.sets.definitions.lrw.cards.InkfathomDivers
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Benthicore (LRW #53) — "When this creature enters, create two 1/1 blue Merfolk Wizard creature
 * tokens. Tap two untapped Merfolk you control: Untap this creature. It gains shroud until end of
 * turn."
 *
 * The ability has **no `{T}`** of its own — the two Merfolk are the whole cost — so a tapped
 * Benthicore untapping itself is the defining case, and it is the one a `{T}`-flavoured
 * mis-transcription would break. The "untapped" and "you control" halves of the cost are the
 * engine's (`controlledUntapped`), not the filter's, so both are checked here against the two
 * shapes that would otherwise pay: three-quarters of a cost, and an already-tapped Merfolk.
 */
class BenthicoreScenarioTest : FunSpec({

    val untapAbility = Benthicore.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(Benthicore, InkfathomDivers))
        d.initMirrorMatch(deck = Deck.of("Island" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun activate(d: GameTestDriver, player: EntityId, source: EntityId, tapping: List<EntityId>) =
        d.submit(
            ActivateAbility(
                playerId = player,
                sourceId = source,
                abilityId = untapAbility,
                costPayment = AdditionalCostPayment(tappedPermanents = tapping),
            )
        )

    test("it enters with two 1/1 blue Merfolk Wizard tokens — exactly the two its ability wants") {
        val d = driver()
        val me = d.activePlayer!!

        val card = d.putCardInHand(me, "Benthicore")
        d.giveMana(me, Color.BLUE, 7)
        d.castSpell(me, card).isSuccess shouldBe true
        // One pass resolves the spell; the enters trigger then needs its own.
        var guard = 0
        while (guard++ < 10 && d.stackSize > 0) d.bothPass()

        val tokens = d.getCreatures(me).filter { d.getCardName(it) == "Merfolk Wizard Token" }
        tokens.size shouldBe 2
        tokens.forEach {
            d.state.projectedState.getPower(it) shouldBe 1
            d.state.projectedState.getToughness(it) shouldBe 1
        }

        val benthicore = d.findPermanent(me, "Benthicore").shouldNotBeNull()
        withClue("the two tokens can pay for it right away — tapping as a cost ignores summoning sickness") {
            activate(d, me, benthicore, tokens).isSuccess shouldBe true
        }
    }

    test("tapping two Merfolk untaps Benthicore and gives it shroud until end of turn") {
        val d = driver()
        val me = d.activePlayer!!

        val benthicore = d.putCreatureOnBattlefield(me, "Benthicore")
        d.tapPermanent(benthicore)
        val divers = List(2) { d.putCreatureOnBattlefield(me, "Inkfathom Divers") }

        activate(d, me, benthicore, divers).isSuccess shouldBe true
        d.bothPass()

        divers.forEach { d.isTapped(it) shouldBe true }
        withClue("no {T} in the cost, so a tapped Benthicore can untap itself") {
            d.isTapped(benthicore) shouldBe false
        }
        d.state.projectedState.hasKeyword(benthicore, Keyword.SHROUD) shouldBe true
    }

    test("one Merfolk can't pay a cost that asks for two") {
        val d = driver()
        val me = d.activePlayer!!

        val benthicore = d.putCreatureOnBattlefield(me, "Benthicore")
        d.tapPermanent(benthicore)
        val diver = d.putCreatureOnBattlefield(me, "Inkfathom Divers")

        activate(d, me, benthicore, listOf(diver)).isSuccess shouldBe false
        d.isTapped(benthicore) shouldBe true
    }

    test("an already-tapped Merfolk doesn't count toward the two") {
        val d = driver()
        val me = d.activePlayer!!

        val benthicore = d.putCreatureOnBattlefield(me, "Benthicore")
        d.tapPermanent(benthicore)
        val divers = List(2) { d.putCreatureOnBattlefield(me, "Inkfathom Divers") }
        d.tapPermanent(divers.first())

        activate(d, me, benthicore, divers).isSuccess shouldBe false
        d.isTapped(benthicore) shouldBe true
    }

    test("Merfolk an opponent controls can't pay for it") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)

        val benthicore = d.putCreatureOnBattlefield(me, "Benthicore")
        d.tapPermanent(benthicore)
        val theirs = List(2) { d.putCreatureOnBattlefield(opponent, "Inkfathom Divers") }

        activate(d, me, benthicore, theirs).isSuccess shouldBe false
        d.isTapped(benthicore) shouldBe true
    }
})
