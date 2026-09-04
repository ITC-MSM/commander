package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.AdditionalPhasesComponent
import com.wingedsheep.engine.state.components.player.ExtraPhaseKind
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Breath of Fury (RAV #116, {2}{R}{R}, Enchantment — Aura).
 *
 *   Enchant creature you control
 *   When enchanted creature deals combat damage to a player, sacrifice it and attach this Aura to
 *   a creature you control. If you do, untap all creatures you control and after this phase, there
 *   is an additional combat phase.
 *
 * Two things the card is easy to get wrong, and both are asserted here. The re-attach is a
 * *choice*, not a target — so the prompt must offer every creature you control **except** the one
 * just sacrificed, which is only true because the candidates are gathered after the sacrifice.
 * And per the 2005-10-01 ruling, an empty candidate list is not a partial resolution: no untap, no
 * extra combat, and the Aura falls off to the graveyard. A version that untapped first, or that
 * gathered before sacrificing, passes the happy-path test and fails the second one.
 */
class BreathOfFuryScenarioTest : ScenarioTestBase() {

    init {
        context("Breath of Fury") {

            test("connecting sacrifices the host, moves the Aura, untaps the team, and adds a combat") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Breath of Fury", "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hill Giant", tapped = true)
                    .withCardOnBattlefield(1, "Craw Wurm", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val aura = game.findPermanent("Breath of Fury")!!
                val giant = game.findPermanent("Hill Giant")!!
                val wurm = game.findPermanent("Craw Wurm")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("Bob took the Bears' 2 damage, which is what fired the trigger") {
                    game.getLifeTotal(2) shouldBe 18
                }

                // Two creatures remain, so the re-attach is a real choice rather than an auto-pick.
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("the sacrificed host is gone by the time the candidates are gathered") {
                    decision.options.toSet() shouldBe setOf(giant, wurm)
                }
                game.selectCards(listOf(giant)).error shouldBe null
                game.resolveStack()

                withClue("the enchanted creature was sacrificed") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("the Aura moved to the chosen creature rather than falling off") {
                    game.isOnBattlefield("Breath of Fury") shouldBe true
                    game.state.getEntity(aura)!!.get<AttachedToComponent>()!!.targetId shouldBe giant
                }
                withClue("all creatures you control untap — not just the new host") {
                    game.state.getEntity(giant)!!.has<TappedComponent>() shouldBe false
                    game.state.getEntity(wurm)!!.has<TappedComponent>() shouldBe false
                }
                withClue("one additional combat phase is queued for the attacking player") {
                    val queued = game.state.getEntity(game.state.activePlayerId!!)
                        ?.get<AdditionalPhasesComponent>()?.phases.orEmpty()
                    queued.size shouldBe 1
                    queued.single().kind shouldBe ExtraPhaseKind.COMBAT
                }
            }

            test("with no other creature the Aura falls off and there is no untap or extra combat") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Breath of Fury", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() is CombatResolutionDecision) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }
                game.checkStateBasedActions()

                withClue("nothing to attach to, so no prompt is raised") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("the host was still sacrificed — that half is not conditional") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("an unattached Aura is put into its owner's graveyard (CR 704.5m)") {
                    game.isOnBattlefield("Breath of Fury") shouldBe false
                    game.isInGraveyard(1, "Breath of Fury") shouldBe true
                }
                withClue("'if you do' failed, so no additional combat phase") {
                    game.state.getEntity(game.state.activePlayerId!!)
                        ?.get<AdditionalPhasesComponent>()?.phases.orEmpty() shouldBe emptyList()
                }
            }
        }
    }
}
