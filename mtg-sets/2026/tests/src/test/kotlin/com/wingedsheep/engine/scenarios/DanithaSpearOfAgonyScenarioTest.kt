package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Danitha, Spear of Agony (FRA #227) — "Whenever you cast a spell that targets an opponent or a
 * creature an opponent controls, put a +1/+1 counter on Danitha."
 *
 * Pins `SpellCastPredicate.TargetsOpponent` (the player half) joined to `TargetsMatching` by
 * `AnyOf`; a spell aimed at yourself or your own creature does nothing.
 */
class DanithaSpearOfAgonyScenarioTest : ScenarioTestBase() {
    init {
        fun game() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Danitha, Spear of Agony")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Grizzly Bears")
            .withCardInHand(1, "Lightning Bolt")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun counters(game: TestGame): Int {
            val danitha = game.findPermanent("Danitha, Spear of Agony")!!
            return game.state.getEntity(danitha)?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        }

        test("targeting an opponent adds a counter") {
            val game = game()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()
            counters(game) shouldBe 1
        }

        test("targeting a creature an opponent controls adds a counter") {
            val game = game()
            val theirs = game.findPermanents("Grizzly Bears")
                .single { game.state.projectedState.getController(it) == game.player2Id }
            game.castSpell(1, "Lightning Bolt", theirs).error shouldBe null
            game.resolveStack()
            counters(game) shouldBe 1
        }

        test("targeting yourself or your own creature adds nothing") {
            val atMe = game()
            atMe.castSpellTargetingPlayer(1, "Lightning Bolt", 1).error shouldBe null
            atMe.resolveStack()
            counters(atMe) shouldBe 0

            val atMine = game()
            val mine = atMine.findPermanents("Grizzly Bears")
                .single { atMine.state.projectedState.getController(it) == atMine.player1Id }
            atMine.castSpell(1, "Lightning Bolt", mine).error shouldBe null
            atMine.resolveStack()
            counters(atMine) shouldBe 0
        }
    }
}
