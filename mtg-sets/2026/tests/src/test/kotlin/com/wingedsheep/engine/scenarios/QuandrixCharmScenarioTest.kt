package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Quandrix Charm (Secrets of Strixhaven #217).
 *
 * Quandrix Charm ({G}{U} Instant), Choose one —
 *   • Counter target spell unless its controller pays {2}.
 *   • Destroy target enchantment.
 *   • Target creature has base power and toughness 5/5 until end of turn.
 */
class QuandrixCharmScenarioTest : ScenarioTestBase() {

    init {
        context("Quandrix Charm — choose one") {

            test("mode 1: counters a spell whose controller cannot pay {2}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(2, "Grizzly Bears")
                    .withCardInHand(1, "Quandrix Charm")
                    .withLandsOnBattlefield(2, "Forest", 2)        // tapped out after Bears
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Opponent casts Grizzly Bears, spending all their mana.
                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.execute(PassPriority(game.player2Id))

                val spellOnStack = game.state.stack.first { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                val charmId = game.state.getHand(game.player1Id).first { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Quandrix Charm"
                }

                game.execute(
                    CastSpell(
                        game.player1Id,
                        charmId,
                        listOf(ChosenTarget.Spell(spellOnStack)),
                        chosenModes = listOf(0),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Spell(spellOnStack))),
                    )
                ).error shouldBe null
                game.resolveStack()
                // Opponent is tapped out, so the unless-pay prompt resolves to a counter.
                if (game.hasPendingDecision()) game.answerYesNo(false)
                game.resolveStack()

                withClue("Grizzly Bears was countered into the graveyard") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }

            test("mode 2: destroy target enchantment") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Quandrix Charm")
                    .withCardOnBattlefield(2, "Pacifism")          // an enchantment
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val pacifism = game.findPermanent("Pacifism")!!
                game.castSpellWithMode(1, "Quandrix Charm", modeIndex = 1, targetId = pacifism).error shouldBe null
                game.resolveStack()

                withClue("Pacifism was destroyed") {
                    game.findPermanent("Pacifism") shouldBe null
                    game.isInGraveyard(2, "Pacifism") shouldBe true
                }
            }

            test("mode 2: a creature is not a legal target for the destroy-enchantment mode") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Quandrix Charm")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.castSpellWithMode(1, "Quandrix Charm", modeIndex = 1, targetId = bears)

                withClue("Grizzly Bears is not an enchantment — illegal target") {
                    (result.error != null) shouldBe true
                }
            }

            test("mode 3: target creature has base power and toughness 5/5 until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Quandrix Charm")
                    .withCardOnBattlefield(1, "Grizzly Bears")     // 2/2
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.projectedState.getPower(bears) shouldBe 2
                game.state.projectedState.getToughness(bears) shouldBe 2

                game.castSpellWithMode(1, "Quandrix Charm", modeIndex = 2, targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears is now a 5/5") {
                    game.state.projectedState.getPower(bears) shouldBe 5
                    game.state.projectedState.getToughness(bears) shouldBe 5
                }
            }
        }
    }
}
