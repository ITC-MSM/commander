package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Biblioplex Tomekeeper (Secrets of Strixhaven #247) — {4} Artifact Creature, 3/4.
 *
 * "When this creature enters, choose up to one —
 *  • Target creature becomes prepared. (Only creatures with prepare spells can become prepared.)
 *  • Target creature becomes unprepared."
 *
 * Exercises the modal ETB trigger and the new [com.wingedsheep.sdk.scripting.effects.UnprepareEffect]
 * (mode 2, the inverse of BecomePrepared).
 */
class BiblioplexTomekeeperScenarioTest : ScenarioTestBase() {

    private fun TestGame.exileCopy(name: String): com.wingedsheep.sdk.model.EntityId? =
        state.getZone(ZoneKey(player1Id, Zone.EXILE)).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }

    init {
        context("Biblioplex Tomekeeper ETB modal trigger") {

            test("mode 1 makes a PREPARE-layout creature prepared") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Biblioplex Tomekeeper")
                    .withCardOnBattlefield(1, "Studious First-Year")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val firstYear = game.findPermanent("Studious First-Year")!!
                withClue("Studious First-Year is not prepared before the trigger") {
                    game.state.getEntity(firstYear)?.get<PreparedComponent>() shouldBe null
                }

                game.castSpell(1, "Biblioplex Tomekeeper")
                game.resolveStack()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 0))

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(firstYear))))
                game.resolveStack()

                withClue("Studious First-Year became prepared") {
                    game.state.getEntity(firstYear)?.get<PreparedComponent>() shouldNotBe null
                }
                withClue("a prepare-spell copy was created in exile") {
                    game.exileCopy("Studious First-Year") shouldNotBe null
                }
            }

            test("mode 2 makes a prepared creature unprepared and removes its exile copy") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Biblioplex Tomekeeper")
                    .withCardInHand(1, "Studious First-Year")
                    .withLandsOnBattlefield(1, "Forest", 8)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Studious First-Year enters prepared on its own (it carries Keyword.PREPARED).
                game.castSpell(1, "Studious First-Year")
                game.resolveStack()
                val firstYear = game.findPermanent("Studious First-Year")!!
                withClue("precondition: Studious First-Year is prepared with an exile copy") {
                    game.state.getEntity(firstYear)?.get<PreparedComponent>() shouldNotBe null
                    game.exileCopy("Studious First-Year") shouldNotBe null
                }

                // Biblioplex Tomekeeper unprepares it (mode 2).
                game.castSpell(1, "Biblioplex Tomekeeper")
                game.resolveStack()
                val unprepMode = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(unprepMode.id, optionIndex = 1))
                val unprepTarget = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(unprepTarget.id, mapOf(0 to listOf(firstYear))))
                game.resolveStack()

                withClue("Studious First-Year is no longer prepared") {
                    game.state.getEntity(firstYear)?.get<PreparedComponent>() shouldBe null
                }
                withClue("the orphaned prepare-spell copy was swept from exile") {
                    game.exileCopy("Studious First-Year") shouldBe null
                }
            }
        }
    }
}
