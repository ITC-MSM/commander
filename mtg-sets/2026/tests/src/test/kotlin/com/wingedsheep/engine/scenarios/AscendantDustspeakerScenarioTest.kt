package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Ascendant Dustspeaker (SOS #8).
 *
 * "{4}{W} Creature — Orc Cleric 3/4. Flying.
 *  When this creature enters, put a +1/+1 counter on another target creature you control.
 *  At the beginning of combat on your turn, exile up to one target card from a graveyard."
 *
 * Covers: (a) the ETB puts a +1/+1 counter on another creature you control and cannot target
 * itself, (b) the begin-combat trigger exiles a chosen graveyard card, (c) it has flying.
 */
class AscendantDustspeakerScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Ascendant Dustspeaker") {

            test("ETB puts a +1/+1 counter on another target creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Ascendant Dustspeaker")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpell(1, "Ascendant Dustspeaker")
                withClue("Casting Ascendant Dustspeaker should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                // ETB trigger asks for another target creature you control.
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears gets one +1/+1 counter") {
                    plusOneCounters(game, bears) shouldBe 1
                }
            }

            test("ETB cannot target itself — only another creature you control is a legal target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Ascendant Dustspeaker")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Ascendant Dustspeaker")
                game.resolveStack()

                val dustspeaker = game.findPermanent("Ascendant Dustspeaker")!!
                val decision = game.state.pendingDecision
                withClue("ETB should prompt for a target") {
                    (decision is ChooseTargetsDecision) shouldBe true
                }
                val legal = (decision as ChooseTargetsDecision).legalTargets[0].orEmpty()
                withClue("Only Grizzly Bears is a legal target; the source cannot target itself") {
                    legal shouldContainExactly listOf(bears)
                }
                withClue("Dustspeaker itself must not be among the legal targets") {
                    (dustspeaker in legal) shouldBe false
                }
            }

            test("begin-combat trigger exiles a chosen card from a graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ascendant Dustspeaker", summoningSickness = false)
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.state.getGraveyard(game.player2Id).single()

                // Advance into combat so the "beginning of combat on your turn" trigger fires.
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                // "Up to one target" — choose the graveyard card.
                game.selectTargets(listOf(target))
                game.resolveStack()

                withClue("Opponent's graveyard should be empty after the card is exiled") {
                    game.state.getGraveyard(game.player2Id).size shouldBe 0
                }
                withClue("The card should now be in the opponent's exile zone") {
                    game.state.getExile(game.player2Id) shouldContainExactly listOf(target)
                }
            }

            test("has flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ascendant Dustspeaker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dustspeaker = game.findPermanent("Ascendant Dustspeaker")!!
                withClue("Ascendant Dustspeaker should have flying") {
                    game.state.projectedState.hasKeyword(dustspeaker, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
