package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Chelonian Tackle (Secrets of Strixhaven #142).
 *
 * "{2}{G} Sorcery.
 *  Target creature you control gets +0/+10 until end of turn. Then it fights up to one
 *  target creature an opponent controls."
 *
 * Exercises the "pump (+0/+10) then fight up to one" composition: the toughness boost lets the
 * creature survive a fight it would otherwise lose, and the opponent's creature is optional.
 */
class ChelonianTackleScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        // A 6/6 vanilla so the fight kills the opponent's creature and would kill the
        // unbuffed 6/4 Craw Wurm too — proving the +0/+10 toughness boost is what saves it.
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Brute",
                manaCost = ManaCost.parse("{5}{G}"),
                subtypes = setOf(Subtype("Beast")),
                power = 6,
                toughness = 6
            )
        )

        context("Chelonian Tackle") {

            test("+0/+10 lets your creature survive the fight while killing the opponent's") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Chelonian Tackle")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardOnBattlefield(1, "Craw Wurm")     // 6/4 -> +0/+10 -> 6/14
                    .withCardOnBattlefield(2, "Test Brute")    // 6/6 victim
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mine = game.findPermanent("Craw Wurm")!!
                val victim = game.findPermanent("Test Brute")!!
                val cardId = game.findCardsInHand(1, "Chelonian Tackle").first()

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        cardId,
                        listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(victim))
                    )
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Your Craw Wurm is buffed to 6/14") {
                    stateProjector.getProjectedToughness(game.state, mine) shouldBe 14
                }
                withClue("Opponent's 6/6 Test Brute takes 6 damage and dies") {
                    game.isOnBattlefield("Test Brute") shouldBe false
                }
                withClue("Your Craw Wurm (now 6/14) takes 6 damage and survives") {
                    game.isOnBattlefield("Craw Wurm") shouldBe true
                }
            }

            test("with no opponent creature targeted, the spell only pumps (fight has no second target)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Chelonian Tackle")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardOnBattlefield(1, "Craw Wurm") // 6/4
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mine = game.findPermanent("Craw Wurm")!!
                val cardId = game.findCardsInHand(1, "Chelonian Tackle").first()

                val cast = game.execute(
                    CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Permanent(mine)))
                )
                withClue("Cast with no opponent creature should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Your Craw Wurm is buffed to 6/14 and survives (no fight)") {
                    stateProjector.getProjectedToughness(game.state, mine) shouldBe 14
                    game.isOnBattlefield("Craw Wurm") shouldBe true
                }
            }
        }
    }
}
