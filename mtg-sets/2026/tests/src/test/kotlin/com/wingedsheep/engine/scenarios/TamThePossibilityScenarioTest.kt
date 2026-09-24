package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tam, the Possibility (FRA #276) — {1}{G}{U} Legendary Creature — Gorgon Wizard 2/4:
 *   Planeswalker spells you cast cost {1} less to cast.
 *   {W}{U}{B}{R}{G}, {T}: Proliferate X times, where X is the number of planeswalker types among
 *   planeswalkers you control.
 *
 * Pins the cost reduction, that X counts planeswalker *types* (two Jaces are one type), that each
 * proliferate is its own choice, and that an opponent's planeswalker adds nothing to X.
 */
class TamThePossibilityScenarioTest : ScenarioTestBase() {

    private fun TestGame.loyalty(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    private val tamAbility get() = cardRegistry.getCard("Tam, the Possibility")!!.script.activatedAbilities.single().id

    private fun ScenarioBuilder.withFiveColors(): ScenarioBuilder = this
        .withCardOnBattlefield(1, "Plains")
        .withCardOnBattlefield(1, "Island")
        .withCardOnBattlefield(1, "Swamp")
        .withCardOnBattlefield(1, "Mountain")
        .withCardOnBattlefield(1, "Forest")

    init {
        test("planeswalker spells cost {1} less") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Tam, the Possibility")
                .withCardInHand(1, "Jace Beleren")
                .withLandsOnBattlefield(1, "Island", 2)
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            withClue("{1}{U}{U} Jace Beleren is castable off two Islands") {
                game.castSpell(1, "Jace Beleren").error shouldBe null
            }
            game.resolveStack()
            game.isOnBattlefield("Jace Beleren") shouldBe true
        }

        test("two Jaces and a Liliana are two planeswalker types: proliferate twice") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Tam, the Possibility")
                .withCardOnBattlefield(1, "Jace Beleren")
                .withCardOnBattlefield(1, "The Theorist, Jace Beleren")
                .withCardOnBattlefield(1, "Liliana Vess")
                .withCardOnBattlefield(2, "Garruk Wildspeaker")
                .withFiveColors()
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val tam = game.findPermanent("Tam, the Possibility")!!
            val jace = game.findPermanent("Jace Beleren")!!
            val liliana = game.findPermanent("Liliana Vess")!!
            val garruk = game.findPermanent("Garruk Wildspeaker")!!
            val jaceStart = game.loyalty(jace)
            val lilianaStart = game.loyalty(liliana)
            val garrukStart = game.loyalty(garruk)

            game.execute(ActivateAbility(game.player1Id, tam, tamAbility)).error shouldBe null
            game.resolveStack()

            withClue("first proliferate") { game.hasPendingDecision() shouldBe true }
            game.selectCards(listOf(jace, liliana)).error shouldBe null
            game.resolveStack()

            withClue("second proliferate — a separate choice") { game.hasPendingDecision() shouldBe true }
            game.selectCards(listOf(jace)).error shouldBe null
            game.resolveStack()

            withClue("the opponent's Garruk is not a third type, so there is no third proliferate") {
                game.hasPendingDecision() shouldBe false
            }
            game.loyalty(jace) shouldBe jaceStart + 2
            game.loyalty(liliana) shouldBe lilianaStart + 1
            game.loyalty(garruk) shouldBe garrukStart
        }

        test("no planeswalkers means X = 0: nothing to proliferate") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Tam, the Possibility")
                .withCardOnBattlefield(2, "Garruk Wildspeaker")
                .withFiveColors()
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val tam = game.findPermanent("Tam, the Possibility")!!
            val garruk = game.findPermanent("Garruk Wildspeaker")!!
            val garrukStart = game.loyalty(garruk)

            game.execute(ActivateAbility(game.player1Id, tam, tamAbility)).error shouldBe null
            game.resolveStack()

            game.hasPendingDecision() shouldBe false
            game.loyalty(garruk) shouldBe garrukStart
        }
    }
}
