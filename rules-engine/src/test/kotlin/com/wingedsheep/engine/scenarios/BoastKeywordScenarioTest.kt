package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.ActivationRestriction
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Boast (CR 702.142) as a keyword, independent of any one card.
 *
 * > **702.142a** Boast is a keyword that adds additional rules to the activated ability that follows
 * > it. "Boast — [Cost]: [Effect]" means "[Cost]: [Effect]. Activate only if this creature attacked
 * > this turn and only once each turn."
 *
 * The keyword is deliberately *not* new machinery: `isBoast = true` renders the "Boast — " prefix
 * and installs the two clauses as ordinary restrictions — [ActivationRestriction.OncePerTurn] and
 * an [ActivationRestriction.OnlyIfCondition] over `Conditions.SourceAttackedThisTurn`. These tests
 * pin that lowering *and* both halves of its enforcement, because a restriction the enumerator
 * honours but the handler doesn't (or the reverse) is the failure mode that actually happens: an
 * ability offered and then rejected, or hidden from the menu but still activatable by a
 * hand-built action.
 *
 * Note boast is once *each turn*, not exhaust's once ever — a boaster that attacks on two
 * consecutive turns boasts on both.
 */
class BoastKeywordScenarioTest : ScenarioTestBase() {

    private val boaster = card("Test Boaster") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Human Warrior"
        power = 2
        toughness = 2
        activatedAbility {
            isBoast = true
            cost = Costs.Free
            effect = Effects.GainLife(3)
        }
    }

    /** A second creature with the same boast, so "the *source* attacked" can be told from "you attacked". */
    private val bystander = card("Test Bystander") {
        manaCost = "{1}{W}"
        typeLine = "Creature — Human Soldier"
        power = 1
        toughness = 3
        activatedAbility {
            isBoast = true
            cost = Costs.Free
            effect = Effects.GainLife(3)
        }
    }

    private fun abilityIdOf(name: String) =
        cardRegistry.getCard(name)!!.script.activatedAbilities[0].id

    private fun TestGame.boast(name: String) = execute(
        ActivateAbility(player1Id, findPermanent(name)!!, abilityIdOf(name))
    )

    private fun TestGame.boastActions(name: String) =
        getLegalActions(1).filter {
            it.description.startsWith("Boast —") &&
                (it.action as? ActivateAbility)?.sourceId == findPermanent(name)
        }

    /**
     * Roll the table forward to this player's *next* declare-attackers step.
     *
     * Written as a loop rather than a fixed list of `passUntilPhase` hops because that helper
     * returns immediately when the game is already in the step being asked for: a hard-coded
     * sequence silently skips a whole turn the moment two consecutive hops name the same step, and
     * the test then quietly runs on the wrong player's combat.
     */
    private fun TestGame.advanceToMyNextDeclareAttackers() {
        val startTurn = state.turnNumber
        repeat(20) {
            passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            if (state.turnNumber > startTurn && state.activePlayerId == player1Id) return
            // Somebody else's combat (or still this turn's): step off so the next hop can move.
            passUntilPhase(Phase.ENDING, Step.END)
        }
        error("never reached this player's next declare-attackers step")
    }

    /**
     * Both creatures on the battlefield, ready to attack in the declare-attackers step.
     *
     * Both libraries are stocked: a scenario is built with *empty* libraries, so the first draw
     * step of any later turn would deck a player and end the game before the test could roll the
     * table around to this player's next combat.
     */
    private fun combatScenario() = scenario()
        .withPlayers("Boaster", "Defender")
        .withCardOnBattlefield(1, "Test Boaster")
        .withCardOnBattlefield(1, "Test Bystander")
        .also { builder -> repeat(5) { builder.withCardInLibrary(1, "Test Bystander") } }
        .also { builder -> repeat(5) { builder.withCardInLibrary(2, "Test Bystander") } }
        .withActivePlayer(1)
        .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

    init {
        cardRegistry.register(boaster)
        cardRegistry.register(bystander)

        // -----------------------------------------------------------------------------------
        // CR 702.142a — the lowering itself
        // -----------------------------------------------------------------------------------

        test("isBoast installs both rules clauses and renders the printed prefix") {
            val ability = cardRegistry.getCard("Test Boaster")!!.script.activatedAbilities[0]

            ability.isBoast shouldBe true
            withClue("'only once each turn' is a plain OncePerTurn restriction") {
                ability.restrictions.contains(ActivationRestriction.OncePerTurn) shouldBe true
            }
            withClue("'only if this creature attacked this turn' is a plain condition restriction") {
                ability.restrictions.count { it is ActivationRestriction.OnlyIfCondition } shouldBe 1
            }
            withClue("boast is once each turn, never exhaust's once ever") {
                ability.restrictions.contains(ActivationRestriction.Once) shouldBe false
            }
            ability.description.startsWith("Boast — ") shouldBe true
        }

        // -----------------------------------------------------------------------------------
        // "Activate only if this creature attacked this turn"
        // -----------------------------------------------------------------------------------

        test("a boast is neither offered nor activatable before its creature attacks") {
            val game = combatScenario().build()
            val life = game.getLifeTotal(1)

            withClue("the enumerator hides it — an unusable ability is absent, not offered") {
                game.boastActions("Test Boaster").isEmpty() shouldBe true
            }
            withClue("and the handler rejects a hand-built activation for the same reason") {
                game.boast("Test Boaster").error shouldBe "Activation condition not met"
            }
            game.getLifeTotal(1) shouldBe life
        }

        test("attacking turns the boast on for the attacker only") {
            val game = combatScenario().build()
            game.declareAttackers(mapOf("Test Boaster" to 2))

            withClue("the creature that attacked may boast") {
                game.boastActions("Test Boaster").size shouldBe 1
            }
            withClue("the one that stayed home may not, even though its controller attacked") {
                game.boastActions("Test Bystander").isEmpty() shouldBe true
                game.boast("Test Bystander").error shouldBe "Activation condition not met"
            }

            val life = game.getLifeTotal(1)
            game.boast("Test Boaster").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe life + 3
        }

        test("the boast window outlives combat — it is still available in the postcombat main phase") {
            val game = combatScenario().build()
            game.declareAttackers(mapOf("Test Boaster" to 2))
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            withClue("'attacked this turn' is a turn-long fact, not a combat-long one") {
                game.boastActions("Test Boaster").size shouldBe 1
            }
            val life = game.getLifeTotal(1)
            game.boast("Test Boaster").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe life + 3
        }

        // -----------------------------------------------------------------------------------
        // "and only once each turn"
        // -----------------------------------------------------------------------------------

        test("a boast can be activated only once each turn") {
            val game = combatScenario().build()
            game.declareAttackers(mapOf("Test Boaster" to 2))

            game.boast("Test Boaster").error shouldBe null
            game.resolveStack()
            val life = game.getLifeTotal(1)

            withClue("the second activation is gone from the menu…") {
                game.boastActions("Test Boaster").isEmpty() shouldBe true
            }
            withClue("…and refused by the handler") {
                game.boast("Test Boaster").error shouldBe
                    "This ability can only be activated once each turn"
            }
            game.getLifeTotal(1) shouldBe life
        }

        test("a spent boast comes back on a later turn the creature attacks again") {
            val game = combatScenario().build()
            game.declareAttackers(mapOf("Test Boaster" to 2))
            game.boast("Test Boaster").error shouldBe null
            game.resolveStack()

            game.advanceToMyNextDeclareAttackers()

            withClue("the table came back around to this player's own combat") {
                (game.state.activePlayerId == game.player1Id) shouldBe true
            }
            withClue("a new turn without an attack leaves the boast unavailable") {
                game.boastActions("Test Boaster").isEmpty() shouldBe true
            }
            game.declareAttackers(mapOf("Test Boaster" to 2))

            val life = game.getLifeTotal(1)
            withClue("attacking again re-opens it — boast is once each turn, not once ever") {
                game.boastActions("Test Boaster").size shouldBe 1
            }
            game.boast("Test Boaster").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe life + 3
        }
    }
}
