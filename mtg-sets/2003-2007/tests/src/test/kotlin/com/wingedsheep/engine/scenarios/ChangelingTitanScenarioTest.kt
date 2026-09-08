package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Changeling Titan (LRW #200) — changeling, champion a creature, 7/7.
 *
 * The Champion keyword's rules matrix lives in `ChampionKeywordTest`; this pins the card's own
 * wiring and the one thing changeling makes worth restating: being every creature type does not
 * make the Titan a legal choice for its *own* champion ability ("another").
 */
class ChangelingTitanScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Changeling Titan — champion a creature") {

            test("the Titan is never its own champion choice, despite being every creature type") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Titan")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Changeling Titan").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                val titan = game.findPermanent("Changeling Titan")!!
                withClue("'another creature you control' excludes the Titan itself") {
                    decision.options shouldNotContain titan
                }
                decision.options shouldBe listOf(game.findPermanent("Grizzly Bears")!!)
            }

            test("exiling a creature keeps the Titan, and it returns when the Titan dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Titan")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Changeling Titan").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()
                game.isInExile(1, "Grizzly Bears") shouldBe true

                game.castSpell(1, "Doom Blade", game.findPermanent("Changeling Titan")!!).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe true
            }

            test("it is a 7/7 of every creature type") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Changeling Titan")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val titan = game.findPermanent("Changeling Titan")!!
                val projected = stateProjector.project(game.state)
                projected.hasKeyword(titan, Keyword.CHANGELING) shouldBe true
                projected.getPower(titan) shouldBe 7
                projected.getToughness(titan) shouldBe 7
            }
        }
    }
}
