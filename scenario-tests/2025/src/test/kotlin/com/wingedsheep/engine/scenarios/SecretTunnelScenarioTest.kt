package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.SecretTunnel
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Secret Tunnel (TLA #278) — Land — Cave.
 *
 *   This land can't be blocked.
 *   {T}: Add {C}.
 *   {4}, {T}: Two target creatures you control that share a creature type can't be blocked this turn.
 *
 * Verifies:
 *  - the {T} mana ability adds one colorless;
 *  - the {4},{T} ability, aimed at two creatures that SHARE a creature type (two Bears), grants
 *    CANT_BE_BLOCKED to both until end of turn;
 *  - aiming the ability at two creatures that do NOT share a creature type (a Bear and a Goblin) is
 *    an illegal target set — the cross-target `sameCreatureType` constraint rejects the activation.
 */
class SecretTunnelScenarioTest : ScenarioTestBase() {

    private val manaAbilityId = SecretTunnel.activatedAbilities[0].id
    private val unblockableAbilityId = SecretTunnel.activatedAbilities[1].id

    init {
        context("Secret Tunnel") {

            test("the printed 'this land can't be blocked' projects CANT_BE_BLOCKED onto the land itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Secret Tunnel", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tunnel = game.findPermanent("Secret Tunnel")!!

                withClue("the self-scoped CantBeBlocked() static must project the keyword even while it's a non-creature land") {
                    game.state.projectedState.hasKeyword(tunnel, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }
            }

            test("{T}: Add {C} produces one colorless mana") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Secret Tunnel", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tunnel = game.findPermanent("Secret Tunnel")!!

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = tunnel, abilityId = manaAbilityId)
                ).error shouldBe null

                withClue("the mana ability adds exactly one colorless mana") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.colorless shouldBe 1
                }
            }

            test("{4},{T} makes two creatures that share a creature type unblockable this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Secret Tunnel", 1)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tunnel = game.findPermanent("Secret Tunnel")!!
                val bears = game.findPermanents("Grizzly Bears")
                bears.size shouldBe 2

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = tunnel,
                        abilityId = unblockableAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears[0]), ChosenTarget.Permanent(bears[1]))
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("both shared-type creatures can't be blocked this turn") {
                    game.state.projectedState.hasKeyword(bears[0], AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                    game.state.projectedState.hasKeyword(bears[1], AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }
            }

            test("{4},{T} can't target two creatures that do not share a creature type") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Secret Tunnel", 1)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Goblin Guide", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tunnel = game.findPermanent("Secret Tunnel")!!
                val bear = game.findPermanent("Grizzly Bears")!!
                val goblin = game.findPermanent("Goblin Guide")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = tunnel,
                        abilityId = unblockableAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bear), ChosenTarget.Permanent(goblin))
                    )
                )

                withClue("a Bear and a Goblin share no creature type, so the target set is illegal") {
                    result.error shouldNotBe null
                }
                withClue("the illegal activation grants nothing") {
                    game.state.projectedState.hasKeyword(bear, "CANT_BE_BLOCKED") shouldBe false
                    game.state.projectedState.hasKeyword(goblin, "CANT_BE_BLOCKED") shouldBe false
                }
            }
        }
    }
}
