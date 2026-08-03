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
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riotFor
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.CantReceiveCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
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
 * Riot's decision offers the counter branch at index 0 and haste at index 1.
 */
class SpiderPunkScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    /**
     * A second, non-legendary riot granter, so a Spider can pick up **two** instances of riot at
     * once (CR 702.136b) without running into the legend rule. Stands in for Rhythm of the Wild
     * ("Nontoken creatures you control have riot"), which isn't implemented yet.
     */
    private val riotDrum = card("Riot Drum") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "Creatures you control have riot."
        riotFor(GameObjectFilter.Creature.youControl())
    }

    /** Stands in for Solemnity: nothing on the battlefield can have counters put on it. */
    private val counterBan = card("Counter Ban") {
        manaCost = "{2}"
        typeLine = "Enchantment"
        oracleText = "Creatures can't have counters put on them."
        staticAbility { ability = CantReceiveCounters(GroupFilter.AllCreatures) }
    }

    init {
        cardRegistry.register(listOf(riotDrum, counterBan))

        val counterMode = 0
        val hasteMode = 1

        /** Cast [cardName] from P1's hand and answer riot's mode choice with [mode]. */
        fun castAndRiot(game: TestGame, cardName: String, mode: Int): EntityId {
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
                withClue("one instance, one choice — the 'other Spiders' grant must not also fire " +
                    "for Spider-Punk itself") {
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

        context("Spider-Punk — riot edge cases") {

            test("two instances of riot make two separate choices (CR 702.136b)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spider-Punk")
                    .withCardOnBattlefield(1, "Riot Drum")
                    .withCardInHand(1, "Giant Spider")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Giant Spider").error shouldBe null
                game.resolveStack()

                val first = game.getPendingDecision()
                withClue("first riot instance asks") { (first != null) shouldBe true }
                game.submitDecision(OptionChosenResponse(first!!.id, counterMode))

                val second = game.getPendingDecision()
                withClue("the second instance asks separately") { (second != null) shouldBe true }
                withClue("a distinct decision, not a resubmission of the first") {
                    (second!!.id == first.id) shouldBe false
                }
                game.submitDecision(OptionChosenResponse(second!!.id, hasteMode))
                game.resolveStack()

                val spider = game.findPermanent("Giant Spider")!!
                val projected = stateProjector.project(game.state)
                withClue("one instance took the counter: 2/4 becomes 3/5") {
                    projected.getPower(spider) shouldBe 3
                    projected.getToughness(spider) shouldBe 5
                }
                withClue("the other instance took haste — both branches apply") {
                    projected.hasKeyword(spider, Keyword.HASTE) shouldBe true
                }
                game.hasPendingDecision() shouldBe false
            }

            test("a creature that can't have counters put on it is given haste with no choice") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spider-Punk")
                    .withCardOnBattlefield(1, "Counter Ban")
                    .withCardInHand(1, "Giant Spider")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Giant Spider").error shouldBe null
                game.resolveStack()

                withClue("the counter branch isn't a legal choice, so riot doesn't ask") {
                    game.hasPendingDecision() shouldBe false
                }
                val spider = game.findPermanent("Giant Spider")!!
                val projected = stateProjector.project(game.state)
                withClue("no counter was placed") {
                    projected.getPower(spider) shouldBe 2
                    projected.getToughness(spider) shouldBe 4
                }
                withClue("it gains haste instead") {
                    projected.hasKeyword(spider, Keyword.HASTE) shouldBe true
                }
            }

            test("a Spider reanimated without being cast still gets riot's choice") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spider-Punk")
                    .withCardInGraveyard(1, "Giant Spider")
                    .withCardInHand(1, "Zombify")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingGraveyardCard(
                    1, "Zombify", game.findCardsInGraveyard(1, "Giant Spider")
                ).error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("a permanent put onto the battlefield still gets its as-enters riot") {
                    (decision != null) shouldBe true
                }
                game.submitDecision(OptionChosenResponse(decision!!.id, counterMode))
                game.resolveStack()

                val spider = game.findPermanent("Giant Spider")!!
                val projected = stateProjector.project(game.state)
                withClue("2/4 plus a +1/+1 counter") {
                    projected.getPower(spider) shouldBe 3
                    projected.getToughness(spider) shouldBe 5
                }
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

                withClue("the counter-proof creature still resolved onto the battlefield") {
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
