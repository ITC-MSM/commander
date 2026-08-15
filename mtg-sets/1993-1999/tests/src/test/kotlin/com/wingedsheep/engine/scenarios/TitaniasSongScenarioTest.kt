package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Titania's Song (ATQ #35):
 *
 *   {3}{G} Enchantment
 *   "Each noncreature artifact loses all abilities and becomes an artifact creature with power and
 *    toughness each equal to its mana value. If this enchantment leaves the battlefield, this
 *    effect continues until end of turn."
 *
 * Exercises the continuous group static (animate noncreature artifacts to MV/MV, strip abilities)
 * and the "this effect continues until end of turn" linger when the enchantment leaves.
 */
class TitaniasSongScenarioTest : ScenarioTestBase() {

    // A {3} noncreature artifact with an activated ability, so we can prove the ability is
    // stripped (Layer 6) and its mana value (3) drives the animated P/T (Layer 7b).
    private val tinkerEngine = card("Tinker Engine") {
        manaCost = "{3}"
        typeLine = "Artifact"
        oracleText = "{T}: Draw a card."
        activatedAbility {
            cost = com.wingedsheep.sdk.dsl.Costs.Tap
            effect = Effects.DrawCards(1)
        }
    }

    // {0} sorcery that destroys a target enchantment, to send Titania's Song to the graveyard.
    private val shatterSong = card("Shatter Song") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Destroy target enchantment."
        spell {
            val t = target("target enchantment", Targets.Enchantment)
            effect = Effects.Destroy(t)
        }
    }

    init {
        cardRegistry.register(tinkerEngine)
        cardRegistry.register(shatterSong)

        context("Titania's Song") {

            test("noncreature artifact becomes an MV/MV creature and loses its abilities") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tinker Engine")
                    .withCardOnBattlefield(1, "Titania's Song")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val engine = game.findPermanent("Tinker Engine")!!
                val projected = game.state.projectedState

                withClue("a {3} noncreature artifact becomes a creature") {
                    projected.isCreature(engine) shouldBe true
                }
                withClue("its base power and toughness each equal its mana value (3)") {
                    projected.getPower(engine) shouldBe 3
                    projected.getToughness(engine) shouldBe 3
                }
                withClue("it loses all abilities (the tap-to-draw ability is suppressed)") {
                    projected.hasLostAllAbilities(engine) shouldBe true
                }
            }

            test("the effect continues until end of turn when Titania's Song leaves, then reverts") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tinker Engine")
                    .withCardOnBattlefield(1, "Titania's Song")
                    .withCardInHand(1, "Shatter Song")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val song = game.findPermanent("Titania's Song")!!
                val engine = game.findPermanent("Tinker Engine")!!

                game.castSpell(1, "Shatter Song", song).error shouldBe null
                game.resolveStack()
                // The leaves-the-battlefield trigger is queued by the destroy; drain it too.
                game.resolveStack()

                withClue("Titania's Song has left the battlefield") {
                    game.isOnBattlefield("Titania's Song") shouldBe false
                }

                // Same turn: the linger keeps the artifact animated as an MV/MV creature.
                val duringTurn = game.state.projectedState
                withClue("the artifact stays a 3/3 creature this turn (effect continues until EOT)") {
                    duringTurn.isCreature(engine) shouldBe true
                    duringTurn.getPower(engine) shouldBe 3
                    duringTurn.getToughness(engine) shouldBe 3
                    duringTurn.hasLostAllAbilities(engine) shouldBe true
                }

                // Advance past this turn's cleanup (where EndOfTurn floating effects expire) into
                // the opponent's turn.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                val nextTurn = game.state.projectedState
                withClue("after end of turn the artifact reverts to a noncreature with its abilities") {
                    nextTurn.isCreature(engine) shouldBe false
                    nextTurn.hasLostAllAbilities(engine) shouldBe false
                }
            }
        }
    }
}
