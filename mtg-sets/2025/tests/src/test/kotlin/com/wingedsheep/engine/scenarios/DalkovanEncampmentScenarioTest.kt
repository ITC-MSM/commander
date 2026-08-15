package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Dalkovan Encampment (TDM #253) — Land.
 *
 * "{2}{W}, {T}: Whenever you attack this turn, create two 1/1 red Warrior creature tokens that are
 *  tapped and attacking. Sacrifice them at the beginning of the next end step."
 *
 * Exercises the activated-ability → event-based delayed-trigger composition: activating the ability
 * installs a `CreateDelayedTriggerEffect(trigger = YouAttack)` that lasts the rest of the turn, so
 * each declare-attackers this turn produces two tapped-and-attacking Warrior tokens. Tokens are
 * sacrificed at the next end step.
 */
class DalkovanEncampmentScenarioTest : ScenarioTestBase() {

    // The {2}{W},{T} mobilize-grant ability is the non-mana activated ability.
    private val mobilizeAbilityId =
        cardRegistry.getCard("Dalkovan Encampment")!!.activatedAbilities[1].id

    init {
        context("Dalkovan Encampment whenever-you-attack token ability") {

            test("activating the ability then attacking creates two tapped, attacking Warrior tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dalkovan Encampment", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val encampment = game.findPermanent("Dalkovan Encampment")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = encampment,
                        abilityId = mobilizeAbilityId
                    )
                )
                withClue("Activating the {2}{W},{T} ability should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("No Warrior tokens before attacking") {
                    game.findPermanents("Warrior Token").size shouldBe 0
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Grizzly Bears" to 2))
                withClue("Declaring attackers should succeed: ${attack.error}") { attack.error shouldBe null }
                game.resolveStack()

                val warriors = game.findPermanents("Warrior Token")
                withClue("Two Warrior tokens should have been created on attack") {
                    warriors.size shouldBe 2
                }
                withClue("Each Warrior token should be tapped and attacking") {
                    warriors.forEach { token ->
                        game.state.getEntity(token)?.has<TappedComponent>() shouldBe true
                        game.state.getEntity(token)?.has<AttackingComponent>() shouldBe true
                    }
                }
            }

            test("the tokens are sacrificed at the next end step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dalkovan Encampment", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val encampment = game.findPermanent("Dalkovan Encampment")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = encampment,
                        abilityId = mobilizeAbilityId
                    )
                )
                game.resolveStack()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.resolveStack()

                withClue("Two Warrior tokens exist during combat") {
                    game.findPermanents("Warrior Token").size shouldBe 2
                }

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                withClue("Warrior tokens are sacrificed by the next end step") {
                    game.findPermanents("Warrior Token").size shouldBe 0
                }
            }
        }
    }
}
