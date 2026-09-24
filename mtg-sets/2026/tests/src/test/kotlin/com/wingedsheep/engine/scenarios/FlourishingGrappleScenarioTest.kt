package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Flourishing Grapple (FRA #102) — {G} Instant.
 * "Target creature or planeswalker an opponent controls that's red or white loses all abilities
 * until end of turn. Target creature you control deals damage equal to its power to that permanent."
 *
 * The strip resolves before the bite, so a white indestructible creature dies to the damage; the
 * first target is restricted to red or white permanents.
 */
class FlourishingGrappleScenarioTest : ScenarioTestBase() {

    private fun builder(mine: String) = scenario()
        .withPlayers("Player", "Opponent")
        .withCardInHand(1, "Flourishing Grapple")
        .withCardOnBattlefield(1, mine)
        .withCardOnBattlefield(2, "Seraph of the Suns")
        .withCardOnBattlefield(2, "Grizzly Bears")
        .withLandsOnBattlefield(1, "Forest", 1)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    init {
        context("Flourishing Grapple") {

            test("strips indestructible before the bite, so the white creature dies") {
                val game = builder("Craw Wurm").build()
                val seraph = game.findPermanent("Seraph of the Suns")!!
                val wurm = game.findPermanent("Craw Wurm")!!
                val grapple = game.findCardsInHand(1, "Flourishing Grapple").single()

                game.execute(
                    CastSpell(
                        game.player1Id, grapple,
                        listOf(ChosenTarget.Permanent(seraph), ChosenTarget.Permanent(wurm))
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("Seraph lost indestructible and took 6 damage") {
                    game.isOnBattlefield("Seraph of the Suns") shouldBe false
                    game.isInGraveyard(2, "Seraph of the Suns") shouldBe true
                }
                game.isOnBattlefield("Craw Wurm") shouldBe true
            }

            test("a surviving target loses its abilities until end of turn") {
                val game = builder("Grizzly Bears").build()
                val seraph = game.findPermanent("Seraph of the Suns")!!
                val myBears = game.state.getBattlefield(game.player1Id)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }
                val grapple = game.findCardsInHand(1, "Flourishing Grapple").single()

                game.execute(
                    CastSpell(
                        game.player1Id, grapple,
                        listOf(ChosenTarget.Permanent(seraph), ChosenTarget.Permanent(myBears))
                    )
                ).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Seraph of the Suns") shouldBe true
                withClue("flying and indestructible are gone this turn") {
                    game.state.projectedState.hasKeyword(seraph, Keyword.FLYING) shouldBe false
                    game.state.projectedState.hasKeyword(seraph, Keyword.INDESTRUCTIBLE) shouldBe false
                }
            }

            test("a green creature an opponent controls is not a legal first target") {
                val game = builder("Craw Wurm").build()
                val theirBears = game.state.getBattlefield(game.player2Id)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }
                val wurm = game.findPermanent("Craw Wurm")!!
                val grapple = game.findCardsInHand(1, "Flourishing Grapple").single()

                game.execute(
                    CastSpell(
                        game.player1Id, grapple,
                        listOf(ChosenTarget.Permanent(theirBears), ChosenTarget.Permanent(wurm))
                    )
                ).error.shouldNotBeNull()
            }
        }
    }
}
