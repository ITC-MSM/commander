package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Icy Reception — "Counter target creature or legendary spell unless its controller pays {3}."
 *
 * The target filter is a union: a creature spell qualifies, a legendary noncreature spell
 * qualifies, and a spell that is neither is not a legal target.
 */
class IcyReceptionScenarioTest : ScenarioTestBase() {

    fun TestGame.castIcyReceptionCounterMode(targetSpellName: String) = run {
        val icy = state.getHand(player1Id).first {
            state.getEntity(it)?.get<CardComponent>()?.name == "Icy Reception"
        }
        val spell: EntityId = state.stack.first {
            state.getEntity(it)?.get<CardComponent>()?.name == targetSpellName
        }
        val targets = listOf(ChosenTarget.Spell(spell))
        execute(CastSpell(player1Id, icy, targets, chosenModes = listOf(0), modeTargetsOrdered = listOf(targets)))
    }

    fun opponentCasts(spellName: String, land: String, lands: Int) = scenario().withPlayers()
        .withCardInHand(1, "Icy Reception")
        .withLandsOnBattlefield(1, "Island", 2)
        .withCardInHand(2, spellName)
        .withLandsOnBattlefield(2, land, lands)
        .withActivePlayer(2)
        .withPriorityPlayer(2)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
        .also { game ->
            game.castSpell(2, spellName).error shouldBe null
            game.passPriority()
        }

    init {
        test("counters a creature spell whose controller can't pay {3}") {
            val game = opponentCasts("Grizzly Bears", "Forest", 2)
            game.castIcyReceptionCounterMode("Grizzly Bears").error shouldBe null
            game.resolveStack()
            if (game.hasPendingDecision()) game.answerYesNo(false)
            game.resolveStack()

            withClue("the Bears were countered") {
                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                game.isOnBattlefield("Grizzly Bears") shouldBe false
            }
        }

        test("a legendary noncreature spell is a legal target") {
            val game = opponentCasts("The Irencrag", "Plains", 2)
            game.castIcyReceptionCounterMode("The Irencrag").error shouldBe null
        }

        test("a spell that is neither a creature nor legendary is not a legal target") {
            val game = opponentCasts("Mind Stone", "Plains", 2)
            game.castIcyReceptionCounterMode("Mind Stone").error shouldNotBe null
        }
    }
}
