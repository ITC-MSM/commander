package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.Electroduplicate
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Electroduplicate (FDN #85) — {2}{R} Sorcery.
 *
 * "Create a token that's a copy of target creature you control, except it has haste and
 * \"At the beginning of the end step, sacrifice this token.\" Flashback {2}{R}{R}."
 *
 * Red's sibling of Self-Reflection: the copy clause layers `addedKeywords = HASTE` plus a
 * `sacrificeAtStep = END` delayed trigger onto [com.wingedsheep.sdk.dsl.Effects.CreateTokenCopyOfTarget].
 */
class ElectroduplicateScenarioTest : FunSpec({

    val projector = StateProjector()

    fun newDriver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(Electroduplicate)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("creates a hasty copy that is sacrificed at the end step") {
        val d = newDriver()
        val p1 = d.player1

        val original = d.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3
        val spell = d.putCardInHand(p1, "Electroduplicate")
        d.giveMana(p1, Color.RED, 1)
        d.giveColorlessMana(p1, 2)

        d.castSpellWithTargets(p1, spell, listOf(ChosenTarget.Permanent(original))).error shouldBe null
        d.bothPass()

        // A token copy now exists alongside the original.
        val creatures = d.getCreatures(p1)
        creatures.size shouldBe 2
        val token = creatures.first { it != original }

        val projected = projector.project(d.state)
        projected.hasKeyword(token, Keyword.HASTE) shouldBe true
        projected.getPower(token) shouldBe 3
        projected.getToughness(token) shouldBe 3

        // At the beginning of the end step the token is sacrificed; the original survives.
        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.getCreatures(p1) shouldBe listOf(original)
    }
})
