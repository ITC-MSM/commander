package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Jace, Reality Sculptor (FRA #216) — {3}{U}{U} Legendary Planeswalker — Jace, loyalty 5.
 *
 *   +1: Empower Jace X, where X is the number of Islands you control.
 *   −3: Until your next turn, whenever a creature attacks you or a planeswalker you control, it
 *       gets -5/-0 until end of turn.
 *   0: Exile all but the bottom card of each opponent's library. Activate only if there are
 *      twenty-five or more loyalty counters among Jaces you control.
 */
class JaceRealitySculptorScenarioTest : ScenarioTestBase() {
    init {
        val abilities = cardRegistry.getCard("Jace, Reality Sculptor")!!.script.activatedAbilities
        fun loyalty(change: Int) = abilities.single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

        fun TestGame.loyaltyOf(id: EntityId): Int =
            state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

        fun TestGame.setLoyalty(id: EntityId, amount: Int) {
            state = state.updateEntity(id) { it.with(CountersComponent(mapOf(CounterType.LOYALTY to amount))) }
        }

        fun base() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Jace, Reality Sculptor")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Grizzly Bears")
            .withCardInLibrary(2, "Hill Giant")
            .withCardInLibrary(2, "Swamp")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("+1 empowers Jace by the number of Islands you control") {
            val game = base()
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Forest", 2)
                .build()
            val jace = game.findPermanent("Jace, Reality Sculptor")!!

            game.execute(ActivateAbility(game.player1Id, jace, loyalty(1))).error shouldBe null
            game.resolveStack()

            game.loyaltyOf(jace) shouldBe 6
            val token = game.findPermanents("Jace").single()
            withClue("the Jace token gets one loyalty counter per Island, not per land") {
                game.loyaltyOf(token) shouldBe 3
            }
        }

        test("−3: creatures attacking you or your planeswalkers get -5/-0 until your next turn") {
            val game = base()
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(1, "Llanowar Elves")
                .build()
            val jace = game.findPermanent("Jace, Reality Sculptor")!!
            val giant = game.findPermanent("Hill Giant")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(ActivateAbility(game.player1Id, jace, loyalty(-3))).error shouldBe null
            game.resolveStack()
            game.loyaltyOf(jace) shouldBe 2

            // Opponent's turn: the Giant attacks you, the Bears attack Jace.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.state.activePlayerId shouldBe game.player2Id
            game.declareAttackersWithPermanentTargets(
                playerAttackers = mapOf("Hill Giant" to 1),
                permanentAttackers = mapOf("Grizzly Bears" to "Jace, Reality Sculptor"),
            ).error shouldBe null
            game.resolveStack()

            withClue("each attacker gets -5/-0; power floors in combat damage, not in the stat") {
                game.state.projectedState.getPower(giant) shouldBe -2
                game.state.projectedState.getToughness(giant) shouldBe 3
                game.state.projectedState.getPower(bears) shouldBe -3
                game.state.projectedState.getToughness(bears) shouldBe 2
            }

            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
            withClue("neither attacker dealt damage") {
                game.getLifeTotal(1) shouldBe 20
                game.loyaltyOf(jace) shouldBe 2
            }
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.activePlayerId shouldBe game.player1Id
            withClue("the -5/-0 wore off at end of turn") {
                game.state.projectedState.getPower(giant) shouldBe 3
            }
        }

        test("−3 doesn't shrink your own attackers, and expires at your next turn") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .build()
            val jace = game.findPermanent("Jace, Reality Sculptor")!!
            val giant = game.findPermanent("Hill Giant")!!

            game.execute(ActivateAbility(game.player1Id, jace, loyalty(-3))).error shouldBe null
            game.resolveStack()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            val bears = game.findPermanent("Grizzly Bears")!!
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            game.resolveStack()
            withClue("a creature attacking your opponent doesn't trigger it") {
                game.state.projectedState.getPower(bears) shouldBe 2
            }

            // Through the opponent's turn without attacking, your next turn, then the opponent's next combat.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.activePlayerId shouldBe game.player2Id
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.activePlayerId shouldBe game.player1Id
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.state.activePlayerId shouldBe game.player2Id
            game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null
            game.resolveStack()
            withClue("until your next turn — the watcher is gone on the following opponent turn") {
                game.state.projectedState.getPower(giant) shouldBe 3
            }
        }

        test("0 is unavailable below twenty-five loyalty among your Jaces") {
            val game = base().build()
            val jace = game.findPermanent("Jace, Reality Sculptor")!!
            game.setLoyalty(jace, 24)

            game.execute(ActivateAbility(game.player1Id, jace, loyalty(0))).error shouldNotBe null
        }

        test("0 counts loyalty among every Jace you control and exiles all but the bottom card") {
            val game = base()
                .withLandsOnBattlefield(1, "Island", 5)
                .build()
            val jace = game.findPermanent("Jace, Reality Sculptor")!!
            // +1 first: Jace goes to 6 and creates a Jace token with 5 (five Islands).
            game.execute(ActivateAbility(game.player1Id, jace, loyalty(1))).error shouldBe null
            game.resolveStack()
            val token = game.findPermanents("Jace").single()
            game.loyaltyOf(token) shouldBe 5

            // Next turn cycle back to you with Jace at 20 — 20 + 5 = 25.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.activePlayerId shouldBe game.player1Id
            game.setLoyalty(jace, 20)

            val oppLibraryBefore = game.librarySize(2)
            oppLibraryBefore shouldBe 3 // four cards, one drawn
            val bottom = game.state.getLibrary(game.player2Id).last()

            game.execute(ActivateAbility(game.player1Id, jace, loyalty(0))).error shouldBe null
            game.resolveStack()

            game.librarySize(2) shouldBe 1
            game.state.getLibrary(game.player2Id).single() shouldBe bottom
            withClue("your own library is untouched") {
                game.librarySize(1) shouldBe 1
            }
        }
    }
}
