package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Theorist's Sanctum (Reality Fracture #191) — Land — Island:
 *   ({T}: Add {U}.)
 *   As this land enters, you may behold a Jace. If you don't, this land enters tapped.
 *   {2}{U}, {T}: Empower Jace 2.
 *
 * Pins `Effects.Behold(..., otherwise = ...)`: beholding keeps the land untapped, declining or
 * having nothing to behold taps it.
 */
class TheoristsSanctumScenarioTest : ScenarioTestBase() {

    private fun ScenarioTestBase.TestGame.isTapped(id: com.wingedsheep.sdk.model.EntityId) =
        state.getEntity(id)?.has<TappedComponent>() == true

    init {
        test("beholding a Jace card from hand lets it enter untapped") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Theorist's Sanctum")
                .withCardInHand(1, "Jace Beleren")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val sanctum = game.findCardsInHand(1, "Theorist's Sanctum").single()
            val jace = game.findCardsInHand(1, "Jace Beleren").single()
            game.execute(PlayLand(game.player1Id, sanctum)).error shouldBe null

            val decision = game.getPendingDecision()
            decision.shouldBeInstanceOf<SelectCardsDecision>()
            decision.options shouldBe listOf(jace)
            game.selectCards(listOf(jace)).error shouldBe null

            game.isOnBattlefield("Theorist's Sanctum") shouldBe true
            game.isTapped(sanctum) shouldBe false
            game.isInHand(1, "Jace Beleren") shouldBe true
        }

        test("beholding a Jace permanent you control also works") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Theorist's Sanctum")
                .withCardOnBattlefield(1, "Jace Beleren")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val sanctum = game.findCardsInHand(1, "Theorist's Sanctum").single()
            val jace = game.findPermanent("Jace Beleren")!!
            game.execute(PlayLand(game.player1Id, sanctum)).error shouldBe null
            game.selectCards(listOf(jace)).error shouldBe null

            game.isTapped(sanctum) shouldBe false
        }

        test("declining to behold makes it enter tapped") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Theorist's Sanctum")
                .withCardInHand(1, "Jace Beleren")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val sanctum = game.findCardsInHand(1, "Theorist's Sanctum").single()
            game.execute(PlayLand(game.player1Id, sanctum)).error shouldBe null
            game.skipSelection().error shouldBe null

            game.isOnBattlefield("Theorist's Sanctum") shouldBe true
            game.isTapped(sanctum) shouldBe true
        }

        test("with no Jace to behold it enters tapped without a prompt") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Theorist's Sanctum")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val sanctum = game.findCardsInHand(1, "Theorist's Sanctum").single()
            game.execute(PlayLand(game.player1Id, sanctum)).error shouldBe null

            game.hasPendingDecision() shouldBe false
            game.isTapped(sanctum) shouldBe true
        }

        test("{2}{U}, {T}: empower Jace 2 creates a Jace token with two loyalty") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Theorist's Sanctum")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val sanctum = game.findPermanent("Theorist's Sanctum")!!
            val empower = game.getLegalActions(1)
                .first { (it.action as? ActivateAbility)?.sourceId == sanctum && it.description.contains("Empower") }
            game.execute(empower.action).error shouldBe null
            game.resolveStack()

            val jaceToken = game.findPermanents("Jace").single()
            game.state.getEntity(jaceToken)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) shouldBe 2
            game.isTapped(sanctum) shouldBe true
        }
    }
}
