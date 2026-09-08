package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Changeling Hero (LRW #9) — changeling, champion a creature, lifelink.
 *
 * The Champion keyword's rules matrix lives in `ChampionKeywordTest`; this pins the card's own
 * wiring: the champion clause with the printed "a creature" quality, and lifelink surviving it.
 */
class ChangelingHeroScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Changeling Hero — champion a creature, lifelink") {

            test("exiling a creature keeps the Hero, and it returns when the Hero dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Hero")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Changeling Hero").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Changeling Hero") shouldBe true
                game.isInExile(1, "Grizzly Bears") shouldBe true

                game.castSpell(1, "Doom Blade", game.findPermanent("Changeling Hero")!!).error shouldBe null
                game.resolveStack()

                withClue("the linked leaves trigger returns the championed Bears") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("with no other creature the Hero is sacrificed, with no prompt") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Hero")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Changeling Hero").error shouldBe null
                game.resolveStack()

                withClue("nothing to exile means nothing to ask") {
                    game.hasPendingDecision() shouldBe false
                }
                game.isInGraveyard(1, "Changeling Hero") shouldBe true
            }

            test("it is every creature type and has lifelink") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Changeling Hero")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hero = game.findPermanent("Changeling Hero")!!
                val projected = stateProjector.project(game.state)
                projected.hasKeyword(hero, Keyword.CHANGELING) shouldBe true
                projected.hasKeyword(hero, Keyword.LIFELINK) shouldBe true
                projected.getPower(hero) shouldBe 4
                projected.getToughness(hero) shouldBe 4
            }

            test("lifelink gains life when it deals combat damage") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Changeling Hero", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Changeling Hero" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.hasPendingDecision()) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                game.getLifeTotal(2) shouldBe 16
                withClue("lifelink: 4 damage dealt is 4 life gained") {
                    game.getLifeTotal(1) shouldBe 24
                }
            }
        }
    }
}
