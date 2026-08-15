package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CoinFlipEvent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Goblin Artisans (ATQ #26).
 *
 * {R} Creature — Goblin Artificer 1/1
 * "{T}: Flip a coin. If you win the flip, draw a card. If you lose the flip, counter target artifact
 *  spell you control that isn't the target of an ability from another creature named Goblin Artisans."
 *
 * Proves the self-referential targeting restriction
 * ([com.wingedsheep.sdk.scripting.predicates.StatePredicate.NotTargetedByAbilityFromSameNamedSource]):
 * once one Goblin Artisans targets an artifact spell, a second Goblin Artisans can't target the same
 * spell. Also exercises the win (draw) / lose (counter) coin-flip branches.
 */
class GoblinArtisansScenarioTest : ScenarioTestBase() {

    init {
        context("Goblin Artisans") {

            val abilityId by lazy { cardRegistry.getCard("Goblin Artisans")!!.script.activatedAbilities[0].id }

            fun goblins(game: TestGame): List<EntityId> =
                game.state.getBattlefield().filter {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Goblin Artisans"
                }

            fun ornithopterOnStack(game: TestGame): EntityId =
                game.state.stack.first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Ornithopter" }

            test("a second Goblin Artisans can't target a spell already targeted by another Goblin Artisans") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Goblin Artisans", summoningSickness = false)
                    .withCardOnBattlefield(1, "Goblin Artisans", summoningSickness = false)
                    .withCardInHand(1, "Ornithopter") // {0} artifact spell to put on the stack
                    .withCardInLibrary(1, "Mountain")  // so a winning flip can draw
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Put an artifact spell (Ornithopter) on the stack, controlled by P1.
                game.castSpell(1, "Ornithopter").error shouldBe null
                val artifactSpell = ornithopterOnStack(game)

                val (first, second) = goblins(game).let { it[0] to it[1] }

                // First Goblin Artisans activates, targeting the artifact spell — legal.
                val firstActivation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = first,
                        abilityId = abilityId,
                        targets = listOf(ChosenTarget.Spell(artifactSpell))
                    )
                )
                withClue("First Goblin Artisans should legally target the artifact spell: ${firstActivation.error}") {
                    firstActivation.error shouldBe null
                }

                // Second Goblin Artisans now tries to target the SAME spell — illegal, because the
                // spell is already the target of an ability from another creature named Goblin Artisans.
                val secondActivation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = second,
                        abilityId = abilityId,
                        targets = listOf(ChosenTarget.Spell(artifactSpell))
                    )
                )
                withClue("Second Goblin Artisans must NOT be able to target the already-targeted spell") {
                    (secondActivation.error != null) shouldBe true
                }
            }

            test("win the flip → draw a card; the targeted artifact spell is not countered") {
                var sawWin = false
                repeat(60) {
                    if (sawWin) return@repeat
                    val game = scenario()
                        .withPlayers("Player", "Opponent")
                        .withCardOnBattlefield(1, "Goblin Artisans", summoningSickness = false)
                        .withCardInHand(1, "Ornithopter")
                        .withCardInLibrary(1, "Mountain")
                        .withActivePlayer(1)
                        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                        .build()

                    game.castSpell(1, "Ornithopter").error shouldBe null
                    val artifactSpell = ornithopterOnStack(game)
                    val handBefore = game.state.getHand(game.player1Id).size
                    val goblin = goblins(game).first()

                    val activation = game.execute(
                        ActivateAbility(game.player1Id, goblin, abilityId, listOf(ChosenTarget.Spell(artifactSpell)))
                    )
                    activation.error shouldBe null
                    val results = game.resolveStack()
                    val coin = results.flatMap { it.events }.filterIsInstance<CoinFlipEvent>().firstOrNull()
                        ?: return@repeat
                    if (coin.won) {
                        sawWin = true
                        withClue("Winning the flip draws a card") {
                            game.state.getHand(game.player1Id).size shouldBe handBefore + 1
                        }
                        withClue("The artifact spell resolved (was not countered) — it's on the battlefield") {
                            game.isOnBattlefield("Ornithopter") shouldBe true
                        }
                    }
                }
                withClue("Expected to observe a winning flip within 60 attempts") { sawWin shouldBe true }
            }

            test("lose the flip → counter the targeted artifact spell") {
                var sawLoss = false
                repeat(60) {
                    if (sawLoss) return@repeat
                    val game = scenario()
                        .withPlayers("Player", "Opponent")
                        .withCardOnBattlefield(1, "Goblin Artisans", summoningSickness = false)
                        .withCardInHand(1, "Ornithopter")
                        .withCardInLibrary(1, "Mountain")
                        .withActivePlayer(1)
                        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                        .build()

                    game.castSpell(1, "Ornithopter").error shouldBe null
                    val artifactSpell = ornithopterOnStack(game)
                    val goblin = goblins(game).first()

                    val activation = game.execute(
                        ActivateAbility(game.player1Id, goblin, abilityId, listOf(ChosenTarget.Spell(artifactSpell)))
                    )
                    activation.error shouldBe null
                    val results = game.resolveStack()
                    val coin = results.flatMap { it.events }.filterIsInstance<CoinFlipEvent>().firstOrNull()
                        ?: return@repeat
                    if (!coin.won) {
                        sawLoss = true
                        withClue("Losing the flip counters the artifact spell — it never reaches the battlefield") {
                            game.isOnBattlefield("Ornithopter") shouldBe false
                        }
                        withClue("The countered spell is in its owner's graveyard") {
                            game.state.getGraveyard(game.player1Id).any {
                                game.state.getEntity(it)?.get<CardComponent>()?.name == "Ornithopter"
                            } shouldBe true
                        }
                    }
                }
                withClue("Expected to observe a losing flip within 60 attempts") { sawLoss shouldBe true }
            }
        }
    }
}
