package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Trusty Boomerang exercises [com.wingedsheep.sdk.scripting.targets.EffectTarget.GrantingSource]:
 * an Equipment grants its bearer "{1}, {T}: Tap target creature. Return Trusty Boomerang to its
 * owner's hand." The granted ability's source is the *host creature* (so `{T}` taps the creature),
 * but "Return Trusty Boomerang" names the *Equipment* — resolved via `GrantingSource`, the permanent
 * whose static granted the ability.
 */
class TrustyBoomerangScenarioTest : ScenarioTestBase() {

    /** Find the granted "{1}, {T}: ..." ability surfaced on [host], returning its abilityId. */
    private fun TestGame.grantedAbilityIdOn(host: EntityId): AbilityId =
        getLegalActions(1)
            .mapNotNull { it.action as? ActivateAbility }
            .first { it.sourceId == host }
            .abilityId

    init {
        test("granted ability taps a creature and returns the Equipment (not the host) to hand") {
            val game = scenario()
                .withPlayers("Alice", "Bob")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardAttachedTo(1, "Trusty Boomerang", "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 1)
                .withCardOnBattlefield(2, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val boomerang = game.findPermanent("Trusty Boomerang")!!
            val giant = game.findPermanent("Hill Giant")!!

            val abilityId = game.grantedAbilityIdOn(bears)
            val result = game.execute(
                ActivateAbility(game.player1Id, bears, abilityId, targets = listOf(ChosenTarget.Permanent(giant)))
            )
            withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
            if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("Target creature an opponent controls is tapped") {
                game.state.getEntity(giant)?.get<TappedComponent>().shouldNotBeNull()
            }
            withClue("GrantingSource returns the Equipment to its owner's hand") {
                game.isInHand(1, "Trusty Boomerang") shouldBe true
                game.findPermanent("Trusty Boomerang") shouldBe null
            }
            withClue("The host creature stays on the battlefield (Self != GrantingSource) and is tapped for {T}") {
                game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.state.getEntity(bears)?.get<TappedComponent>().shouldNotBeNull()
            }
        }

        test("GrantingSource returns the attached granter specifically, not any same-named Equipment") {
            // A second, unattached Trusty Boomerang sits on the battlefield granting nothing. Activating
            // the bearer's ability must bounce the *attached* Equipment (the granter), leaving the
            // unattached one — proving GrantingSource resolves the specific granter, not "a Boomerang".
            val game = scenario()
                .withPlayers("Alice", "Bob")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardAttachedTo(1, "Trusty Boomerang", "Grizzly Bears")
                .withCardOnBattlefield(1, "Trusty Boomerang")
                .withLandsOnBattlefield(1, "Island", 1)
                .withCardOnBattlefield(2, "Eager Cadet")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val cadet = game.findPermanent("Eager Cadet")!!
            val unattachedBoomerang = game.findPermanents("Trusty Boomerang")
                .first { game.state.getEntity(it)?.get<AttachedToComponent>() == null }

            val abilityId = game.grantedAbilityIdOn(bears)
            val result = game.execute(
                ActivateAbility(game.player1Id, bears, abilityId, targets = listOf(ChosenTarget.Permanent(cadet)))
            )
            withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
            if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("The attached granter returned to hand; the unattached Boomerang stayed in play") {
                game.isInHand(1, "Trusty Boomerang") shouldBe true
                game.findPermanents("Trusty Boomerang") shouldBe listOf(unattachedBoomerang)
            }
        }
    }
}
