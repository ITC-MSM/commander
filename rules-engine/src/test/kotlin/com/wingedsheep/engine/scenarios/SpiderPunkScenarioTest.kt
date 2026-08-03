package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lea.cards.ProdigalSorcerer
import com.wingedsheep.mtg.sets.definitions.ons.cards.BattlefieldMedic
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Spider-Punk (SPM #92).
 *
 * {1}{R} Legendary Creature — Spider Human Hero, 2/1
 * "Riot (This creature enters with your choice of a +1/+1 counter or haste.)
 *  Other Spiders you control have riot.
 *  Spells and abilities can't be countered.
 *  Damage can't be prevented."
 *
 * Riot's mode options are declared in option order by the `riot()` DSL helper:
 * index 0 = "A +1/+1 counter", index 1 = "Haste".
 */
class SpiderPunkScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        val counterMode = 0
        val hasteMode = 1

        /** Cast [cardName] from P1's hand and answer riot's mode choice with [mode]. */
        fun castAndRiot(game: TestGame, cardName: String, mode: Int): com.wingedsheep.sdk.model.EntityId {
            game.castSpell(1, cardName).error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            withClue("$cardName entering prompts riot's choice") { (decision != null) shouldBe true }
            game.submitDecision(OptionChosenResponse(decision!!.id, mode))
            game.resolveStack()

            return game.findPermanent(cardName)!!
        }

        context("Spider-Punk — riot on itself") {

            test("choosing the +1/+1 counter: a 3/2 without haste") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spider-Punk")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val punk = castAndRiot(game, "Spider-Punk", counterMode)
                val projected = stateProjector.project(game.state)

                withClue("2/1 plus a +1/+1 counter") {
                    projected.getPower(punk) shouldBe 3
                    projected.getToughness(punk) shouldBe 2
                }
                withClue("the counter branch grants no haste") {
                    projected.hasKeyword(punk, Keyword.HASTE) shouldBe false
                }
                withClue("riot resolves in exactly one choice — the 'other Spiders' grant must " +
                    "not also fire for Spider-Punk itself") {
                    game.hasPendingDecision() shouldBe false
                }
            }

            test("choosing haste: a 2/1 with haste") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spider-Punk")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val punk = castAndRiot(game, "Spider-Punk", hasteMode)
                val projected = stateProjector.project(game.state)

                withClue("printed 2/1, no counter") {
                    projected.getPower(punk) shouldBe 2
                    projected.getToughness(punk) shouldBe 1
                }
                withClue("has haste") { projected.hasKeyword(punk, Keyword.HASTE) shouldBe true }
            }
        }

        context("Spider-Punk — other Spiders you control have riot") {

            fun spiderGame() = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Spider-Punk")
                .withCardInHand(1, "Giant Spider")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            test("another Spider entering gets riot's choice — counter branch") {
                val game = spiderGame()
                val spider = castAndRiot(game, "Giant Spider", counterMode)
                val projected = stateProjector.project(game.state)

                withClue("2/4 plus a +1/+1 counter") {
                    projected.getPower(spider) shouldBe 3
                    projected.getToughness(spider) shouldBe 5
                }
                projected.hasKeyword(spider, Keyword.HASTE) shouldBe false
            }

            test("another Spider entering gets riot's choice — haste branch") {
                val game = spiderGame()
                val spider = castAndRiot(game, "Giant Spider", hasteMode)
                val projected = stateProjector.project(game.state)

                withClue("printed 2/4, no counter") {
                    projected.getPower(spider) shouldBe 2
                    projected.getToughness(spider) shouldBe 4
                }
                withClue("has haste") { projected.hasKeyword(spider, Keyword.HASTE) shouldBe true }
            }

            test("a non-Spider entering under Spider-Punk gets no riot choice") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spider-Punk")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("riot only applies to Spiders") { game.hasPendingDecision() shouldBe false }
                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = stateProjector.project(game.state)
                projected.getPower(bears) shouldBe 2
                projected.getToughness(bears) shouldBe 2
                projected.hasKeyword(bears, Keyword.HASTE) shouldBe false
            }

            test("no riot choice once Spider-Punk has left the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Giant Spider")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Giant Spider").error shouldBe null
                game.resolveStack()

                withClue("no granter on the battlefield, no choice") {
                    game.hasPendingDecision() shouldBe false
                }
                val spider = game.findPermanent("Giant Spider")!!
                val projected = stateProjector.project(game.state)
                projected.getPower(spider) shouldBe 2
                projected.getToughness(spider) shouldBe 4
            }
        }

        context("Spider-Punk — spells and abilities can't be countered") {

            test("a Counterspell aimed at a spell resolves but counters nothing") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spider-Punk")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInHand(2, "Counterspell")
                    .withLandsOnBattlefield(2, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.passPriority()
                game.castSpellTargetingStackSpell(2, "Counterspell", "Grizzly Bears")
                    .error shouldBe null
                game.resolveStack()

                withClue("the countered-proof creature still resolved onto the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("Grizzly Bears never hit the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            test("a Stifle aimed at an activated ability resolves but counters nothing") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spider-Punk")
                    .withCardOnBattlefield(1, "Prodigal Sorcerer")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Stifle")
                    .withLandsOnBattlefield(2, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sorcerer = game.findPermanent("Prodigal Sorcerer")!!
                val opponentId = game.state.turnOrder[1]

                game.execute(
                    ActivateAbility(
                        playerId = game.state.turnOrder[0],
                        sourceId = sorcerer,
                        abilityId = ProdigalSorcerer.activatedAbilities.first().id,
                        targets = listOf(ChosenTarget.Player(opponentId)),
                    )
                ).error shouldBe null

                val abilityOnStack = game.state.stack.last()
                game.passPriority()
                game.execute(
                    CastSpell(
                        playerId = opponentId,
                        cardId = game.findCardsInHand(2, "Stifle").first(),
                        targets = listOf(ChosenTarget.Spell(abilityOnStack)),
                    )
                ).error shouldBe null

                game.resolveStack()

                withClue("the ability wasn't countered — the ping still landed") {
                    game.getLifeTotal(2) shouldBe 19
                }
            }
        }

        context("Spider-Punk — damage can't be prevented") {

            /**
             * Battlefield Medic shields the Prodigal Sorcerer for 1 (one Cleric on the
             * battlefield), then the Sorcerer pings itself. Without Spider-Punk the shield eats
             * the damage and the 1/1 survives; with Spider-Punk the shield is ignored and the
             * Sorcerer dies to state-based actions.
             */
            fun pingSelfThroughShield(withSpiderPunk: Boolean): TestGame {
                val builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Battlefield Medic")
                    .withCardOnBattlefield(1, "Prodigal Sorcerer")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                if (withSpiderPunk) builder.withCardOnBattlefield(1, "Spider-Punk")
                val game = builder.build()

                val playerId = game.state.turnOrder[0]
                val medic = game.findPermanent("Battlefield Medic")!!
                val sorcerer = game.findPermanent("Prodigal Sorcerer")!!

                game.execute(
                    ActivateAbility(
                        playerId = playerId,
                        sourceId = medic,
                        abilityId = BattlefieldMedic.activatedAbilities.first().id,
                        targets = listOf(ChosenTarget.Permanent(sorcerer)),
                    )
                ).error shouldBe null
                game.resolveStack()

                game.execute(
                    ActivateAbility(
                        playerId = playerId,
                        sourceId = sorcerer,
                        abilityId = ProdigalSorcerer.activatedAbilities.first().id,
                        targets = listOf(ChosenTarget.Permanent(sorcerer)),
                    )
                ).error shouldBe null
                game.resolveStack()

                return game
            }

            test("without Spider-Punk the prevention shield saves the 1/1") {
                val game = pingSelfThroughShield(withSpiderPunk = false)
                game.isOnBattlefield("Prodigal Sorcerer") shouldBe true
            }

            test("with Spider-Punk the shield is ignored and the 1/1 dies") {
                val game = pingSelfThroughShield(withSpiderPunk = true)
                withClue("damage can't be prevented, so the ping is lethal") {
                    game.isOnBattlefield("Prodigal Sorcerer") shouldBe false
                }
            }
        }
    }
}
