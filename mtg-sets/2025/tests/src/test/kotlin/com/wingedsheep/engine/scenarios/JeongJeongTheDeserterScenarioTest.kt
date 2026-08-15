package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.JeongJeongTheDeserter
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Jeong Jeong, the Deserter (TLA #142).
 *
 * {2}{R} 2/3 Human Rebel Ally, Firebending 1.
 * Exhaust — {3}: Put a +1/+1 counter on Jeong Jeong. When you next cast a Lesson spell this turn,
 *   copy it and you may choose new targets for the copy.
 */
class JeongJeongTheDeserterScenarioTest : ScenarioTestBase() {

    // A {0} Lesson that draws a card — used to observe the copy (original + copy = two draws).
    private val study = card("Study") {
        manaCost = "{0}"
        typeLine = "Sorcery — Lesson"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    init {
        cardRegistry.register(JeongJeongTheDeserter)
        cardRegistry.register(study)

        context("Jeong Jeong, the Deserter") {

            test("exhaust adds a +1/+1 counter and copies the next Lesson spell") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jeong Jeong, the Deserter", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInHand(1, "Study")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jeong = game.findPermanent("Jeong Jeong, the Deserter")!!
                val abilityId = cardRegistry.getCard("Jeong Jeong, the Deserter")!!.script.activatedAbilities[0].id

                game.execute(ActivateAbility(game.player1Id, jeong, abilityId)).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("a +1/+1 counter on Jeong Jeong") {
                    game.state.getEntity(jeong)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }

                val libraryBefore = game.librarySize(1)
                game.castSpell(1, "Study").error shouldBe null
                game.resolveStack()

                withClue("the Lesson is copied: the original and the copy each draw a card (2 total)") {
                    (libraryBefore - game.librarySize(1)) shouldBe 2
                }
            }
        }
    }
}
