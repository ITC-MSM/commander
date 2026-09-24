package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Countersculpt (Reality Fracture #25) — {U}{U} Instant:
 *   As an additional cost to cast this spell, behold a Jace or pay {1}.
 *   Counter target spell. Empower Jace 1.
 */
class CountersculptScenarioTest : ScenarioTestBase() {

    private fun ScenarioTestBase.TestGame.jaceTokenLoyalty(): Int? =
        findPermanents("Jace").singleOrNull()
            ?.let { state.getEntity(it)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) }

    init {
        test("beholding a Jace card from hand casts it for {U}{U}; counters and empowers Jace 1") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Countersculpt")
                .withCardInHand(1, "Jace Beleren")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            val bolt = game.state.stack.single()

            val hand = game.state.getHand(game.player1Id)
            fun named(n: String) = hand.first { game.state.getEntity(it)?.get<CardComponent>()?.name == n }
            val cast = game.execute(
                CastSpell(
                    game.player1Id, named("Countersculpt"), listOf(ChosenTarget.Spell(bolt)),
                    additionalCostPayment = AdditionalCostPayment(beheldCards = listOf(named("Jace Beleren")))
                )
            )
            withClue("behold leg should be castable with only two Islands: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 20
            game.isInGraveyard(1, "Lightning Bolt") shouldBe true
            game.isInHand(1, "Jace Beleren") shouldBe true
            game.jaceTokenLoyalty() shouldBe 1
        }

        test("without a Jace, paying {1} counters an opponent's spell and creates a Jace token") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Countersculpt")
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(2, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
            game.passPriority()
            game.castSpellTargetingStackSpell(1, "Countersculpt", "Lightning Bolt").error shouldBe null
            game.resolveStack()

            game.getLifeTotal(1) shouldBe 20
            game.isInGraveyard(2, "Lightning Bolt") shouldBe true
            game.jaceTokenLoyalty() shouldBe 1
        }

        test("a Jace planeswalker you control can be beheld; empower still makes a Jace token") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Countersculpt")
                .withCardInHand(1, "Lightning Bolt")
                .withCardOnBattlefield(1, "Jace Beleren")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            val bolt = game.state.stack.single()
            val beleren = game.findPermanent("Jace Beleren")!!
            val countersculpt = game.findCardsInHand(1, "Countersculpt").single()
            game.execute(
                CastSpell(
                    game.player1Id, countersculpt, listOf(ChosenTarget.Spell(bolt)),
                    additionalCostPayment = AdditionalCostPayment(beheldCards = listOf(beleren))
                )
            ).error shouldBe null
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 20
            // Jace Beleren isn't a Jace *token*, so empower creates one and puts the counter there.
            game.isOnBattlefield("Jace Beleren") shouldBe true
            game.findPermanents("Jace").size shouldBe 1
            game.jaceTokenLoyalty() shouldBe 1
        }

        test("with no Jace and only {U}{U} available it can't be cast") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Countersculpt")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.castSpellTargetingStackSpell(1, "Countersculpt", "Lightning Bolt").error shouldNotBe null
        }
    }
}
