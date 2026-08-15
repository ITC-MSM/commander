package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Spiked Ripsaw (VOW #220) — {2}{G} Artifact — Equipment
 *
 *   Equipped creature gets +3/+3.
 *   Whenever equipped creature attacks, you may sacrifice a Forest. If you do, that creature gains
 *   trample until end of turn.
 *   Equip {3}
 *
 * Exercises the +3/+3 static and the attack-triggered optional-sacrifice: sacrificing a Forest
 * grants the attacker trample until end of turn; declining sacrifices nothing and grants nothing.
 */
class SpikedRipsawScenarioTest : ScenarioTestBase() {

    init {
        context("Spiked Ripsaw — +3/+3 and attack-triggered Forest sacrifice for trample") {

            test("equipped creature is +3/+3") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Spiked Ripsaw", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("base 2/2 + 3/3 = 5/5") {
                    game.state.projectedState.getPower(bears) shouldBe 5
                    game.state.projectedState.getToughness(bears) shouldBe 5
                }
                withClue("no trample before the attack trigger resolves") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe false
                }
            }

            test("attacking and sacrificing a Forest grants trample until end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Spiked Ripsaw", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("the attack trigger offers the up-to-one Forest sacrifice") {
                    game.hasPendingDecision() shouldBe true
                }
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("the lone Forest is the only sacrifice option") {
                    decision.options.size shouldBe 1
                }

                game.selectCards(listOf(decision.options.first())).error shouldBe null
                game.resolveStack()

                withClue("the Forest was sacrificed") {
                    game.isInGraveyard(1, "Forest") shouldBe true
                }
                withClue("the equipped creature gained trample") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
                }
            }

            test("declining the sacrifice keeps the Forest and grants no trample") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Spiked Ripsaw", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                // "you may" — decline by selecting no Forest.
                game.skipSelection().error shouldBe null
                game.resolveStack()

                withClue("the Forest was not sacrificed") {
                    game.isInGraveyard(1, "Forest") shouldBe false
                    game.isOnBattlefield("Forest") shouldBe true
                }
                withClue("no trample was granted") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe false
                }
            }
        }
    }
}
