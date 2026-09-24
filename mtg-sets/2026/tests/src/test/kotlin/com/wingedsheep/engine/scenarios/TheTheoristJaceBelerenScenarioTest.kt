package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The Theorist, Jace Beleren (FRA #43) — {2}{U}{U} Legendary Planeswalker — Jace, loyalty 3.
 *
 *   At the beginning of each opponent's draw step, you draw a card.
 *   +1: Create a 1/1 blue Illusion creature token.
 *   −2: For each opponent, return up to one target artifact or creature that player controls to
 *       its owner's hand.
 *   −6: Draw three cards. Then put X +1/+1 counters on each creature you control, where X is the
 *       number of cards in your hand.
 */
class TheTheoristJaceBelerenScenarioTest : ScenarioTestBase() {

    private val jaceName = "The Theorist, Jace Beleren"

    private fun abilityId(change: Int) = cardRegistry.getCard(jaceName)!!.script.activatedAbilities
        .single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

    private fun TestGame.counters(id: EntityId, type: CounterType): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    init {
        test("draws a card at the beginning of each opponent's draw step") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, jaceName)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(2)
                .withTurnNumber(2)
                .inPhase(Phase.BEGINNING, Step.UPKEEP)
                .build()

            game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
            game.resolveStack()

            game.handSize(1) shouldBe 1
            withClue("the opponent drew only their own card") { game.handSize(2) shouldBe 1 }
        }

        test("does not trigger on your own draw step") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, jaceName)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .withTurnNumber(3)
                .inPhase(Phase.BEGINNING, Step.UPKEEP)
                .build()

            game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
            game.resolveStack()

            game.handSize(1) shouldBe 1
            game.state.stack.isEmpty() shouldBe true
        }

        test("+1 creates a 1/1 blue Illusion") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, jaceName)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val jace = game.findPermanent(jaceName)!!

            game.execute(ActivateAbility(game.player1Id, jace, abilityId(1))).error shouldBe null
            game.resolveStack()

            val illusion = game.findPermanent("Illusion Token")
            illusion shouldNotBe null
            game.state.projectedState.getPower(illusion!!) shouldBe 1
            game.state.projectedState.getToughness(illusion) shouldBe 1
            game.counters(jace, CounterType.LOYALTY) shouldBe 4
        }

        test("−2 returns an opponent's artifact to its owner's hand, and can't target your own creature") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, jaceName)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Sol Ring")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val jace = game.findPermanent(jaceName)!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val ring = game.findPermanent("Sol Ring")!!

            withClue("your own creature is not a legal target") {
                game.execute(
                    ActivateAbility(game.player1Id, jace, abilityId(-2), targets = listOf(ChosenTarget.Permanent(bears)))
                ).error shouldNotBe null
            }

            game.execute(
                ActivateAbility(game.player1Id, jace, abilityId(-2), targets = listOf(ChosenTarget.Permanent(ring)))
            ).error shouldBe null
            game.resolveStack()

            game.isInHand(2, "Sol Ring") shouldBe true
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.counters(jace, CounterType.LOYALTY) shouldBe 1
        }

        test("−2 returns an opponent's creature, and with no target does nothing") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, jaceName)
                .withCardOnBattlefield(2, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val jace = game.findPermanent(jaceName)!!
            val giant = game.findPermanent("Hill Giant")!!

            game.execute(
                ActivateAbility(game.player1Id, jace, abilityId(-2), targets = listOf(ChosenTarget.Permanent(giant)))
            ).error shouldBe null
            game.resolveStack()
            game.isInHand(2, "Hill Giant") shouldBe true
        }

        test("−6 draws three, then puts X +1/+1 counters on each creature you control, X = hand size") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, jaceName)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Savannah Lions")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInHand(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val jace = game.findPermanent(jaceName)!!
            game.state = game.state.updateEntity(jace) {
                it.with(CountersComponent(mapOf(CounterType.LOYALTY to 6)))
            }

            game.execute(ActivateAbility(game.player1Id, jace, abilityId(-6))).error shouldBe null
            game.resolveStack()

            game.handSize(1) shouldBe 4
            val bears = game.findPermanent("Grizzly Bears")!!
            val lions = game.findPermanent("Savannah Lions")!!
            val giant = game.findPermanent("Hill Giant")!!
            game.counters(bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 4
            game.counters(lions, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 4
            withClue("the opponent's creature gets nothing") {
                game.counters(giant, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 0
            }
        }
    }
}
