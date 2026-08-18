package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.battle.Battles
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The battle card type (CR 310) end to end: defense counters, the protector designation, being
 * attacked, damage removing defense, and the two state-based actions that bin a battle.
 *
 * Uses inline test battles rather than printed cards: a Siege (the only battle type that exists in
 * paper — CR 310.11) and a hypothetical typeless battle, which is the only way to exercise both the
 * CR 310.8a branch where a battle's own controller becomes its protector and the CR 704.5w
 * 0-defense action that, unlike a Siege's CR 704.5v, has no pending-trigger reprieve.
 */
class BattleCardTypeScenarioTest : ScenarioTestBase() {

    private val testSiege = card("Test Siege") {
        manaCost = "{2}{B}{B}"
        colorIdentity = "B"
        typeLine = "Battle — Siege"
        startingDefense = 5
        oracleText = "(As a Siege enters, choose an opponent to protect it. You and others can attack it.)"
    }

    /** No battle type, so CR 310.8a makes its own controller the protector. */
    private val testTypelessBattle = card("Test Bulwark") {
        manaCost = "{3}{G}"
        colorIdentity = "G"
        typeLine = "Battle"
        startingDefense = 3
        oracleText = "A battle with no battle types."
    }

    /**
     * A battle — of each type — carrying a triggered ability of its own, so the CR 704.5v
     * defeat-trigger reprieve and CR 704.5w's lack of one can be told apart. An upkeep trigger is
     * used because `passUntilPhase` stops with begin-of-step triggers queued but unresolved, which
     * is exactly the "is the source of an ability that has triggered but not yet left the stack"
     * state both rules turn on.
     */
    private val testTriggeringTypelessBattle = card("Test Beacon") {
        manaCost = "{2}{U}"
        colorIdentity = "U"
        typeLine = "Battle"
        startingDefense = 3
        oracleText = "At the beginning of your upkeep, draw a card."

        triggeredAbility {
            trigger = Triggers.YourUpkeep
            effect = Effects.DrawCards(1)
            description = "At the beginning of your upkeep, draw a card."
        }
    }

    private val testTriggeringSiege = card("Test Watchtower") {
        manaCost = "{2}{U}"
        colorIdentity = "U"
        typeLine = "Battle — Siege"
        startingDefense = 3
        oracleText = "At the beginning of your upkeep, draw a card."

        triggeredAbility {
            trigger = Triggers.YourUpkeep
            effect = Effects.DrawCards(1)
            description = "At the beginning of your upkeep, draw a card."
        }
    }

    private fun defenseOf(game: TestGame, name: String): Int =
        game.findPermanent(name)
            ?.let { game.state.getEntity(it)?.get<CountersComponent>()?.getCount(CounterType.DEFENSE) }
            ?: 0

    private fun protectorOf(game: TestGame, name: String): EntityId? =
        game.findPermanent(name)?.let { Battles.protectorOf(game.state, it) }

    init {
        cardRegistry.register(testSiege)
        cardRegistry.register(testTypelessBattle)
        cardRegistry.register(testTriggeringTypelessBattle)
        cardRegistry.register(testTriggeringSiege)

        context("CR 310.4 — defense is defense counters") {

            test("a cast battle enters with its printed defense as defense counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Test Siege")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Test Siege").error shouldBe null
                game.resolveStack()

                withClue("CR 310.4b — enters with printed defense (5) worth of defense counters") {
                    defenseOf(game, "Test Siege") shouldBe 5
                }
            }

            test("a battle reanimated straight onto the battlefield still enters with its defense") {
                // The intrinsic entry ability is a replacement effect (CR 614.1c), so it applies to
                // every way the battle enters — not just a resolving spell.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Test Siege")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val siegeId = game.findCardsInGraveyard(1, "Test Siege").single()
                val result = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
                    game.state, siegeId, com.wingedsheep.sdk.core.Zone.BATTLEFIELD
                )
                game.state = result.state

                withClue("CR 310.4b applies to a non-cast entry too") {
                    defenseOf(game, "Test Siege") shouldBe 5
                }
            }
        }

        context("CR 310.8 / 704.5w / 704.5x — the protector") {

            test("a Siege is protected by its controller's opponent, never by its controller") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions().error shouldBe null

                withClue("CR 310.11a — only an opponent of a Siege's controller may protect it") {
                    protectorOf(game, "Test Siege") shouldBe game.player2Id
                }
                withClue("the Siege is still controlled by the player who cast it (CR 310.8d asymmetry)") {
                    game.state.projectedState.getController(game.findPermanent("Test Siege")!!) shouldBe game.player1Id
                }
            }

            test("a battle with no battle types is protected by its own controller") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Bulwark")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions().error shouldBe null

                withClue("CR 310.8a — with no battle types, the controller becomes the protector") {
                    protectorOf(game, "Test Bulwark") shouldBe game.player1Id
                }
            }

            test("the protector is assigned without prompting when only one player is eligible") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()

                withClue("a forced choice in a two-player game raises no decision") {
                    game.hasPendingDecision() shouldBe false
                }
                protectorOf(game, "Test Siege") shouldNotBe null
            }

            test("the protector designation is dropped when the battle leaves the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                val siegeId = game.findPermanent("Test Siege")!!
                protectorOf(game, "Test Siege") shouldNotBe null

                val result = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
                    game.state, siegeId, com.wingedsheep.sdk.core.Zone.GRAVEYARD
                )
                game.state = result.state

                withClue("CR 400.7 — the object that left has no protector designation") {
                    Battles.protectorOf(game.state, siegeId) shouldBe null
                }
            }
        }

        context("CR 310.8b — who can attack a battle") {

            test("a Siege's controller can attack the Siege they control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackersWithPermanentTargets(
                    permanentAttackers = mapOf("Grizzly Bears" to "Test Siege")
                )

                withClue("CR 310.8b — the opponent protects it, so its controller may attack it") {
                    result.error shouldBe null
                }
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.getEntity(bears)?.get<AttackingComponent>()?.defenderId shouldBe
                    game.findPermanent("Test Siege")
            }

            test("a battle's protector can never attack it") {
                // P2 protects P1's Siege, so P2's creatures may not attack it (CR 310.8b).
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                protectorOf(game, "Test Siege") shouldBe game.player2Id
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackersWithPermanentTargets(
                    permanentAttackers = mapOf("Grizzly Bears" to "Test Siege")
                )

                withClue("the protector's own creatures can't attack the battle they protect") {
                    result.error shouldNotBe null
                }
            }

            test("an attackable battle is offered in the legal attack targets") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val declare = game.getLegalActions(1).single { it.actionType == "DeclareAttackers" }
                withClue("the server, not the client, decides a battle is attackable") {
                    declare.validAttackTargets.orEmpty() shouldNotBe emptyList<EntityId>()
                    (game.findPermanent("Test Siege") in declare.validAttackTargets.orEmpty()) shouldBe true
                }
            }
        }

        context("CR 120.3h / 704.5v — damage and defeat") {

            test("combat damage to a battle removes that many defense counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackersWithPermanentTargets(
                    permanentAttackers = mapOf("Grizzly Bears" to "Test Siege")
                ).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("a 2/2 removes 2 of the Siege's 5 defense counters") {
                    defenseOf(game, "Test Siege") shouldBe 3
                }
                withClue("CR 120.5 — the damage itself doesn't destroy the battle") {
                    game.isOnBattlefield("Test Siege") shouldBe true
                }
            }

            test("a battle whose defense reaches 0 is put into its owner's graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Bulwark")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                val bulwark = game.findPermanent("Test Bulwark")!!
                game.state = game.state.updateEntity(bulwark) { container ->
                    container.with(CountersComponent().withCounters(CounterType.DEFENSE, 0))
                }

                game.checkStateBasedActions().error shouldBe null

                withClue("CR 704.5v — a battle at 0 defense is put into its owner's graveyard") {
                    game.isOnBattlefield("Test Bulwark") shouldBe false
                    game.isInGraveyard(1, "Test Bulwark") shouldBe true
                }
            }

            /**
             * The August 7, 2026 split of the 0-defense state-based action along the Siege line.
             * The two tests below are the same board with one word of type line changed, because
             * the *only* thing that decides the outcome is whether the battle is a Siege:
             *
             *  - CR 704.5v (Siege) keeps a battle alive while it is the source of an ability that
             *    has triggered but not yet left the stack — the clause that exists so a Siege's own
             *    defeat trigger has something left to exile.
             *  - CR 704.5w (non-Siege) has no such clause, so the battle is binned on the spot and
             *    its pending trigger resolves with the battle already in the graveyard.
             *
             * Until that update the reprieve was written for every battle, which no test could
             * distinguish while Siege was the only printed battle type.
             */
            fun battleWithPendingUpkeepTrigger(battleName: String): TestGame {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, battleName)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()
                game.checkStateBasedActions()
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                val battleId = game.findPermanent(battleName)!!
                withClue("the battle's own upkeep trigger is on the stack and hasn't resolved") {
                    game.state.stack.any { stackId ->
                        game.state.getEntity(stackId)
                            ?.get<TriggeredAbilityOnStackComponent>()
                            ?.sourceId == battleId
                    } shouldBe true
                }

                game.state = game.state.updateEntity(battleId) { container ->
                    container.with(CountersComponent().withCounters(CounterType.DEFENSE, 0))
                }
                game.checkStateBasedActions().error shouldBe null
                return game
            }

            test("a Siege at 0 defense survives while its own trigger is still on the stack") {
                val game = battleWithPendingUpkeepTrigger("Test Watchtower")

                withClue("CR 704.5v — the pending-trigger reprieve is Siege-only, and this is one") {
                    game.isOnBattlefield("Test Watchtower") shouldBe true
                }
            }

            test("a non-Siege battle at 0 defense is binned despite its own pending trigger") {
                val game = battleWithPendingUpkeepTrigger("Test Beacon")

                withClue("CR 704.5w — a non-Siege battle gets no reprieve for a pending trigger") {
                    game.isOnBattlefield("Test Beacon") shouldBe false
                    game.isInGraveyard(1, "Test Beacon") shouldBe true
                }
            }

            test("noncombat damage removes defense counters too, and excess damage is capped at the defense") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Bulwark")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                val bulwark = game.findPermanent("Test Bulwark")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val result = com.wingedsheep.engine.handlers.effects.DamageUtils.dealDamageToTarget(
                    game.state, bulwark, 10, sourceId = bears
                )
                game.state = result.state

                val damageEvent = result.events
                    .filterIsInstance<com.wingedsheep.engine.core.DamageDealtEvent>()
                    .single { event -> event.targetId == bulwark }
                withClue("CR 120.4a — excess is the amount above the battle's defense (10 - 3)") {
                    damageEvent.excessAmount shouldBe 7
                }
                withClue("defense counters can't go below zero") {
                    game.state.getEntity(bulwark)?.get<CountersComponent>()
                        ?.getCount(CounterType.DEFENSE) ?: 0 shouldBe 0
                }
            }
        }

        context("CR 310.8c/d — the protector defends the battle") {

            test("the protector, not the controller, is the defending player for an attacked battle") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackersWithPermanentTargets(
                    permanentAttackers = mapOf("Grizzly Bears" to "Test Siege")
                ).error shouldBe null

                val siege = game.findPermanent("Test Siege")!!
                withClue("CR 310.8d — the defending player is the Siege's protector, not its controller") {
                    com.wingedsheep.engine.mechanics.combat.CombatDefenders
                        .defendingPlayerOf(game.state, siege) shouldBe game.player2Id
                }
                withClue("so the protector is the one who gets to declare blockers") {
                    com.wingedsheep.engine.mechanics.combat.CombatDefenders
                        .isDefendingPlayer(game.state, game.player2Id) shouldBe true
                }
            }

            test("the protector may block a creature attacking the battle they protect") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackersWithPermanentTargets(
                    permanentAttackers = mapOf("Grizzly Bears" to "Test Siege")
                ).error shouldBe null
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val result = game.declareBlockers(mapOf("Hill Giant" to listOf("Grizzly Bears")))

                withClue("CR 310.8c — the Siege's protector may block its attackers") {
                    result.error shouldBe null
                }
            }
        }
    }
}
