package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Hapatra, the Desert Fang (FRA #271) — {2}{B}{B}{G} Legendary Creature — Human Cleric 3/3.
 *
 *   When Hapatra enters, for each opponent, put X -1/-1 counters on up to one target creature that
 *   player controls, where X is the greatest mana value among cards in your graveyard.
 */
class HapatraTheDesertFangScenarioTest : ScenarioTestBase() {

    private fun TestGame.minusCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.MINUS_ONE_MINUS_ONE) ?: 0

    private fun ScenarioBuilder.hapatraBoard(): ScenarioBuilder = this
        .withPlayers("Player1", "Player2")
        .withLandsOnBattlefield(1, "Swamp", 3)
        .withLandsOnBattlefield(1, "Forest", 2)
        .withCardInHand(1, "Hapatra, the Desert Fang")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withCardOnBattlefield(2, "Craw Wurm")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    /** Cast Hapatra and walk the enters trigger, picking [choose] from the offered targets. */
    private fun TestGame.castHapatra(choose: (List<EntityId>) -> List<EntityId>): List<EntityId>? {
        castSpell(1, "Hapatra, the Desert Fang").error shouldBe null
        var offered: List<EntityId>? = null
        var guard = 0
        while (guard++ < 40) {
            when (val decision = state.pendingDecision) {
                is SelectManaSourcesDecision -> submitManaSourcesAutoPay()
                is ChooseTargetsDecision -> {
                    offered = decision.legalTargets[0] ?: emptyList()
                    selectTargets(choose(offered))
                }
                null -> {
                    if (state.stack.isEmpty()) return offered
                    resolveStack()
                }
                else -> error("unexpected decision: $decision")
            }
        }
        error("decision loop did not settle")
    }

    init {
        test("puts X -1/-1 counters on the opponent's creature, X = greatest mana value in your graveyard") {
            val game = scenario().hapatraBoard()
                .withCardInGraveyard(1, "Grizzly Bears") // MV 2
                .withCardInGraveyard(1, "Savannah Lions") // MV 1
                .build()
            val wurm = game.findPermanent("Craw Wurm")!! // 6/4
            val bears = game.findPermanent("Grizzly Bears")!!

            val offered = game.castHapatra { listOf(wurm) }

            withClue("only the opponent's creature is offered") {
                offered!! shouldContain wurm
                offered shouldNotContain bears
            }
            withClue("X is the greatest mana value (2), not the sum") {
                game.minusCounters(wurm) shouldBe 2
            }
            game.state.projectedState.getPower(wurm) shouldBe 4
            game.state.projectedState.getToughness(wurm) shouldBe 2
            game.minusCounters(bears) shouldBe 0
        }

        test("enough counters kill the creature") {
            val game = scenario().hapatraBoard()
                .withCardInGraveyard(1, "Hill Giant") // MV 4
                .build()
            val wurm = game.findPermanent("Craw Wurm")!!

            game.castHapatra { listOf(wurm) }

            game.isInGraveyard(2, "Craw Wurm") shouldBe true
        }

        test("with an empty graveyard X is 0 and nothing changes") {
            val game = scenario().hapatraBoard().build()
            val wurm = game.findPermanent("Craw Wurm")!!

            game.castHapatra { listOf(wurm) }

            game.minusCounters(wurm) shouldBe 0
            game.isOnBattlefield("Craw Wurm") shouldBe true
        }

        test("up to one — choosing no target puts no counters") {
            val game = scenario().hapatraBoard()
                .withCardInGraveyard(1, "Hill Giant")
                .build()
            val wurm = game.findPermanent("Craw Wurm")!!

            game.castHapatra { emptyList() }

            game.minusCounters(wurm) shouldBe 0
        }
    }
}
