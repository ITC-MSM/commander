package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AvianChangeling
import com.wingedsheep.mtg.sets.definitions.lrw.cards.NightshadeStinger
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SilvergillDouser
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Silvergill Douser (LRW #87) — "{T}: Target creature gets -X/-0 until end of turn, where X is the
 * number of Merfolk and/or Faeries you control."
 *
 * X is one subtype-union count, so the two things worth proving are that a Faerie feeds the same
 * tally a Merfolk does, and that a changeling — which is *both* — is counted once rather than twice.
 * The Douser itself is a Merfolk, so the floor is -1/-0, never -0/-0.
 */
class SilvergillDouserScenarioTest : FunSpec({

    val douseAbility = SilvergillDouser.activatedAbilities.single().id
    val projector = StateProjector()

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + SilvergillDouser + NightshadeStinger + AvianChangeling)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the Douser alone counts itself: -1/-0") {
        val d = driver()
        val douser = d.putCreatureOnBattlefield(d.player1, "Silvergill Douser")
        d.removeSummoningSickness(douser)
        val bear = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")

        d.submit(
            ActivateAbility(d.player1, douser, douseAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        d.bothPass()

        val after = projector.project(d.state)
        withClue("One Merfolk (the Douser) is -1/-0 on a 2/2") {
            after.getPower(bear) shouldBe 1
            after.getToughness(bear) shouldBe 2
        }
    }

    test("a Faerie feeds the same tally a Merfolk does") {
        val d = driver()
        val douser = d.putCreatureOnBattlefield(d.player1, "Silvergill Douser")
        d.removeSummoningSickness(douser)
        d.putCreatureOnBattlefield(d.player1, "Nightshade Stinger")
        val bear = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")

        d.submit(
            ActivateAbility(d.player1, douser, douseAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        d.bothPass()

        val after = projector.project(d.state)
        withClue("A Merfolk plus a Faerie is -2/-0") {
            after.getPower(bear) shouldBe 0
            after.getToughness(bear) shouldBe 2
        }
    }

    test("a changeling is both halves of the union but is counted once") {
        val d = driver()
        val douser = d.putCreatureOnBattlefield(d.player1, "Silvergill Douser")
        d.removeSummoningSickness(douser)
        d.putCreatureOnBattlefield(d.player1, "Avian Changeling")
        val bear = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")

        d.submit(
            ActivateAbility(d.player1, douser, douseAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        d.bothPass()

        val after = projector.project(d.state)
        withClue("The changeling is one permanent, so the tally is 2, not 3") {
            after.getPower(bear) shouldBe 0
        }
    }

    test("an opponent's Merfolk does not count — the tally is yours only") {
        val d = driver()
        val douser = d.putCreatureOnBattlefield(d.player1, "Silvergill Douser")
        d.removeSummoningSickness(douser)
        d.putCreatureOnBattlefield(d.player2, "Nightshade Stinger")
        val bear = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")

        d.submit(
            ActivateAbility(d.player1, douser, douseAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        d.bothPass()

        val after = projector.project(d.state)
        withClue("Bob's Faerie is on Bob's side of the count") {
            after.getPower(bear) shouldBe 1
        }
    }
})
