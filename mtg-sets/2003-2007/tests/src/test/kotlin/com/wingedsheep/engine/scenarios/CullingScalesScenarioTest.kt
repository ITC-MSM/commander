package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/** Scenario coverage for Culling Scales (MRD #160). */
class CullingScalesScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(testCreature("One-Mana Test Creature", "{1}"))
        cardRegistry.register(testCreature("Another One-Mana Test Creature", "{1}"))
        cardRegistry.register(testCreature("Four-Mana Test Creature", "{4}"))
        cardRegistry.register(testCreature("Zero-Mana Test Token", null))

        context("Culling Scales upkeep trigger") {
            test("only permanents tied for the lowest mana value are legal targets") {
                val game = upkeepScenario(
                    "One-Mana Test Creature",
                    "Another One-Mana Test Creature",
                    "Four-Mana Test Creature"
                )

                advanceToScalesTargetChoice(game)
                val expensive = game.findPermanent("Four-Mana Test Creature").shouldNotBeNull()
                withClue("A higher-mana-value permanent is not a legal target") {
                    game.selectTargets(listOf(expensive)).error.shouldNotBeNull()
                }

                val tiedMinimum = game.findPermanent("Another One-Mana Test Creature").shouldNotBeNull()
                game.selectTargets(listOf(tiedMinimum)).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Another One-Mana Test Creature") shouldBe false
                game.isOnBattlefield("One-Mana Test Creature") shouldBe true
            }

            test("a target that is no longer a minimum at resolution is not destroyed") {
                val game = upkeepScenario("One-Mana Test Creature", "Four-Mana Test Creature")
                advanceToScalesTargetChoice(game)

                val target = game.findPermanent("One-Mana Test Creature").shouldNotBeNull()
                val bystander = game.findPermanent("Four-Mana Test Creature").shouldNotBeNull()
                game.selectTargets(listOf(target)).error shouldBe null

                // A face-down permanent has mana value 0. Making the bystander face down after
                // targets are chosen causes the original target to fail its resolution-time check.
                val bystanderContainer = game.state.getEntity(bystander).shouldNotBeNull()
                game.state = game.state.withEntity(bystander, bystanderContainer.with(FaceDownComponent))
                game.resolveStack()

                withClue("The former minimum remains because the ability has no legal target") {
                    game.isOnBattlefield("One-Mana Test Creature") shouldBe true
                }
            }

            test("a mana-value-zero token is the minimum") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Culling Scales")
                    .withCardOnBattlefield(2, "Zero-Mana Test Token", isToken = true)
                    .withCardOnBattlefield(2, "One-Mana Test Creature")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                advanceToScalesTargetChoice(game)
                val token = game.findPermanent("Zero-Mana Test Token").shouldNotBeNull()
                game.selectTargets(listOf(token)).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Zero-Mana Test Token") shouldBe false
                game.isOnBattlefield("One-Mana Test Creature") shouldBe true
            }

            test("can destroy itself when it is the lowest-mana-value nonland permanent") {
                val game = upkeepScenario("Four-Mana Test Creature")
                advanceToScalesTargetChoice(game)

                val scales = game.findPermanent("Culling Scales").shouldNotBeNull()
                game.selectTargets(listOf(scales)).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Culling Scales") shouldBe false
                game.isInGraveyard(1, "Culling Scales") shouldBe true
            }
        }
    }

    private fun upkeepScenario(vararg permanents: String): TestGame {
        var builder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Culling Scales")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(2)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        permanents.forEach { builder = builder.withCardOnBattlefield(2, it) }
        return builder.build()
    }

    private fun advanceToScalesTargetChoice(game: TestGame) {
        game.passUntilPhase(Phase.ENDING, Step.END)
        game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
        game.resolveStack()
        game.getPendingDecision().shouldNotBeNull()
    }

    private fun testCreature(name: String, cost: String?): CardDefinition = CardDefinition.creature(
        name = name,
        manaCost = cost?.let(ManaCost::parse) ?: ManaCost.ZERO,
        subtypes = setOf(Subtype("Construct")),
        power = 1,
        toughness = 1
    )
}
