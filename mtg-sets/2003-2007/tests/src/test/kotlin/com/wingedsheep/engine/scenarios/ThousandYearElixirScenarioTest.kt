package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lrw.cards.ThousandYearElixir
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Thousand-Year Elixir (LRW #263) —
 *   "You may activate abilities of creatures you control as though those creatures had haste."
 *   "{1}, {T}: Untap target creature."
 *
 * The permission itself is engine-tested in `ActivateAbilitiesAsThoughHastyTest`; these cover the
 * card — that a summoning-sick creature's `{T}` ability is offered only while the Elixir is out,
 * that it grants no attack rights, and that the untap ability untaps its target.
 */
class ThousandYearElixirScenarioTest : ScenarioTestBase() {

    private val dummy = card("Elixir Test Dummy") {
        manaCost = "{1}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(1)
            description = "{T}: You gain 1 life."
        }
    }

    private val dummyTapAbilityId = dummy.activatedAbilities.single().id
    private val untapAbilityId = ThousandYearElixir.activatedAbilities.single().id

    private fun activationsOf(game: TestGame, sourceId: EntityId, abilityId: AbilityId) =
        game.getLegalActions(1)
            .mapNotNull { it.action as? ActivateAbility }
            .filter { it.sourceId == sourceId && it.abilityId == abilityId }

    init {
        cardRegistry.register(dummy)

        context("Thousand-Year Elixir — the as-though-haste permission") {

            test("a summoning-sick creature's {T} ability is activatable with the Elixir out") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Thousand-Year Elixir")
                    .withCardOnBattlefield(1, "Elixir Test Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dummyId = game.findPermanent("Elixir Test Dummy")!!
                activationsOf(game, dummyId, dummyTapAbilityId).size shouldBe 1

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = dummyId, abilityId = dummyTapAbilityId)
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()
                game.getLifeTotal(1) shouldBe 21
            }

            test("without the Elixir the same {T} ability is not offered") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Elixir Test Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dummyId = game.findPermanent("Elixir Test Dummy")!!
                activationsOf(game, dummyId, dummyTapAbilityId).size shouldBe 0
            }

            test("it grants no attack rights") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Thousand-Year Elixir")
                    .withCardOnBattlefield(1, "Elixir Test Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val offeredAttackers = game.getLegalActions(1)
                    .firstOrNull { it.actionType == "DeclareAttackers" }
                    ?.validAttackers.orEmpty()
                withClue("the 2007-10-01 ruling: it doesn't let you attack as though they had haste") {
                    offeredAttackers shouldBe emptyList()
                }
            }
        }

        context("Thousand-Year Elixir — {1}, {T}: Untap target creature") {

            test("untaps the targeted creature and taps the Elixir") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Thousand-Year Elixir")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elixir = game.findPermanent("Thousand-Year Elixir")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = elixir,
                        abilityId = untapAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                game.state.getEntity(elixir)?.has<TappedComponent>() shouldBe true
            }
        }
    }
}
