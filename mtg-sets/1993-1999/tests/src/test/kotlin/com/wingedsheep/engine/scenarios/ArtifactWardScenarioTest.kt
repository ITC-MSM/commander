package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Artifact Ward — Aura, enchant creature:
 *  - Enchanted creature can't be blocked by artifact creatures.
 *  - Prevent all damage that would be dealt to enchanted creature by artifact sources.
 *  - Enchanted creature can't be the target of abilities from artifact sources.
 *
 * The targeting clause is the engine gap (`CantBeTargetedBySourceTypeAbilities`); the other
 * two compose from existing primitives. These tests prove the targeting clause and the
 * prevent-artifact-damage clause, plus a control: a NON-artifact ability can still target the
 * warded creature.
 */
class ArtifactWardScenarioTest : ScenarioTestBase() {

    private val triskelionAbilityId by lazy {
        cardRegistry.getCard("Triskelion")!!.activatedAbilities[0].id
    }
    private val sorcererAbilityId by lazy {
        cardRegistry.getCard("Prodigal Sorcerer")!!.activatedAbilities[0].id
    }

    init {
        context("Artifact Ward — can't be the target of abilities from artifact sources") {
            test("an artifact source's ability can't target the warded creature (even your own), but a non-artifact ability can") {
                // All permanents under P1, who is active and has priority. The ward blocks artifact
                // sources regardless of controller, so P1's own Triskelion can't target it; P1's
                // (non-artifact) Prodigal Sorcerer can.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Artifact Ward")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(1, "Triskelion", summoningSickness = false) // artifact source
                    .withCardOnBattlefield(1, "Prodigal Sorcerer", summoningSickness = false) // non-artifact source
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val castWard = game.castSpell(1, "Artifact Ward", bears)
                withClue("Artifact Ward should cast onto Grizzly Bears: ${castWard.error}") {
                    castWard.error shouldBe null
                }
                game.resolveStack()

                // Triskelion (artifact source) ability can't target the warded creature.
                val triskelion = game.findPermanent("Triskelion")!!
                val attempt = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = triskelion,
                        abilityId = triskelionAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("Triskelion (artifact) must NOT be able to target the warded creature") {
                    (attempt.error != null) shouldBe true
                }
                withClue("The warded creature took no damage (the artifact ability was illegal)") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }

                // The Sorcerer is not an artifact source — its tap ability CAN target the warded creature.
                val sorcerer = game.findPermanent("Prodigal Sorcerer")!!
                val legal = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = sorcerer,
                        abilityId = sorcererAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("Prodigal Sorcerer (non-artifact) CAN target the warded creature: ${legal.error}") {
                    legal.error shouldBe null
                }
            }
        }

        context("Artifact Ward — prevent all damage from artifact sources & can't be blocked by artifact creatures") {
            test("artifact combat damage to the warded creature is prevented; it survives a lethal artifact attacker") {
                // P2 is active and attacks with a 4/4 artifact creature (Triskelion); P1's warded 2/2
                // blocks. All artifact-source damage to the warded creature is prevented, so it survives
                // what would otherwise be lethal. (The ward is attached up front via the builder.)
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // 2/2 warded blocker
                    .withCardAttachedTo(1, "Artifact Ward", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Triskelion", summoningSickness = false) // 4/4 artifact attacker
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Triskelion" to 1))
                withClue("Declaring the artifact attacker should succeed: ${attack.error}") { attack.error shouldBe null }
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Grizzly Bears" to listOf("Triskelion")))
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Artifact damage to the warded creature is prevented — it survives the 4/4") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
