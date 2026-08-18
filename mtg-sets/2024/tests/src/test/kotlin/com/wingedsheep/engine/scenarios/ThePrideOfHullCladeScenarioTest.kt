package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.components.combat.CanAttackDespiteDefenderThisTurnComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * The Pride of Hull Clade (MKM #172) — {10}{G} 2/15 Legendary Creature — Crocodile Elk Turtle.
 *
 * "This spell costs {X} less to cast, where X is the total toughness of creatures you control.
 *  Defender
 *  {2}{U}{U}: Until end of turn, target creature you control gets +1/+0, gains 'Whenever this
 *  creature deals combat damage to a player, draw cards equal to its toughness,' and can attack as
 *  though it didn't have defender."
 *
 * Two things are worth pinning down. The cost reduction is the *toughness* reading of Ghalta's
 * power reduction, so it is asserted against creatures whose power and toughness differ — a
 * power-vs-toughness mix-up would pass any test built on vanilla bears. And the ruling that the
 * reduction "can't reduce the total cost below {G}" falls out of CR 601.2f (only generic mana is
 * reduced), which is checked with a board big enough to overshoot the {10}.
 *
 * The activated ability's three grants land on the *target*, not on the Pride: inside the granted
 * trigger "this creature" re-points at whatever received it, which is the whole reason the card
 * hands its ability to something with evasion rather than attacking with a defender-bearing 2/15.
 */
class ThePrideOfHullCladeScenarioTest : ScenarioTestBase() {

    init {
        val abilityId by lazy {
            cardRegistry.getCard("The Pride of Hull Clade")!!.script.activatedAbilities.single().id
        }

        context("cost reduction — total toughness, not total power") {

            test("with no creatures the spell costs its printed {10}{G}") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .build()

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("The Pride of Hull Clade"),
                    game.player1Id,
                    fromZone = Zone.HAND,
                )
                cost.genericAmount shouldBe 10
            }

            test("a 1/5 reduces the cost by 5, its toughness — not by 1, its power") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wall of Air")
                    .build()

                val wall = cardRegistry.requireCard("Wall of Air")
                withClue("the fixture creature must have differing power and toughness") {
                    (wall.creatureStats!!.power == wall.creatureStats!!.toughness) shouldBe false
                }

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("The Pride of Hull Clade"),
                    game.player1Id,
                    fromZone = Zone.HAND,
                )
                withClue("Wall of Air is 1/5, so the {10} drops to {5} — by toughness, not power") {
                    cost.genericAmount shouldBe 5
                }
            }

            test("only creatures you control count toward the reduction") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Wall of Air")
                    .build()

                val calc = CostCalculator(cardRegistry)
                val pride = cardRegistry.requireCard("The Pride of Hull Clade")

                withClue("an opponent's creature contributes nothing") {
                    calc.calculateEffectiveCost(game.state, pride, game.player1Id, fromZone = Zone.HAND)
                        .genericAmount shouldBe 10
                }
                withClue("the opponent casting it does get their own board's reduction") {
                    calc.calculateEffectiveCost(game.state, pride, game.player2Id, fromZone = Zone.HAND)
                        .genericAmount shouldBe 5
                }
            }

            test("the reduction bottoms out at {G} — only generic mana is reduced (CR 601.2f)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wall of Air")
                    .withCardOnBattlefield(1, "Wall of Swords")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .build()

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("The Pride of Hull Clade"),
                    game.player1Id,
                    fromZone = Zone.HAND,
                )
                withClue("total toughness here overshoots the {10}; generic can only reach 0") {
                    cost.genericAmount shouldBe 0
                }
            }
        }

        context("the activated ability") {

            test("grants +1/+0 and a defender bypass to the targeted creature, not to itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Pride of Hull Clade")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val pride = game.findPermanent("The Pride of Hull Clade")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = pride,
                        abilityId = abilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("activating should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("Grizzly Bears is 2/2 base, +1/+0 from the ability") {
                    projected.getPower(bears) shouldBe 3
                    projected.getToughness(bears) shouldBe 2
                }
                game.state.getEntity(bears)?.has<CanAttackDespiteDefenderThisTurnComponent>() shouldBe true
                withClue("the Pride keeps its own printed body and its own defender") {
                    projected.getPower(pride) shouldBe 2
                    projected.getToughness(pride) shouldBe 15
                    game.state.getEntity(pride)?.has<CanAttackDespiteDefenderThisTurnComponent>() shouldBe false
                }
            }
        }
    }
}
