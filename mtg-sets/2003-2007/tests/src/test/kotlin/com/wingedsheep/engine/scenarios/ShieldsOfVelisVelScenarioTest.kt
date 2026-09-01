package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.ShieldsOfVelisVel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Shields of Velis Vel (LRW #39) — "Creatures target player controls get +0/+1 and gain all
 * creature types until end of turn."
 *
 * The spell targets a *player*, not the creatures, and the controller predicate on the group has
 * to resolve against that bound target rather than against the caster. A filter that fell back to
 * the implicit "you control" reading would still buff a board and still read correctly on the
 * card, so the test aims the spell at the **opponent** — a shape where the two readings give
 * opposite answers.
 *
 * The changeling grant is asserted alongside the stats: "gain all creature types" is the half a
 * pump-only script would silently drop, and it is the half the card is named for.
 */
class ShieldsOfVelisVelScenarioTest : FunSpec({

    fun GameTestDriver.toughness(id: EntityId): Int? = state.projectedState.getToughness(id)

    test("the target player's creatures get the buff and every creature type; yours do not") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + ShieldsOfVelisVel)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val opponent = d.getOpponent(d.player1)
        val theirs = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val ours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")

        val shields = d.putCardInHand(d.player1, "Shields of Velis Vel")
        d.giveMana(d.player1, Color.WHITE, 1)
        d.castSpellWithTargets(d.player1, shields, listOf(ChosenTarget.Player(opponent)))
            .error shouldBe null
        d.bothPass()

        withClue("the opponent's 2/2 became a 2/3") {
            d.toughness(theirs) shouldBe 3
        }
        withClue("and gained all creature types, so their Bear is also a Goblin") {
            d.state.projectedState.hasSubtype(theirs, "Goblin") shouldBe true
        }
        withClue("our own creature was never in the group — the target player is not us") {
            d.toughness(ours) shouldBe 2
            d.state.projectedState.hasSubtype(ours, "Goblin") shouldBe false
        }
    }
})
