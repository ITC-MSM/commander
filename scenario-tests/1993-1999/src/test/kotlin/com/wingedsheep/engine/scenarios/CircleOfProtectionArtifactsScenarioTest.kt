package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Circle of Protection: Artifacts (ATQ #4).
 *
 * {1}{W} Enchantment
 * "{2}: The next time an artifact source of your choice would deal damage to you this turn, prevent
 *  that damage."
 *
 * Exercises the `ChosenSourceMatching(GameObjectFilter.Artifact)` prevention filter: only artifact
 * sources are eligible for the choice, and — because the facade sets `nextInstanceOnly = true` —
 * only the *next* damage instance from the chosen artifact source is prevented, not all damage from
 * it for the turn.
 */
class CircleOfProtectionArtifactsScenarioTest : ScenarioTestBase() {

    init {
        fun copAbilityId() =
            cardRegistry.getCard("Circle of Protection: Artifacts")!!.script.activatedAbilities[0].id

        context("Circle of Protection: Artifacts") {

            test("prevents combat damage from the chosen artifact source but not a nonartifact source") {
                // Player 2 attacks with an artifact creature (Yotian Soldier 1/4) and a nonartifact
                // creature (Grizzly Bears 2/2). Player 1 names the artifact source to prevent it.
                val game = scenario()
                    .withPlayers("Defender", "Attacker")
                    .withCardOnBattlefield(1, "Circle of Protection: Artifacts")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(2, "Yotian Soldier")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Yotian Soldier" to 1, "Grizzly Bears" to 1)).error shouldBe null
                game.passPriority() // P2 passes; P1 gets priority

                // Player 1 activates the Circle. It pauses to choose an artifact source.
                val cop = game.findPermanent("Circle of Protection: Artifacts")!!
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = cop,
                        abilityId = copAbilityId()
                    )
                )
                withClue("Activation succeeds: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                val decision = game.state.pendingDecision
                decision.shouldNotBeNull()
                decision.shouldBeInstanceOf<SelectCardsDecision>()

                val yotian = game.findPermanent("Yotian Soldier")!!
                val grizzly = game.findPermanent("Grizzly Bears")!!
                withClue("Only artifact sources are offered for the choice") {
                    decision.options shouldContain yotian
                    decision.options shouldNotContain grizzly
                }

                game.submitDecision(CardsSelectedResponse(decision.id, listOf(yotian)))

                // Resolve combat.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Yotian Soldier's 1 damage prevented; Grizzly Bears' 2 damage still dealt → 20 - 2 = 18") {
                    game.getLifeTotal(1) shouldBe 18
                }
            }

            test("prevents only the NEXT instance from the chosen source, not all damage from it") {
                // Regression for nextInstanceOnly: the shield is single-instance (Circle of
                // Protection family), not an all-damage-from-source shield (Samite Ministration).
                // Triskelion (artifact) pings its own controller twice; only the first is prevented.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Triskelion")
                    .withLandsOnBattlefield(1, "Plains", 8)
                    .withCardOnBattlefield(1, "Circle of Protection: Artifacts")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Triskelion").error shouldBe null
                game.resolveStack()
                val trisk = game.findPermanent("Triskelion")!!

                // Activate the Circle and name Triskelion as the chosen artifact source.
                val cop = game.findPermanent("Circle of Protection: Artifacts")!!
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = cop, abilityId = copAbilityId())
                ).error shouldBe null
                game.resolveStack()
                val decision = game.state.pendingDecision
                decision.shouldNotBeNull()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                game.submitDecision(CardsSelectedResponse(decision.id, listOf(trisk)))

                val triskAbility = cardRegistry.getCard("Triskelion")!!.script.activatedAbilities[0]
                fun pingSelf() {
                    game.execute(
                        ActivateAbility(
                            playerId = game.player1Id,
                            sourceId = trisk,
                            abilityId = triskAbility.id,
                            targets = listOf(entityIdToChosenTarget(game.state, game.player1Id))
                        )
                    ).error shouldBe null
                    game.resolveStack()
                }

                pingSelf()
                withClue("First instance from the chosen source is prevented") {
                    game.getLifeTotal(1) shouldBe 20
                }

                pingSelf()
                withClue("Shield is consumed after one instance — the second ping hits for 1") {
                    game.getLifeTotal(1) shouldBe 19
                }
            }
        }
    }
}
