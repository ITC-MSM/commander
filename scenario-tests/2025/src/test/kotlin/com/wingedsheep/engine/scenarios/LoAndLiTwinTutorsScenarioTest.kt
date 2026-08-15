package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.cards.LoAndLiTwinTutors
import com.wingedsheep.mtg.sets.definitions.tla.cards.OzaisCruelty
import com.wingedsheep.mtg.sets.definitions.tla.cards.ZukoExiledPrince
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Lo and Li, Twin Tutors (TLA #108) — {4}{B} Legendary Creature — Human Advisor, 2/2.
 *
 * "When Lo and Li enter, search your library for a Lesson or Noble card, reveal it, put it into
 *  your hand, then shuffle.
 *  Noble creatures you control and Lesson spells you control have lifelink."
 *
 * Exercises:
 *  - the compound Lesson-OR-Noble ETB tutor (`Any.withAnySubtype("Lesson", "Noble")`) — the search
 *    offers both a Lesson card and a Noble card as valid finds, proving the OR filter matches both
 *    subtypes; the chosen card lands in hand and the non-chosen match stays in the shuffled library;
 *  - the Noble-creatures lifelink lord (projected keyword + combat life gain), and that a non-Noble
 *    creature you control does not gain the keyword;
 *  - the Lesson-spells lifelink clause: a damage-dealing Lesson (Ozai's Cruelty) you control gains
 *    you life equal to the damage it deals, via the `GrantKeywordToOwnSpells` → DamageUtils path.
 */
class LoAndLiTwinTutorsScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(LoAndLiTwinTutors, ZukoExiledPrince, OzaisCruelty))
        return d
    }

    test("ETB tutor: search offers both a Lesson and a Noble; chosen card goes to hand, library shuffled") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Seed the library with one Lesson and one Noble (plus the 40 Islands, which match neither).
        val lessonId = d.putCardOnTopOfLibrary(active, "Ozai's Cruelty")   // Sorcery — Lesson
        val nobleId = d.putCardOnTopOfLibrary(active, "Zuko, Exiled Prince") // Legendary Creature — Human Noble
        val librarySizeBefore = d.state.getLibrary(active).size

        val loAndLi = d.putCardInHand(active, "Lo and Li, Twin Tutors")
        d.giveMana(active, Color.BLACK, 1)
        d.giveColorlessMana(active, 4)
        d.castSpell(active, loAndLi).isSuccess shouldBe true

        // Resolve the cast + its ETB trigger until the library-search decision pauses execution.
        var guard = 0
        while (d.pendingDecision == null && guard++ < 20) d.bothPass()

        val search = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        val offered = search.options.mapNotNull { d.getCardName(it) }
        // The OR filter matches BOTH subtypes.
        offered shouldContain "Ozai's Cruelty"
        offered shouldContain "Zuko, Exiled Prince"

        // Choose the Lesson.
        d.submitCardSelection(active, listOf(lessonId))

        // Resolve the reveal + move-to-hand + shuffle tail.
        guard = 0
        while ((d.pendingDecision != null || d.stackSize > 0) && guard++ < 20) {
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }

        // Chosen Lesson is now in hand; the non-chosen Noble stays in the (shuffled) library.
        val handNames = d.getHand(active).mapNotNull { d.getCardName(it) }
        handNames shouldContain "Ozai's Cruelty"
        d.state.getLibrary(active) shouldContain nobleId
        d.state.getLibrary(active) shouldNotContain lessonId
        // Exactly one card left the library (the found Lesson).
        d.state.getLibrary(active).size shouldBe librarySizeBefore - 1
    }

    test("Noble creature you control has lifelink and gains you life on combat damage") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        // Lo and Li provides the static; direct placement does not fire the ETB tutor.
        d.putCreatureOnBattlefield(me, "Lo and Li, Twin Tutors")
        val zuko = d.putCreatureOnBattlefield(me, "Zuko, Exiled Prince") // Human Noble, 4/3

        // Projected: the Noble has lifelink, Lo and Li (Human Advisor, non-Noble) does not.
        d.state.projectedState.hasKeyword(zuko, Keyword.LIFELINK) shouldBe true

        d.removeSummoningSickness(zuko)
        val lifeBefore = d.getLifeTotal(me)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(zuko), opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opp)
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Lifelink: Zuko dealt 4 combat damage, so I gained 4 life.
        d.getLifeTotal(me) shouldBe lifeBefore + 4
    }

    test("a non-Noble creature you control does NOT gain lifelink") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        val loAndLi = d.putCreatureOnBattlefield(me, "Lo and Li, Twin Tutors") // Human Advisor, 2/2

        d.state.projectedState.hasKeyword(loAndLi, Keyword.LIFELINK) shouldBe false

        d.removeSummoningSickness(loAndLi)
        val lifeBefore = d.getLifeTotal(me)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(loAndLi), opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opp)
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // No lifelink on the non-Noble attacker — my life total is unchanged.
        d.getLifeTotal(me) shouldBe lifeBefore
    }

    test("Lesson spell you control has lifelink: a burn Lesson gains you life equal to its damage") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        // Lo and Li grants lifelink to Lesson spells I control.
        d.putCreatureOnBattlefield(me, "Lo and Li, Twin Tutors")
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ozai = d.putCardInHand(me, "Ozai's Cruelty") // Sorcery — Lesson: deals 2 damage to target player
        d.giveMana(me, Color.BLACK, 1)
        d.giveColorlessMana(me, 2)
        val lifeBefore = d.getLifeTotal(me)

        d.castSpell(me, ozai, targets = listOf(opp)).isSuccess shouldBe true
        var guard = 0
        while ((d.pendingDecision != null || d.stackSize > 0) && guard++ < 30) {
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }

        // The Lesson dealt 2 damage to the opponent; lifelink gained me 2 life.
        d.getLifeTotal(me) shouldBe lifeBefore + 2
    }
})
