package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Casualties of War (WAR #187).
 *
 * {2}{B}{B}{G}{G} Sorcery
 * "Choose one or more —
 *  • Destroy target artifact.
 *  • Destroy target creature.
 *  • Destroy target enchantment.
 *  • Destroy target land.
 *  • Destroy target planeswalker."
 *
 * This is the corpus's first five-mode modal spell, and the first at `chooseCount = 5`. What the
 * tests pin is the *count* semantics rather than the destruction (a one-line `Effects.Destroy` per
 * mode): "choose one or more" is `minChooseCount = 1` against `chooseCount = 5`, so any non-empty
 * subset is legal, all five at once is legal, and the empty choice is not.
 *
 * Modes: 0 = artifact, 1 = creature, 2 = enchantment, 3 = land, 4 = planeswalker.
 */
class CasualtiesOfWarScenarioTest : ScenarioTestBase() {

    init {
        /** A board with one legal target for every mode, all controlled by the opponent. */
        fun boardWithOneOfEach(): TestGame = scenario()
            .withPlayers("Player", "Opponent")
            .withCardInHand(1, "Casualties of War")
            .withLandsOnBattlefield(1, "Swamp", 3)
            .withLandsOnBattlefield(1, "Forest", 3)
            .withCardOnBattlefield(2, "Sol Ring")
            .withCardOnBattlefield(2, "Centaur Courser")
            .withCardOnBattlefield(2, "Test Enchantment")
            .withLandsOnBattlefield(2, "Island", 1)
            .withCardOnBattlefield(2, "Domri, Anarch of Bolas")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun cast(game: TestGame, modes: List<Int>, targets: List<EntityId>) =
            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = game.findCardsInHand(1, "Casualties of War").first(),
                    targets = targets.map { ChosenTarget.Permanent(it) },
                    chosenModes = modes,
                    modeTargetsOrdered = targets.map { listOf(ChosenTarget.Permanent(it)) }
                )
            )

        context("Casualties of War — choosing a subset") {

            test("two modes destroy exactly their two targets and leave the rest alone") {
                val game = boardWithOneOfEach()
                val ring = game.findPermanent("Sol Ring")!!
                val courser = game.findPermanent("Centaur Courser")!!

                cast(game, modes = listOf(0, 1), targets = listOf(ring, courser)).error shouldBe null
                game.resolveStack()

                withClue("Mode 0 destroyed the artifact") {
                    game.findPermanent("Sol Ring").shouldBeNull()
                }
                withClue("Mode 1 destroyed the creature") {
                    game.findPermanent("Centaur Courser").shouldBeNull()
                }
                withClue("The unchosen modes touched nothing") {
                    game.isOnBattlefield("Test Enchantment") shouldBe true
                    game.isOnBattlefield("Domri, Anarch of Bolas") shouldBe true
                }
            }
        }

        context("Casualties of War — choosing every mode") {

            test("all five modes resolve in one cast") {
                val game = boardWithOneOfEach()
                val ring = game.findPermanent("Sol Ring")!!
                val courser = game.findPermanent("Centaur Courser")!!
                val enchantment = game.findPermanent("Test Enchantment")!!
                val island = game.findAllPermanents("Island").first()
                val domri = game.findPermanent("Domri, Anarch of Bolas")!!

                cast(
                    game,
                    modes = listOf(0, 1, 2, 3, 4),
                    targets = listOf(ring, courser, enchantment, island, domri)
                ).error shouldBe null
                game.resolveStack()

                withClue("Every chosen mode destroyed its target") {
                    game.findPermanent("Sol Ring").shouldBeNull()
                    game.findPermanent("Centaur Courser").shouldBeNull()
                    game.findPermanent("Test Enchantment").shouldBeNull()
                    game.findPermanent("Domri, Anarch of Bolas").shouldBeNull()
                    game.findAllPermanents("Island") shouldBe emptyList()
                }
            }
        }

        context("Casualties of War — the floor") {

            test("choosing no modes is illegal (\"one or more\" means at least one)") {
                val game = boardWithOneOfEach()

                val result = cast(game, modes = emptyList(), targets = emptyList())

                withClue("minChooseCount = 1 rejects the empty choice") {
                    result.isSuccess shouldBe false
                }
            }
        }
    }
}
