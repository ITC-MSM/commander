package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Skycoach Waypoint (Secrets of Strixhaven) — a Land.
 *
 * {T}: Add {C}.
 * {3}, {T}: Target creature becomes prepared. (Only creatures with prepare spells can become prepared.)
 *
 * The prepare ability targets any creature, but `Effects.BecomePrepared` is a no-op against a creature
 * whose card has no prepare spell (matching the reminder text). A creature with a prepare spell (e.g.
 * Leech Collector) becomes prepared and gets a castable copy of its prepare spell in exile.
 */
class SkycoachWaypointScenarioTest : ScenarioTestBase() {

    private val manaAbilityId =
        cardRegistry.getCard("Skycoach Waypoint")!!.activatedAbilities[0].id
    private val prepareAbilityId =
        cardRegistry.getCard("Skycoach Waypoint")!!.activatedAbilities[1].id

    private fun TestGame.findExileCopy(playerNumber: Int, name: String): com.wingedsheep.sdk.model.EntityId? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }
    }

    init {
        context("Skycoach Waypoint") {

            test("{T}: Add {C} taps the land for one colorless mana") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Skycoach Waypoint")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val waypoint = game.findPermanent("Skycoach Waypoint")!!
                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = waypoint, abilityId = manaAbilityId)
                )
                withClue("Tapping Skycoach Waypoint for mana should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("Skycoach Waypoint produced one colorless mana") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.colorless shouldBe 1
                }
            }

            test("{3}, {T}: a creature with a prepare spell becomes prepared") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Skycoach Waypoint")
                    .withCardOnBattlefield(1, "Leech Collector", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val waypoint = game.findPermanent("Skycoach Waypoint")!!
                val leech = game.findPermanent("Leech Collector")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = waypoint,
                        abilityId = prepareAbilityId,
                        targets = listOf(ChosenTarget.Permanent(leech)),
                    )
                )
                withClue("Activating the prepare ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Leech Collector (a prepare creature) becomes prepared") {
                    game.state.getEntity(leech)?.get<PreparedComponent>() shouldNotBe null
                }
                withClue("A Bloodletting prepare-spell copy is created in exile") {
                    game.findExileCopy(1, "Leech Collector") shouldNotBe null
                }
            }

            test("{3}, {T} on a creature with no prepare spell is a no-op (only prepare creatures can become prepared)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Skycoach Waypoint")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val waypoint = game.findPermanent("Skycoach Waypoint")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = waypoint,
                        abilityId = prepareAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("The ability still resolves (Grizzly Bears is a legal target): ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears has no prepare spell, so it does NOT become prepared") {
                    game.state.getEntity(bears)?.get<PreparedComponent>() shouldBe null
                }
                withClue("No prepare-spell copy is created") {
                    game.state.getExile(game.player1Id).none { id ->
                        game.state.getEntity(id)?.get<PreparedSpellCopyComponent>() != null
                    } shouldBe true
                }
            }
        }
    }
}
