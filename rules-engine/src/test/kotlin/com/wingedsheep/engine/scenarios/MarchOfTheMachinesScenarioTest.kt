package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for March of the Machines (MRD #42):
 *
 *   {3}{U} Enchantment
 *   "Each noncreature artifact is an artifact creature with power and toughness each equal to its
 *    mana value."
 *
 * The distinguishing behaviour against its Antiquities cousin Titania's Song is that March does
 * *not* strip abilities, and the printed rulings that fall out of the layer system: an artifact
 * land has mana value 0 and so dies as a 0/0 to state-based actions, and an artifact that is
 * already a creature is untouched.
 */
class MarchOfTheMachinesScenarioTest : ScenarioTestBase() {

    // A {3} noncreature artifact with an activated ability — proves the ability survives (unlike
    // Titania's Song) and that its mana value drives the animated P/T.
    private val cogwheelEngine = card("Cogwheel Engine") {
        manaCost = "{3}"
        typeLine = "Artifact"
        oracleText = "{T}: Draw a card."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.DrawCards(1)
        }
    }

    // A {2} artifact that is already a creature — March's filter must skip it.
    private val brassSoldier = card("Brass Soldier") {
        manaCost = "{2}"
        typeLine = "Artifact Creature — Soldier"
        power = 1
        toughness = 4
    }

    init {
        cardRegistry.register(cogwheelEngine)
        cardRegistry.register(brassSoldier)

        context("March of the Machines") {

            test("a noncreature artifact becomes an MV/MV creature and keeps its abilities") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Cogwheel Engine")
                    .withCardOnBattlefield(1, "March of the Machines")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val engine = game.findPermanent("Cogwheel Engine")!!
                val projected = game.state.projectedState

                withClue("a {3} noncreature artifact becomes a creature") {
                    projected.isCreature(engine) shouldBe true
                }
                withClue("its base power and toughness each equal its mana value (3)") {
                    projected.getPower(engine) shouldBe 3
                    projected.getToughness(engine) shouldBe 3
                }
                withClue("unlike Titania's Song, March does not strip abilities") {
                    projected.hasLostAllAbilities(engine) shouldBe false
                }
            }

            test("an artifact that is already a creature keeps its printed power and toughness") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Brass Soldier")
                    .withCardOnBattlefield(1, "March of the Machines")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val soldier = game.findPermanent("Brass Soldier")!!
                val projected = game.state.projectedState

                withClue("the noncreature-artifact filter skips an artifact creature") {
                    projected.getPower(soldier) shouldBe 1
                    projected.getToughness(soldier) shouldBe 4
                }
            }

            test("an artifact land is a 0/0 and dies to state-based actions") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ancient Den")
                    .withCardOnBattlefield(1, "March of the Machines")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val den = game.findPermanent("Ancient Den")!!
                val projected = game.state.projectedState

                withClue("an artifact land is animated too, at its mana value of 0") {
                    projected.isCreature(den) shouldBe true
                    projected.getPower(den) shouldBe 0
                    projected.getToughness(den) shouldBe 0
                }

                // The builder seeds permanents without ever handing out priority, so SBAs have
                // not run yet.
                game.checkStateBasedActions()

                withClue("a 0-toughness creature is put into the graveyard by CR 704.5f") {
                    game.isOnBattlefield("Ancient Den") shouldBe false
                }
            }
        }
    }
}
