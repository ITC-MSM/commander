package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.battle.Battles
import com.wingedsheep.engine.mechanics.combat.CombatDamageManager
import com.wingedsheep.engine.mechanics.combat.DamageCalculator
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The battle card type (CR 310) end to end: defense counters, the protector designation, being
 * attacked, damage removing defense, and the two state-based actions that bin a battle.
 *
 * Uses two inline test battles rather than a printed card: a Siege (the only battle type that
 * exists in paper — CR 310.11) and a hypothetical typeless battle, which is the only way to
 * exercise the CR 310.8a branch where a battle's own controller becomes its protector.
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

    private val testTitan = card("Test Titan") {
        manaCost = "{6}"
        colorIdentity = ""
        typeLine = "Creature — Giant"
        power = 10
        toughness = 10
    }

    private val testStrikerA = card("Test Striker A") {
        manaCost = "{3}"
        typeLine = "Creature — Warrior"
        power = 3
        toughness = 3
    }

    private val testStrikerB = card("Test Striker B") {
        manaCost = "{3}"
        typeLine = "Creature — Warrior"
        power = 3
        toughness = 3
    }

    private val testPlaneswalker = card("Test Planeswalker") {
        manaCost = "{2}{U}"
        colorIdentity = "U"
        typeLine = "Planeswalker — Tester"
        startingLoyalty = 3
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
        cardRegistry.register(testTitan)
        cardRegistry.register(testStrikerA)
        cardRegistry.register(testStrikerB)
        cardRegistry.register(testPlaneswalker)

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

            test("combat damage reports excess above a battle's defense") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Test Titan", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackersWithPermanentTargets(
                    permanentAttackers = mapOf("Test Titan" to "Test Siege")
                ).error shouldBe null

                val siege = game.findPermanent("Test Siege")!!
                val combatDamage = CombatDamageManager(cardRegistry, DamageCalculator(cardRegistry))
                    .applyCombatDamage(game.state)
                combatDamage.error shouldBe null

                val damageEvent = combatDamage.events
                    .filterIsInstance<com.wingedsheep.engine.core.DamageDealtEvent>()
                    .single { it.targetId == siege }
                withClue("CR 120.4a — combat damage above a battle's defense is excess") {
                    damageEvent.excessAmount shouldBe 5
                }
            }

            test("simultaneous combat damage reports aggregate battle excess without source-order attribution") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Siege")
                    .withCardOnBattlefield(1, "Test Striker A", summoningSickness = false)
                    .withCardOnBattlefield(1, "Test Striker B", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.checkStateBasedActions()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackersWithPermanentTargets(
                    permanentAttackers = linkedMapOf(
                        "Test Striker A" to "Test Siege",
                        "Test Striker B" to "Test Siege"
                    )
                ).error shouldBe null

                val siege = game.findPermanent("Test Siege")!!
                val combatDamage = CombatDamageManager(cardRegistry, DamageCalculator(cardRegistry))
                    .applyCombatDamage(game.state)
                combatDamage.error shouldBe null

                val sourceEvents = combatDamage.events
                    .filterIsInstance<com.wingedsheep.engine.core.DamageDealtEvent>()
                    .filter { it.targetId == siege }
                withClue("neither simultaneous source is arbitrarily marked as the excess source") {
                    sourceEvents.size shouldBe 2
                    sourceEvents.map { it.excessAmount }.toSet() shouldBe setOf(0)
                }
                val aggregate = combatDamage.events
                    .filterIsInstance<com.wingedsheep.engine.core.CombatDamageBatchExcessEvent>()
                    .single { it.targetId == siege }
                withClue("CR 120.4a/120.10 — 3 + 3 combat damage over 5 defense has one aggregate excess") {
                    aggregate.preDamageCounterCount shouldBe 5
                    aggregate.totalDamage shouldBe 6
                    aggregate.excessAmount shouldBe 1
                    aggregate.sourceIds.toSet() shouldBe sourceEvents.map { it.sourceId }.toSet()
                }
            }

            test("simultaneous combat damage reports aggregate planeswalker excess") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(2, "Test Planeswalker")
                    .withCardOnBattlefield(1, "Test Striker A", summoningSickness = false)
                    .withCardOnBattlefield(1, "Test Striker B", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackersWithPermanentTargets(
                    permanentAttackers = linkedMapOf(
                        "Test Striker A" to "Test Planeswalker",
                        "Test Striker B" to "Test Planeswalker"
                    )
                ).error shouldBe null

                val planeswalker = game.findPermanent("Test Planeswalker")!!
                val combatDamage = CombatDamageManager(cardRegistry, DamageCalculator(cardRegistry))
                    .applyCombatDamage(game.state)
                combatDamage.error shouldBe null

                combatDamage.events
                    .filterIsInstance<com.wingedsheep.engine.core.DamageDealtEvent>()
                    .filter { it.targetId == planeswalker }
                    .map { it.excessAmount }
                    .toSet() shouldBe setOf(0)
                val aggregate = combatDamage.events
                    .filterIsInstance<com.wingedsheep.engine.core.CombatDamageBatchExcessEvent>()
                    .single { it.targetId == planeswalker }
                withClue("CR 120.4a/120.10 — 3 + 3 over three loyalty is three aggregate excess") {
                    aggregate.counterType shouldBe CounterType.LOYALTY.name
                    aggregate.preDamageCounterCount shouldBe 3
                    aggregate.totalDamage shouldBe 6
                    aggregate.excessAmount shouldBe 3
                }
            }

            test("noncombat damage reports excess above a planeswalker's loyalty") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Planeswalker")
                    .withCardOnBattlefield(1, "Test Titan", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val planeswalker = game.findPermanent("Test Planeswalker")!!
                val titan = game.findPermanent("Test Titan")!!
                val result = com.wingedsheep.engine.handlers.effects.DamageUtils.dealDamageToTarget(
                    game.state, planeswalker, 10, sourceId = titan
                )

                val damageEvent = result.events
                    .filterIsInstance<com.wingedsheep.engine.core.DamageDealtEvent>()
                    .single { it.targetId == planeswalker }
                withClue("CR 120.4a — excess is the amount above the planeswalker's loyalty (10 - 3)") {
                    damageEvent.excessAmount shouldBe 7
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
