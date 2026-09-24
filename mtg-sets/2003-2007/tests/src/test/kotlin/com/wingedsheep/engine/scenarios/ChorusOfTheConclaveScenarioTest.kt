package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Chorus of the Conclave (RAV #195) — "As an additional cost to cast creature spells, you may pay
 * any amount of mana. If you do, that creature enters with that many additional +1/+1 counters on
 * it."
 *
 * Pins the cast-time payment: N extra generic mana buys N counters; zero declines; the offer is
 * advertised only on creature spells while Chorus is out; a forged payment without Chorus, on a
 * noncreature spell, negative, or beyond what the caster can pay is refused; and once paid the
 * counters arrive even if Chorus leaves before the creature resolves.
 */
class ChorusOfTheConclaveScenarioTest : ScenarioTestBase() {

    private fun board(withChorus: Boolean = true, forests: Int = 5): TestGame {
        val builder = scenario()
            .withPlayers("Caster", "Opponent")
            .withCardInHand(1, "Grizzly Bears")
            .withCardInHand(1, "Giant Growth")
            .withLandsOnBattlefield(1, "Forest", forests)
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        if (withChorus) builder.withCardOnBattlefield(1, "Chorus of the Conclave")
        return builder.build()
    }

    private fun TestGame.cast(name: String, extra: Int) = execute(
        CastSpell(
            playerId = player1Id,
            cardId = findCardsInHand(1, name).single(),
            additionalManaForCounters = extra
        )
    )

    private fun TestGame.plusOnes(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun TestGame.tappedForests(): Int =
        findPermanents("Forest").count { state.getEntity(it)!!.has<TappedComponent>() }

    init {
        test("paying three extra mana makes the creature enter with three +1/+1 counters") {
            val game = board()
            game.cast("Grizzly Bears", 3).error shouldBe null
            withClue("{1}{G} plus {3} taps all five Forests") { game.tappedForests() shouldBe 5 }
            game.resolveStack()

            val bears = game.findPermanent("Grizzly Bears")!!
            game.plusOnes(bears) shouldBe 3
            game.state.projectedState.getPower(bears) shouldBe 5
        }

        test("paying nothing is declining — no counters, no extra mana") {
            val game = board()
            game.cast("Grizzly Bears", 0).error shouldBe null
            game.tappedForests() shouldBe 2
            game.resolveStack()
            game.plusOnes(game.findPermanent("Grizzly Bears")!!) shouldBe 0
        }

        test("the offer is advertised on creature spells only, bounded by the available mana") {
            val game = board()
            val offers = game.getLegalActions(1).filter { it.action is CastSpell }
            val bears = offers.single { (it.action as CastSpell).cardId == game.findCardsInHand(1, "Grizzly Bears").single() }
            val growth = offers.single { (it.action as CastSpell).cardId == game.findCardsInHand(1, "Giant Growth").single() }
            withClue("five Forests minus the Bears' two leaves three to spend") {
                bears.maxAdditionalManaForCounters shouldBe 3
            }
            withClue("Giant Growth is not a creature spell") { growth.maxAdditionalManaForCounters shouldBe null }
        }

        test("forged payments are refused") {
            withClue("no Chorus on the battlefield") {
                board(withChorus = false).cast("Grizzly Bears", 1).error shouldNotBe null
            }
            withClue("a noncreature spell") {
                board().cast("Giant Growth", 1).error shouldNotBe null
            }
            withClue("a negative amount") {
                board().cast("Grizzly Bears", -1).error shouldNotBe null
            }
            withClue("more than the caster can pay") {
                board().cast("Grizzly Bears", 4).error shouldNotBe null
            }
        }

        test("once paid, the counters arrive even if Chorus has left the battlefield") {
            val game = board()
            game.cast("Grizzly Bears", 2).error shouldBe null
            val chorus = game.findPermanent("Chorus of the Conclave")!!
            game.state = game.state.removeFromZone(
                com.wingedsheep.engine.state.ZoneKey(game.player1Id, com.wingedsheep.sdk.core.Zone.BATTLEFIELD), chorus
            )
            game.resolveStack()
            game.plusOnes(game.findPermanent("Grizzly Bears")!!) shouldBe 2
        }
    }
}
