package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Brine Comber // Brinebound Gift (VOW #233) — {1}{W}{U} Creature — Spirit 1/1 // Enchantment — Aura
 *
 * Front:
 *   Whenever this creature enters or becomes the target of an Aura spell, create a 1/1 white Spirit
 *   creature token with flying.
 *   Disturb {W}{U}
 * Back (Brinebound Gift):
 *   Enchant creature
 *   Whenever this Aura enters or enchanted creature becomes the target of an Aura spell, create a
 *   1/1 white Spirit creature token with flying.
 *   If this Aura would be put into a graveyard from anywhere, exile it instead.
 *
 * The card is the first user of `BecomesTargetEvent.sourceFilter` — a filter on the *targeting*
 * spell rather than the targeted permanent — on both the SELF binding (front) and the ATTACHED
 * binding (back). The negative tests are the point of the pair: a non-Aura spell targeting the same
 * permanent must not make a token, which is what proves the filter isn't a no-op.
 */
class BrineComberScenarioTest : ScenarioTestBase() {

    init {
        context("Brine Comber — front face") {

            test("entering the battlefield creates a 1/1 white flying Spirit token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Brine Comber")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Brine Comber").error shouldBe null
                game.resolveStack()

                val tokens = game.findPermanents("Spirit Token")
                tokens.size shouldBe 1
                withClue("the token is a 1/1 with flying") {
                    game.state.projectedState.getPower(tokens.first()) shouldBe 1
                    game.state.projectedState.getToughness(tokens.first()) shouldBe 1
                    game.state.projectedState.hasKeyword(tokens.first(), Keyword.FLYING) shouldBe true
                }
            }

            test("becoming the target of an Aura spell creates a Spirit token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Brine Comber", summoningSickness = false)
                    .withCardInHand(1, "Pacifism")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val comber = game.findPermanent("Brine Comber")!!
                withClue("direct battlefield placement fires no ETB trigger, so the board starts clean") {
                    game.findPermanents("Spirit Token").size shouldBe 0
                }

                game.castSpell(1, "Pacifism", targetId = comber).error shouldBe null
                game.resolveStack()

                withClue("the Aura spell targeting it made exactly one Spirit token") {
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
            }

            test("becoming the target of a NON-Aura spell creates no token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Brine Comber", summoningSickness = false)
                    .withCardInHand(1, "Giant Growth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val comber = game.findPermanent("Brine Comber")!!
                game.castSpell(1, "Giant Growth", targetId = comber).error shouldBe null
                game.resolveStack()

                withClue("an instant is not an Aura spell — the trigger must stay silent") {
                    game.findPermanents("Spirit Token").size shouldBe 0
                }
                withClue("the pump still happened, so the spell really did target it") {
                    game.state.projectedState.getPower(comber) shouldBe 4
                }
            }
        }

        context("Brinebound Gift — back face") {

            test("an Aura spell targeting the enchanted creature creates a Spirit token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Brinebound Gift", "Grizzly Bears")
                    .withCardInHand(1, "Pacifism")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.findPermanents("Spirit Token").size shouldBe 0

                game.castSpell(1, "Pacifism", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the ATTACHED-bound trigger fired for the host being targeted") {
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
            }

            test("a NON-Aura spell targeting the enchanted creature creates no token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Brinebound Gift", "Grizzly Bears")
                    .withCardInHand(1, "Giant Growth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Giant Growth", targetId = bears).error shouldBe null
                game.resolveStack()

                game.findPermanents("Spirit Token").size shouldBe 0
            }

            test("disturb casts Brinebound Gift as an Aura whose own entry makes a Spirit token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInGraveyard(1, "Brine Comber")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val comberCard = game.state.getGraveyard(game.player1Id).first()

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = comberCard,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                        useAlternativeCost = true,
                        alternativeCostType = AlternativeCostType.DISTURB,
                    )
                ).error shouldBe null
                game.resolveStack()

                val aura = game.findPermanent("Brinebound Gift")
                withClue("disturb resolves the back face as an Aura attached to the chosen creature") {
                    aura shouldBe game.findPermanent("Brinebound Gift")
                    game.state.getEntity(aura!!)?.get<AttachedToComponent>()?.targetId shouldBe bears
                }
                withClue("the Aura's own enters trigger made one Spirit token") {
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
            }
        }
    }
}
