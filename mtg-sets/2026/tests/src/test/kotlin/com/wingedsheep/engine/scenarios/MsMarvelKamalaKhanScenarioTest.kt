package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ms. Marvel, Kamala Khan (MSH #67) — {2}{U} Legendary Creature — Mutant Inhuman Hero, 1/4.
 *
 * "Reach, vigilance
 *  You have no maximum hand size.
 *  Embiggen Fist — Whenever you cast a spell that targets a creature you control, draw a card.
 *  Until end of turn, Ms. Marvel gains 'Ms. Marvel's base power is equal to the number of cards in
 *  your hand.'"
 *
 * The line under test is the granted base-power clause, which must keep tracking the hand for the
 * rest of the turn rather than freezing the number the trigger saw. It rides on
 * `Effects.SetBasePower(..., reevaluateContinuously = true)`; the primitive's own matrix (counters,
 * pump, duration, granter leaving) lives in `SetBaseStatsContinuousScenarioTest`.
 */
class MsMarvelKamalaKhanScenarioTest : ScenarioTestBase() {

    private val pump = card("Ms Marvel Pump Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Target creature gets +1/+1 until end of turn."
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.ModifyStats(1, 1, creature)
        }
    }

    private val drawTwo = card("Ms Marvel Draw Two Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Draw two cards."
        spell {
            effect = Effects.DrawCards(2)
        }
    }

    init {
        cardRegistry.register(listOf(pump, drawTwo))

        // Ms. Marvel is auto-discovered from the MSH package, so she is already in the registry.
        fun build(vararg handCards: String, libraryCards: Int = 6) = scenario()
            .withPlayers()
            .withCardOnBattlefield(1, "Ms. Marvel, Kamala Khan")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Hill Giant")
            .withLandsOnBattlefield(1, "Island", 6)
            .let { builder ->
                var acc = builder
                repeat(libraryCards) { acc = acc.withCardInLibrary(1, "Hill Giant") }
                handCards.fold(acc) { b, name -> b.withCardInHand(1, name) }
            }
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun TestGame.castAndResolve(spellName: String, targetId: EntityId? = null) {
            val result = castSpell(1, spellName, targetId)
            withClue("casting $spellName should succeed: ${result.error}") { result.error shouldBe null }
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
            resolveStack()
        }

        test("Embiggen Fist draws a card and sets base power to the hand size, leaving toughness at 4") {
            val game = build("Ms Marvel Pump Test", "Hill Giant", "Hill Giant", "Hill Giant")
            val marvel = game.findPermanent("Ms. Marvel, Kamala Khan")!!
            val bear = game.findPermanent("Grizzly Bears")!!

            withClue("printed 1/4 before anything happens") {
                game.state.projectedState.getPower(marvel) shouldBe 1
                game.state.projectedState.getToughness(marvel) shouldBe 4
            }

            // Hand 4 -> the pump leaves the hand (3) -> the trigger draws one (4).
            game.castAndResolve("Ms Marvel Pump Test", bear)

            withClue("Embiggen Fist drew a card") { game.handSize(1) shouldBe 4 }
            game.state.projectedState.getPower(marvel) shouldBe 4
            withClue("only base power is set; the printed toughness is untouched") {
                game.state.projectedState.getToughness(marvel) shouldBe 4
            }
            withClue("the pump went to the Bears, not to Ms. Marvel") {
                game.state.projectedState.getPower(bear) shouldBe 3
            }
        }

        test("her base power keeps tracking the hand after the trigger has resolved") {
            val game = build(
                "Ms Marvel Pump Test", "Ms Marvel Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val marvel = game.findPermanent("Ms. Marvel, Kamala Khan")!!
            val bear = game.findPermanent("Grizzly Bears")!!

            // Hand 5 -> pump leaves (4) -> trigger draws one (5).
            game.castAndResolve("Ms Marvel Pump Test", bear)
            game.handSize(1) shouldBe 5
            game.state.projectedState.getPower(marvel) shouldBe 5

            // Draw Two targets nothing, so it does not re-trigger Embiggen Fist:
            // hand 5 -> spell leaves (4) -> draws two (6).
            game.castAndResolve("Ms Marvel Draw Two Test")
            game.handSize(1) shouldBe 6
            withClue("the granted clause re-reads the hand rather than keeping the trigger's number") {
                game.state.projectedState.getPower(marvel) shouldBe 6
            }
            game.state.projectedState.getToughness(marvel) shouldBe 4
        }

        test("a spell targeting Ms. Marvel herself arms the trigger — she is a creature you control") {
            val game = build("Ms Marvel Pump Test", "Hill Giant", "Hill Giant", "Hill Giant")
            val marvel = game.findPermanent("Ms. Marvel, Kamala Khan")!!

            // The trigger goes on the stack above the pump and resolves first: hand 4 -> the pump
            // leaves the hand (3) -> Embiggen Fist draws one (4), so base power is set to 4. Then
            // the pump resolves on top of that, in layer 7c: 4+1 power, 4+1 toughness.
            game.castAndResolve("Ms Marvel Pump Test", marvel)

            withClue("Embiggen Fist drew a card off a spell aimed at Ms. Marvel herself") {
                game.handSize(1) shouldBe 4
            }
            game.state.projectedState.getPower(marvel) shouldBe 5
            game.state.projectedState.getToughness(marvel) shouldBe 5
        }

        test("a spell targeting a creature you don't control does not trigger Embiggen Fist") {
            val game = build("Ms Marvel Pump Test", "Hill Giant", "Hill Giant", "Hill Giant")
            val marvel = game.findPermanent("Ms. Marvel, Kamala Khan")!!
            val opposing = game.findPermanent("Hill Giant")!!

            game.castAndResolve("Ms Marvel Pump Test", opposing)

            withClue("no draw — the trigger requires a creature *you control*") {
                game.handSize(1) shouldBe 3
            }
            withClue("base power stays printed") {
                game.state.projectedState.getPower(marvel) shouldBe 1
            }
        }

        test("the granted base-power clause wears off at end of turn") {
            val game = build("Ms Marvel Pump Test", "Hill Giant", "Hill Giant", "Hill Giant")
            val marvel = game.findPermanent("Ms. Marvel, Kamala Khan")!!
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Ms Marvel Pump Test", bear)
            game.state.projectedState.getPower(marvel) shouldBe 4

            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN) // opponent's turn

            withClue("back to the printed 1/4") {
                game.state.projectedState.getPower(marvel) shouldBe 1
                game.state.projectedState.getToughness(marvel) shouldBe 4
            }
        }

        test("You have no maximum hand size — no discard at cleanup with ten cards in hand") {
            val game = build(
                *Array(10) { "Hill Giant" },
                libraryCards = 6,
            )
            game.handSize(1) shouldBe 10

            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN) // opponent's turn

            withClue("NoMaximumHandSize means the cleanup step discards nothing") {
                game.handSize(1) shouldBe 10
            }
        }
    }
}
