package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Urza's Avenger (ATQ #74).
 *
 * {6} Artifact Creature — Shapeshifter 4/4
 * "{0}: This creature gets -1/-1 and gains your choice of banding, flying, first strike, or
 *  trample until end of turn."
 *
 * Verifies that each {0} activation applies a cumulative -1/-1 and grants the chosen keyword until
 * end of turn, and that stacking activations subtract more and grant multiple keywords.
 */
class UrzasAvengerScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    private val abilityId by lazy {
        cardRegistry.getCard("Urza's Avenger")!!.script.activatedAbilities[0].id
    }

    init {
        // Mode order in the card: 0 banding, 1 flying, 2 first strike, 3 trample.
        fun activateChoosing(game: TestGame, avengerId: com.wingedsheep.sdk.model.EntityId, modeIndex: Int) {
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = avengerId,
                    abilityId = abilityId
                )
            )
            withClue("Activating Urza's Avenger should succeed: ${result.error}") {
                result.error shouldBe null
            }
            game.resolveStack()
            val modeDecision = game.getPendingDecision() as ChooseOptionDecision
            game.submitDecision(OptionChosenResponse(modeDecision.id, modeIndex))
            game.resolveStack()
        }

        context("Urza's Avenger") {

            test("one activation applies -1/-1 and grants the chosen keyword until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Urza's Avenger", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val avenger = game.findPermanent("Urza's Avenger")!!

                // Choose flying (mode 1).
                activateChoosing(game, avenger, 1)

                val projected = stateProjector.project(game.state)
                withClue("4/4 base minus -1/-1 = 3/3") {
                    projected.getPower(avenger) shouldBe 3
                    projected.getToughness(avenger) shouldBe 3
                }
                withClue("It gains flying until end of turn") {
                    projected.hasKeyword(avenger, Keyword.FLYING) shouldBe true
                }
            }

            test("two activations stack the -X/-X and grant both chosen keywords") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Urza's Avenger", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val avenger = game.findPermanent("Urza's Avenger")!!

                // First activation: first strike (mode 2). Second: trample (mode 3).
                activateChoosing(game, avenger, 2)
                activateChoosing(game, avenger, 3)

                val projected = stateProjector.project(game.state)
                withClue("Two -1/-1s stack: 4/4 -> 2/2") {
                    projected.getPower(avenger) shouldBe 2
                    projected.getToughness(avenger) shouldBe 2
                }
                withClue("Both chosen keywords are granted until end of turn") {
                    projected.hasKeyword(avenger, Keyword.FIRST_STRIKE) shouldBe true
                    projected.hasKeyword(avenger, Keyword.TRAMPLE) shouldBe true
                }
                withClue("An unchosen keyword (flying) is NOT granted") {
                    projected.hasKeyword(avenger, Keyword.FLYING) shouldBe false
                }
            }

            test("banding can be chosen") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Urza's Avenger", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val avenger = game.findPermanent("Urza's Avenger")!!

                activateChoosing(game, avenger, 0) // banding

                val projected = stateProjector.project(game.state)
                withClue("It gains banding until end of turn") {
                    projected.hasKeyword(avenger, Keyword.BANDING) shouldBe true
                }
            }
        }
    }
}
