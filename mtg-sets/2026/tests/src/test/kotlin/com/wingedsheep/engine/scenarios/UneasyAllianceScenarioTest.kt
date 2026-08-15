package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Uneasy Alliance (TMT) — {1}{W} Enchantment — Aura.
 *
 *   Enchant creature
 *   Enchanted creature can't attack or block.
 *   {5}, Sacrifice this Aura: Exile enchanted creature. You create a 1/1 black
 *   Ninja creature token. Activate only as a sorcery.
 *
 * Regression: prior to the fix the static abilities defaulted to `GroupFilter.source()`,
 * which applied the restriction to the Aura itself (a non-creature with no attack/block
 * step), so the enchanted creature could still attack and block freely. The Pacifism-style
 * `GroupFilter.attachedCreature()` is required for the restrictions to land on the
 * enchanted creature.
 */
class UneasyAllianceScenarioTest : ScenarioTestBase() {

    private val sacrificeAbilityId =
        cardRegistry.getCard("Uneasy Alliance")!!.activatedAbilities.first().id

    init {
        context("Uneasy Alliance") {

            test("enchanted creature can't attack or block (Pacifism-style static)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Uneasy Alliance")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val grizzlyBears = game.findPermanent("Grizzly Bears")!!

                val castResult = game.castSpell(1, "Uneasy Alliance", grizzlyBears)
                withClue("Uneasy Alliance should cast on opponent's creature: ${castResult.error}") {
                    castResult.error shouldBe null
                }
                game.resolveStack()

                withClue("Uneasy Alliance should be on the battlefield") {
                    game.isOnBattlefield("Uneasy Alliance") shouldBe true
                }
                withClue("Enchanted creature can't attack") {
                    game.state.projectedState.cantAttack(grizzlyBears) shouldBe true
                }
                withClue("Enchanted creature can't block") {
                    game.state.projectedState.cantBlock(grizzlyBears) shouldBe true
                }
            }

            test("activated ability exiles the enchanted creature and creates a 1/1 black Ninja") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Uneasy Alliance")
                    .withLandsOnBattlefield(1, "Plains", 7)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val grizzlyBears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Uneasy Alliance", grizzlyBears)
                game.resolveStack()

                val aura = game.findPermanent("Uneasy Alliance")
                withClue("Aura should be on the battlefield before activation") {
                    aura.shouldNotBeNull()
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = aura!!,
                        abilityId = sacrificeAbilityId,
                    )
                )
                withClue("Activating the sacrifice ability should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Aura is sacrificed and ends up in Player1's graveyard") {
                    game.isInGraveyard(1, "Uneasy Alliance") shouldBe true
                }
                withClue("Enchanted creature is exiled") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.state.getExile(game.player2Id).contains(grizzlyBears) shouldBe true
                }
                withClue("A 1/1 black Ninja creature token is created under Player1's control") {
                    val tokens = game.state.getBattlefield(game.player1Id).mapNotNull { id ->
                        val entity = game.state.getEntity(id) ?: return@mapNotNull null
                        val card = entity.get<CardComponent>()
                            ?: return@mapNotNull null
                        val isToken = entity.get<TokenComponent>() != null
                        if (isToken && card.typeLine.hasSubtype(Subtype("Ninja"))) card else null
                    }
                    tokens.size shouldBe 1
                    tokens.first().baseStats?.basePower shouldBe 1
                    tokens.first().baseStats?.baseToughness shouldBe 1
                    tokens.first().colors shouldBe setOf(Color.BLACK)
                }
            }
        }
    }
}
