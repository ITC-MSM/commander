package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sorceress's Schemes (Final Fantasy, {3}{R} Sorcery):
 *
 *   "Return target instant or sorcery card from your graveyard or exiled card with flashback you
 *    own to your hand. Add {R}."
 *
 * The target is a cross-zone union ([com.wingedsheep.sdk.scripting.filters.unified.TargetFilter.or]):
 *   clause A = instant or sorcery card in your graveyard,
 *   clause B = a card you own with flashback in your exile.
 *
 * These tests pin both clauses of the union, the ownership/type filters that scope each clause, and
 * the resulting "Add {R}" — the exact behaviour the cross-zone target feature exists to support.
 */
class SorceressSchemesScenarioTest : ScenarioTestBase() {

    private fun TestGame.exileCardId(playerNumber: Int, name: String): EntityId {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).first { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == name
        }
    }

    /** Cast Sorceress's Schemes from player 1's hand at an explicit card target in a given zone. */
    private fun TestGame.castSchemesAt(targetId: EntityId, ownerPlayer: Int, zone: Zone): ExecutionResult {
        val ownerId = if (ownerPlayer == 1) player1Id else player2Id
        val cardId = state.getHand(player1Id).first { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == "Sorceress's Schemes"
        }
        return execute(CastSpell(player1Id, cardId, listOf(ChosenTarget.Card(targetId, ownerId, zone))))
    }

    init {
        context("Sorceress's Schemes — cross-zone union target") {

            test("returns an instant or sorcery from your graveyard to hand and adds {R}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sorceress's Schemes")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bolt = game.findCardsInGraveyard(1, "Lightning Bolt").first()
                val result = game.castSchemesAt(bolt, ownerPlayer = 1, zone = Zone.GRAVEYARD)
                withClue("cast targeting a graveyard instant should be legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Lightning Bolt returns to its owner's hand") {
                    game.isInHand(1, "Lightning Bolt") shouldBe true
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe false
                }
                withClue("Sorceress's Schemes also adds {R}") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.getAmount(Color.RED) shouldBe 1
                }
            }

            test("returns an exiled card with flashback you own to hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sorceress's Schemes")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    // Gysahl Greens is a sorcery with Flashback — a legal clause-B target while exiled.
                    .withCardInExile(1, "Gysahl Greens")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val greens = game.exileCardId(1, "Gysahl Greens")
                val result = game.castSchemesAt(greens, ownerPlayer = 1, zone = Zone.EXILE)
                withClue("cast targeting an exiled flashback card you own should be legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Gysahl Greens returns from exile to its owner's hand") {
                    game.isInHand(1, "Gysahl Greens") shouldBe true
                    game.isInExile(1, "Gysahl Greens") shouldBe false
                }
            }

            test("legal targets are the union of both clauses and exclude illegal candidates") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sorceress's Schemes")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    // clause A legal: instant you own in your graveyard.
                    .withCardInGraveyard(1, "Lightning Bolt")
                    // illegal: a creature in your graveyard (not instant/sorcery).
                    .withCardInGraveyard(1, "Grizzly Bears")
                    // clause B legal: a flashback card you own in your exile.
                    .withCardInExile(1, "Gysahl Greens")
                    // illegal: an exiled card you own with no flashback.
                    .withCardInExile(1, "Mountain")
                    // illegal: instant in the OPPONENT's graveyard (not owned by you).
                    .withCardInGraveyard(2, "Lightning Bolt")
                    // illegal: flashback card in the OPPONENT's exile (not owned by you).
                    .withCardInExile(2, "Gysahl Greens")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val myBolt = game.findCardsInGraveyard(1, "Lightning Bolt").first()
                val myGreens = game.exileCardId(1, "Gysahl Greens")

                val cast = game.getLegalActions(1).first { it.description.contains("Sorceress's Schemes") }
                withClue("only the graveyard instant and the exiled flashback card you own are legal") {
                    val validTargets = cast.validTargets.shouldNotBeNull()
                    validTargets.shouldContainExactlyInAnyOrder(listOf(myBolt, myGreens))
                }
            }

            test("rejects targeting a creature card in your graveyard (fails clause A's type filter)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sorceress's Schemes")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").first()
                val result = game.castSchemesAt(bears, ownerPlayer = 1, zone = Zone.GRAVEYARD)
                withClue("a creature in the graveyard matches neither clause, so the cast is illegal") {
                    result.error.shouldNotBeNull()
                }
                game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            }

            test("rejects targeting an exiled card you own without flashback (fails clause B's keyword filter)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sorceress's Schemes")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInExile(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mountain = game.exileCardId(1, "Mountain")
                val result = game.castSchemesAt(mountain, ownerPlayer = 1, zone = Zone.EXILE)
                withClue("an exiled card without flashback matches neither clause, so the cast is illegal") {
                    result.error.shouldNotBeNull()
                }
            }
        }
    }
}
