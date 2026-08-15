package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Exdeath, Void Warlock // Neo Exdeath, Dimension's End (FIN #220) — {1}{B}{G} Legendary
 * Creature — Spirit Warlock 3/3.
 *
 *  Front:
 *    "When Exdeath enters, you gain 3 life.
 *     At the beginning of your end step, if there are six or more permanent cards in your
 *     graveyard, transform Exdeath."
 *  Back — Neo Exdeath, Dimension's End (Spirit Avatar, * /3):
 *    "Trample
 *     Neo Exdeath's power is equal to the number of permanent cards in your graveyard."
 *
 * Verifies the ETB life gain, the intervening-"if" end-step transform (only at ≥6 permanent
 * cards in the graveyard — nonpermanent cards don't count), and the back face's
 * characteristic-defining power tracking the number of permanent cards in the graveyard.
 */
class ExdeathVoidWarlockScenarioTest : ScenarioTestBase() {

    private val FRONT = "Exdeath, Void Warlock"
    private val BACK = "Neo Exdeath, Dimension's End"

    private fun com.wingedsheep.engine.support.ScenarioTestBase.TestGame.faceNameOf(id: com.wingedsheep.sdk.model.EntityId): String? =
        state.getEntity(id)?.get<CardComponent>()?.name

    init {
        context("Exdeath, Void Warlock") {

            test("ETB — you gain 3 life when Exdeath enters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLandsOnBattlefield(1, "Swamp", 2) // pays the {1}{B}
                    .withLandsOnBattlefield(1, "Forest", 1) // pays the {G}
                    .withCardInHand(1, FRONT)
                    .build()

                withClue("starting life is 20") { game.getLifeTotal(1) shouldBe 20 }

                game.castSpell(1, FRONT).error shouldBe null
                game.resolveStack()

                withClue("Exdeath's ETB gains you 3 life") { game.getLifeTotal(1) shouldBe 23 }
            }

            test("end step does NOT transform with fewer than six permanent cards in graveyard") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, FRONT, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Five permanent cards + one instant = six graveyard cards but only FIVE permanents.
                repeat(5) { builder = builder.withCardInGraveyard(1, "Grizzly Bears") }
                builder = builder.withCardInGraveyard(1, "Lightning Bolt")
                val game = builder.build()

                val exdeath = game.findPermanent(FRONT)!!

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("only 5 permanent cards in graveyard → the intervening-if fails, no transform") {
                    game.faceNameOf(exdeath) shouldBe FRONT
                }
            }

            test("end step DOES transform at six permanent cards; back face is Neo Exdeath") {
                // Cast Exdeath so it enters as a proper double-faced permanent (its transform
                // needs the DoubleFacedComponent that only a real cast / zone-entry wires up).
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, FRONT)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { builder = builder.withCardInGraveyard(1, "Grizzly Bears") }
                val game = builder.build()

                game.castSpell(1, FRONT).error shouldBe null
                game.resolveStack()
                val exdeath = game.findPermanent(FRONT)!!

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("six permanent cards in graveyard → transform into Neo Exdeath") {
                    game.faceNameOf(exdeath) shouldBe BACK
                }
                withClue("after transform, Neo Exdeath's power equals 6 permanent cards, toughness 3") {
                    game.state.projectedState.getPower(exdeath) shouldBe 6
                    game.state.projectedState.getToughness(exdeath) shouldBe 3
                }
                withClue("Neo Exdeath has trample") {
                    game.state.projectedState.hasKeyword(exdeath, Keyword.TRAMPLE) shouldBe true
                }
            }

            test("Neo Exdeath's power tracks the number of permanent cards in your graveyard") {
                // Back face placed directly: four permanent cards + one nonpermanent (instant).
                var fourBuilder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, BACK, summoningSickness = false)
                repeat(4) { fourBuilder = fourBuilder.withCardInGraveyard(1, "Grizzly Bears") }
                fourBuilder = fourBuilder.withCardInGraveyard(1, "Lightning Bolt") // does not count
                val fourGame = fourBuilder.build()

                val neoFour = fourGame.findPermanent(BACK)!!
                withClue("4 permanent cards (the instant is excluded) → power 4, toughness 3") {
                    fourGame.state.projectedState.getPower(neoFour) shouldBe 4
                    fourGame.state.projectedState.getToughness(neoFour) shouldBe 3
                }

                // A larger graveyard → the CDA recomputes to the new count.
                var sevenBuilder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, BACK, summoningSickness = false)
                repeat(7) { sevenBuilder = sevenBuilder.withCardInGraveyard(1, "Grizzly Bears") }
                val sevenGame = sevenBuilder.build()

                val neoSeven = sevenGame.findPermanent(BACK)!!
                withClue("7 permanent cards → power updates to 7") {
                    sevenGame.state.projectedState.getPower(neoSeven) shouldBe 7
                }
            }
        }
    }
}
