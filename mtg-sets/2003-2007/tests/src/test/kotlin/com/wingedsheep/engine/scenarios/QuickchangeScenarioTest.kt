package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.Outcome
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.Quickchange
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Quickchange (RAV #62) — "Target creature becomes the color or colors of your choice until end of
 * turn. Draw a card."
 *
 * Pins the multi-color choice: the decision offers "one or more" colors, a set answer recolors the
 * target to exactly that set (replacing its old color), a single-color answer still works, an
 * answer naming a color twice (or a primary color outside the set) is refused, the change ends at
 * end of turn, and the card draw happens either way.
 */
class QuickchangeScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + Quickchange)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Casts Quickchange at [target] and returns the color decision it raises. */
    fun GameTestDriver.castQuickchange(caster: EntityId, target: EntityId): ChooseColorDecision {
        giveMana(caster, Color.BLUE, 2)
        val spell = putCardInHand(caster, "Quickchange")
        castSpellWithTargets(caster, spell, listOf(ChosenTarget.Permanent(target))).outcome shouldBe Outcome.Done
        var guard = 0
        while (stackSize > 0 && pendingDecision == null && guard++ < 10) bothPass()
        return pendingDecision.shouldBeInstanceOf<ChooseColorDecision>()
    }

    fun GameTestDriver.finishStack() {
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("the target becomes exactly the chosen set of colors, and the caster draws a card") {
        val d = driver()
        val me = d.player1
        val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears") // green

        val decision = d.castQuickchange(me, bears)
        withClue("the decision lets the caster pick more than one color") {
            (decision.maxColors > 1) shouldBe true
        }
        val handBefore = d.getHandSize(me)

        d.submitDecision(me, ColorChosenResponse(decision.id, Color.BLUE, listOf(Color.BLUE, Color.RED)))
            .outcome shouldBe Outcome.Done
        d.finishStack()

        withClue("green is replaced by the chosen blue and red") {
            d.state.projectedState.getColors(bears) shouldBe setOf("BLUE", "RED")
        }
        withClue("Quickchange draws a card") {
            d.getHandSize(me) shouldBe handBefore + 1
        }

        d.passPriorityUntil(Step.UPKEEP)
        withClue("the color change ends at end of turn") {
            d.state.projectedState.getColors(bears) shouldBe setOf("GREEN")
        }
    }

    test("a single color is a legal choice") {
        val d = driver()
        val me = d.player1
        val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")

        val decision = d.castQuickchange(me, bears)
        d.submitDecision(me, ColorChosenResponse(decision.id, Color.BLACK)).outcome shouldBe Outcome.Done
        d.finishStack()

        d.state.projectedState.getColors(bears) shouldBe setOf("BLACK")
    }

    test("a colorless artifact creature becomes colored but stays an artifact") {
        val d = driver()
        val me = d.player1
        val golem = d.putCreatureOnBattlefield(me, "Artifact Creature")

        val decision = d.castQuickchange(me, golem)
        d.submitDecision(me, ColorChosenResponse(decision.id, Color.WHITE, Color.entries.toList()))
            .outcome shouldBe Outcome.Done
        d.finishStack()

        d.state.projectedState.getColors(golem) shouldBe Color.entries.map { it.name }.toSet()
        d.state.projectedState.hasType(golem, "ARTIFACT") shouldBe true
    }

    test("malformed multi-color answers are refused") {
        val d = driver()
        val me = d.player1
        val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")

        val decision = d.castQuickchange(me, bears)
        withClue("a color named twice") {
            d.submitDecision(me, ColorChosenResponse(decision.id, Color.RED, listOf(Color.RED, Color.RED)))
                .outcome.shouldBeInstanceOf<Outcome.Rejected>()
        }
        withClue("a primary color outside the chosen set") {
            d.submitDecision(me, ColorChosenResponse(decision.id, Color.WHITE, listOf(Color.RED)))
                .outcome.shouldBeInstanceOf<Outcome.Rejected>()
        }
        withClue("the decision is still pending after the refusals") {
            d.pendingDecision?.id shouldBe decision.id
        }
    }
})
