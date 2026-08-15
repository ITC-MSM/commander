package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Fishing Pole — the granted "{1}, {T}, Tap Fishing Pole: Put a bait counter on Fishing Pole"
 * plus the "whenever equipped creature becomes untapped" payoff.
 *
 * Three separate objects are touched by one activation: `{T}` taps the *creature*,
 * "Tap Fishing Pole" taps the *Equipment*
 * ([com.wingedsheep.sdk.scripting.AbilityCost.TapGrantingPermanent]), and the counter goes on the
 * *Equipment* ([com.wingedsheep.sdk.scripting.targets.EffectTarget.GrantingSource]). These tests
 * pin all three, plus the "if you do" gate that makes no Fish without a bait counter.
 */
class FishingPoleScenarioTest : ScenarioTestBase() {

    private fun TestGame.baitCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.BAIT) ?: 0

    /** The bait ability the Fishing Pole's static grants to its equipped creature. */
    private val baitAbilityId by lazy {
        cardRegistry.requireCard("Fishing Pole").script.staticAbilities
            .filterIsInstance<GrantActivatedAbility>().first().ability.id
    }

    init {
        test("the granted ability taps both the creature and the Equipment, and baits the pole") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardAttachedTo(1, "Fishing Pole", "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val pole = game.findPermanent("Fishing Pole")!!

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = bears,
                    abilityId = baitAbilityId,
                )
            ).error shouldBe null
            game.resolveStack()

            game.state.getEntity(bears)!!.has<TappedComponent>() shouldBe true
            game.state.getEntity(pole)!!.has<TappedComponent>() shouldBe true
            game.baitCounters(pole) shouldBe 1
        }

        test("the ability can't be activated again while the Equipment is tapped") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardAttachedTo(1, "Fishing Pole", "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 4)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val pole = game.findPermanent("Fishing Pole")!!
            val abilityId = baitAbilityId

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = bears, abilityId = abilityId)
            ).error shouldBe null
            game.resolveStack()

            // Untap only the creature: the {T} half would be payable again, but the pole is tapped.
            game.state = game.state.updateEntity(bears) { it.without<TappedComponent>() }

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = bears, abilityId = abilityId)
            ).error shouldNotBe null
            game.baitCounters(pole) shouldBe 1
        }

        test("the equipped creature untapping spends a bait counter for a 1/1 blue Fish") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
                .withCardAttachedTo(1, "Fishing Pole", "Grizzly Bears")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                // Start on the opponent's turn so the next untap step is ours.
                .withActivePlayer(2)
                .withPriorityPlayer(2)
                .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                .build()

            val pole = game.findPermanent("Fishing Pole")!!
            game.state = game.state.updateEntity(pole) { container ->
                val existing = container.get<CountersComponent>() ?: CountersComponent()
                container.with(existing.withAdded(CounterType.BAIT, 1))
            }

            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.resolveStack()

            game.baitCounters(pole) shouldBe 0
            game.findPermanent("Fish Token") shouldNotBe null
        }

        test("no bait counter means no Fish") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
                .withCardAttachedTo(1, "Fishing Pole", "Grizzly Bears")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(2)
                .withPriorityPlayer(2)
                .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                .build()

            val pole = game.findPermanent("Fishing Pole")!!
            game.baitCounters(pole) shouldBe 0

            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.resolveStack()

            // The trigger still fires; the "if you do" gate withholds the token.
            game.findPermanent("Fish Token") shouldBe null
        }
    }
}
