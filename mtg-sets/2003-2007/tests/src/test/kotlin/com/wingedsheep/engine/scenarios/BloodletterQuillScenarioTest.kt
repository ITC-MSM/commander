package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.BloodletterQuill
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Bloodletter Quill (RAV #254) — "{2}, {T}, Put a blood counter on this artifact: Draw a card, then
 * you lose 1 life for each blood counter on this artifact." / "{U}{B}: Remove a blood counter."
 *
 * Two things are worth proving, and the second is the trap. The counter is an *activation cost*, so
 * it is on the Quill before the ability resolves and the very first draw already costs a life. And
 * the life loss counts **blood** counters specifically — a named counter type whose
 * `CounterType` enum entry is load-bearing, because the amount resolver falls back to +1/+1
 * counters for a name it doesn't recognise. A Quill carrying +1/+1 counters and blood counters is
 * the cheapest way to tell the two apart.
 */
class BloodletterQuillScenarioTest : ScenarioTestBase() {

    private val drawAbility = BloodletterQuill.activatedAbilities.first().id
    private val removeAbility = BloodletterQuill.activatedAbilities.last().id

    init {
        context("Bloodletter Quill") {

            fun TestGame.bloodCounters(quill: EntityId): Int =
                state.getEntity(quill)?.get<CountersComponent>()?.getCount(CounterType.BLOOD) ?: 0

            fun TestGame.seed(quill: EntityId, type: CounterType, count: Int) {
                state = state.updateEntity(quill) { container ->
                    val existing = container.get<CountersComponent>() ?: CountersComponent()
                    container.with(existing.withAdded(type, count))
                }
            }

            test("the first activation taps it, banks a blood counter, draws, and costs 1 life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bloodletter Quill")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val quill = game.findPermanent("Bloodletter Quill")!!
                val handBefore = game.handSize(1)

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = quill, abilityId = drawAbility)
                ).error shouldBe null

                withClue("the counter and the tap are costs — both land before resolution") {
                    game.bloodCounters(quill) shouldBe 1
                    game.state.getEntity(quill)!!.has<TappedComponent>() shouldBe true
                }

                game.resolveStack()

                withClue("one card drawn, one life lost for the one blood counter") {
                    game.handSize(1) shouldBe handBefore + 1
                    game.getLifeTotal(1) shouldBe 19
                }
            }

            test("the life loss scales with the blood counters, and ignores other counter types") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bloodletter Quill")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val quill = game.findPermanent("Bloodletter Quill")!!
                game.seed(quill, CounterType.BLOOD, 2)
                // A decoy: if the amount silently resolved +1/+1 counters instead of blood ones,
                // this would make the life loss 7 rather than 3.
                game.seed(quill, CounterType.PLUS_ONE_PLUS_ONE, 7)

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = quill, abilityId = drawAbility)
                ).error shouldBe null
                game.resolveStack()

                withClue("two banked plus the cost's own makes three blood counters, so 3 life") {
                    game.bloodCounters(quill) shouldBe 3
                    game.getLifeTotal(1) shouldBe 17
                }
            }

            test("the {U}{B} ability takes a blood counter back off") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bloodletter Quill")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val quill = game.findPermanent("Bloodletter Quill")!!
                game.seed(quill, CounterType.BLOOD, 2)

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = quill, abilityId = removeAbility)
                ).error shouldBe null
                game.resolveStack()

                withClue("the removal is the effect, not a cost — and it does not tap the Quill") {
                    game.bloodCounters(quill) shouldBe 1
                    game.state.getEntity(quill)!!.has<TappedComponent>() shouldBe false
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}
