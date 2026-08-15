package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * The Skullspore Nexus (LCI #212) — {6}{G}{G} Legendary Artifact.
 *
 *  - "This spell costs {X} less to cast, where X is the greatest power among creatures you control."
 *  - "Whenever one or more nontoken creatures you control die, create a green Fungus Dinosaur
 *    creature token with base power and toughness each equal to the total power of those creatures."
 *  - "{2}, {T}: Double target creature's power until end of turn."
 *
 * Exercises the `GreatestPropertyAmongPermanentsYouControl(Power)` cost source, the `diedBatchTotalPower`
 * death-batch dynamic amount (summed last-known power of the *nontoken* dying creatures), and the
 * reused ModifyStats doubling (`+X/+0` at resolution).
 */
class TheSkullsporeNexusScenarioTest : ScenarioTestBase() {

    // Vanilla test creatures with known power (toughness 1 so Pyroclasm's 2 damage is lethal).
    private val bruiser = CardDefinition.creature(
        name = "Test Bruiser", manaCost = ManaCost.parse("{3}{G}"),
        subtypes = setOf(Subtype("Beast")), power = 4, toughness = 4
    )
    private val titan = CardDefinition.creature(
        name = "Test Titan", manaCost = ManaCost.parse("{6}{G}"),
        subtypes = setOf(Subtype("Giant")), power = 10, toughness = 10
    )
    private val ox = CardDefinition.creature(
        name = "Test Ox", manaCost = ManaCost.parse("{2}{G}"),
        subtypes = setOf(Subtype("Ox")), power = 3, toughness = 3
    )
    private val spikeA = CardDefinition.creature(
        name = "Test Spike A", manaCost = ManaCost.parse("{2}{G}"),
        subtypes = setOf(Subtype("Boar")), power = 3, toughness = 1
    )
    private val spikeB = CardDefinition.creature(
        name = "Test Spike B", manaCost = ManaCost.parse("{3}{G}"),
        subtypes = setOf(Subtype("Elk")), power = 4, toughness = 1
    )
    private val sprout = CardDefinition.creature(
        name = "Test Sprout", manaCost = ManaCost.parse("{G}"),
        subtypes = setOf(Subtype("Saproling")), power = 2, toughness = 1
    )

    init {
        listOf(bruiser, titan, ox, spikeA, spikeB, sprout).forEach { cardRegistry.register(it) }

        context("Cost reduction — {X} less where X is greatest power among creatures you control") {
            fun effectiveCmc(game: TestGame): Int =
                CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state, cardRegistry.getCard("The Skullspore Nexus")!!, game.player1Id
                ).cmc

            test("no creatures — full {6}{G}{G}, cmc 8") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "The Skullspore Nexus")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                withClue("X = 0 with no creatures → cost is the printed {6}{G}{G}") {
                    effectiveCmc(game) shouldBe 8
                }
            }

            test("greatest power 4 reduces generic by 4 → {2}{G}{G}, cmc 4") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "The Skullspore Nexus")
                    .withCardOnBattlefield(1, "Test Bruiser")   // power 4
                    .withCardOnBattlefield(1, "Test Ox")        // power 3 (not the greatest)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                withClue("X = greatest power (4), not the sum → 8 - 4 = 4") {
                    effectiveCmc(game) shouldBe 4
                }
            }

            test("greatest power 10 cannot reduce below the {G}{G} pips, cmc 2") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "The Skullspore Nexus")
                    .withCardOnBattlefield(1, "Test Titan")     // power 10 > 6 generic
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                withClue("Reduction floors the generic at 0; the coloured {G}{G} remain → cmc 2") {
                    effectiveCmc(game) shouldBe 2
                }
            }
        }

        context("Death trigger — token base P/T = total power of the nontoken creatures that died") {
            test("two nontoken creatures die in one batch → a single Fungus Dinosaur token of their total power") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Skullspore Nexus")
                    .withCardOnBattlefield(1, "Test Spike A")   // 3/1 nontoken
                    .withCardOnBattlefield(1, "Test Spike B")   // 4/1 nontoken
                    .withCardInHand(1, "Pyroclasm")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Pyroclasm")
                withClue("Casting Pyroclasm should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Both 1-toughness creatures die to 2 damage") {
                    game.findAllPermanents("Test Spike A").size shouldBe 0
                    game.findAllPermanents("Test Spike B").size shouldBe 0
                }
                val tokens = game.findAllPermanents("Fungus Dinosaur Token")
                withClue("A single batch fires the trigger once → exactly one token") {
                    tokens.size shouldBe 1
                }
                withClue("Base P/T each equal the total power of those creatures (3 + 4 = 7)") {
                    game.state.projectedState.getPower(tokens.first()) shouldBe 7
                    game.state.projectedState.getToughness(tokens.first()) shouldBe 7
                }
            }

            test("token creatures dying are excluded from the trigger and the power sum") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Skullspore Nexus")
                    .withCardOnBattlefield(1, "Test Spike A")               // 3/1 nontoken
                    .withCardOnBattlefield(1, "Test Sprout", isToken = true) // 2/1 TOKEN
                    .withCardInHand(1, "Pyroclasm")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pyroclasm")
                game.resolveStack()

                val tokens = game.findAllPermanents("Fungus Dinosaur Token")
                withClue("The trigger still fires (a nontoken creature died) → one token") {
                    tokens.size shouldBe 1
                }
                withClue("Only the nontoken 3-power creature counts; the 2-power token is ignored → 3/3") {
                    game.state.projectedState.getPower(tokens.first()) shouldBe 3
                    game.state.projectedState.getToughness(tokens.first()) shouldBe 3
                }
            }

            test("only token creatures dying does not trigger the ability at all") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Skullspore Nexus")
                    .withCardOnBattlefield(1, "Test Sprout", isToken = true) // 2/1 TOKEN only
                    .withCardInHand(1, "Pyroclasm")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pyroclasm")
                game.resolveStack()

                withClue("No nontoken creature you control died → no Fungus Dinosaur token created") {
                    game.findAllPermanents("Fungus Dinosaur Token").size shouldBe 0
                }
            }
        }

        context("{2}, {T}: Double target creature's power until end of turn") {
            test("doubles the target's current power (+X/+0) at resolution") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Skullspore Nexus")
                    .withCardOnBattlefield(1, "Test Ox", summoningSickness = false) // 3/3
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val nexus = game.findPermanent("The Skullspore Nexus")!!
                val ox = game.findPermanent("Test Ox")!!
                val abilityId = cardRegistry.getCard("The Skullspore Nexus")!!
                    .script.activatedAbilities[0].id

                val activate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = nexus,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, ox)),
                    )
                )
                withClue("Activating the doubling ability should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Doubling a 3-power creature yields power 6 (3 + 3)") {
                    game.state.projectedState.getPower(ox) shouldBe 6
                }
                withClue("Only power is doubled; toughness is unchanged at 3") {
                    game.state.projectedState.getToughness(ox) shouldBe 3
                }
            }
        }
    }
}
