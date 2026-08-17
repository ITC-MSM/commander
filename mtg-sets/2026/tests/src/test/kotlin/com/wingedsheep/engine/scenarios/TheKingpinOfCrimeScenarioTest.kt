package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for The Kingpin of Crime (Marvel Super Heroes #220).
 *
 * {1}{W}{B} · Legendary Creature — Human Villain · 1/5
 *   Extort
 *   Whenever you attack, you may pay 2 life. If you do, until end of turn, creatures you control
 *   with toughness greater than their power assign combat damage equal to their toughness rather
 *   than their power.
 *
 * The attack ability's affected set is **dynamic**, not a snapshot: the effect changes no
 * characteristic and no controller, so under CR 611.2c it modifies the rules of the game and "can
 * affect objects that weren't affected when that continuous effect began". The card grants itself
 * `AssignDamageEqualToToughness` — Bedrock Tortoise's printed sentence — until end of turn, and
 * `CombatDamageUtils` reads that at the point of use against final projected power and toughness.
 * The "pumped after the trigger resolved" test below is what distinguishes that from the
 * snapshotting `ForEachInGroup` + `GrantKeyword` shape it replaced.
 */
class TheKingpinOfCrimeScenarioTest : ScenarioTestBase() {

    /** A 1/3 — toughness-heavy from the start, so it matches the filter at resolution. */
    private val ogre = card("Test Ogre") {
        manaCost = "{2}{B}"
        typeLine = "Creature — Ogre"
        power = 1
        toughness = 3
    }

    /** A 2/2 — does *not* match the filter until something raises its toughness. */
    private val bear = card("Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    /** A free +0/+2, cast after the trigger has resolved to move a creature into the group. */
    private val fortify = card("Test Fortify") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "Target creature gets +0/+2 until end of turn."
        spell {
            val t = target("target creature", TargetCreature())
            effect = Effects.ModifyStats(0, 2, t)
        }
    }

    /**
     * Cast the pump at [targetId], declining the extort trigger the cast sets off — the Kingpin is
     * on the battlefield, and extort is "whenever you cast a spell, you may pay {W/B}".
     */
    private fun castFortify(game: TestGame, targetId: com.wingedsheep.sdk.model.EntityId) {
        game.castSpell(1, "Test Fortify", targetId = targetId).error shouldBe null
        repeat(4) {
            if (game.getPendingDecision() is YesNoDecision) game.answerYesNo(false)
            game.resolveStack()
        }
    }

    private fun attackWith(vararg attackers: String): TestGame {
        val builder = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "The Kingpin of Crime", summoningSickness = false)
            .withCardInHand(1, "Test Fortify")
            .withActivePlayer(1)
            .withPriorityPlayer(1)
            .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
        attackers.forEach { builder.withCardOnBattlefield(1, it, summoningSickness = false) }
        val game = builder.build()

        game.declareAttackers(attackers.associateWith { 2 }).error shouldBe null
        game.resolveStack()
        return game
    }

    init {
        cardRegistry.register(ogre)
        cardRegistry.register(bear)
        cardRegistry.register(fortify)

        context("whenever you attack — the may-pay gate") {

            test("paying 2 life makes a toughness-heavy attacker assign damage equal to toughness") {
                val game = attackWith("Test Ogre")

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.resolveStack()

                withClue("the 2 life is paid") { game.getLifeTotal(1) shouldBe 18 }

                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("the 1/3 assigns 3, not 1") { game.getLifeTotal(2) shouldBe 17 }
            }

            test("declining pays nothing and the attacker assigns its power") {
                val game = attackWith("Test Ogre")

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("no life paid") { game.getLifeTotal(1) shouldBe 20 }

                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("the 1/3 assigns its printed power") { game.getLifeTotal(2) shouldBe 19 }
            }
        }

        context("CR 611.2c — the affected set stays dynamic") {

            test("a creature that becomes toughness-heavy after the trigger resolved is covered") {
                val game = attackWith("Test Bear")

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.resolveStack()
                withClue("the 2/2 does not match the filter when the trigger resolves") {
                    game.getLifeTotal(1) shouldBe 18
                }

                // Only now does the Bear become a 2/4 — a snapshot taken at resolution would
                // have frozen it out, but a rules-modifying effect keeps re-evaluating.
                val bearId = game.findPermanent("Test Bear")!!
                castFortify(game, bearId)
                withClue("the pump resolved") {
                    game.state.projectedState.getPower(bearId) shouldBe 2
                    game.state.projectedState.getToughness(bearId) shouldBe 4
                }

                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("the now-2/4 assigns 4, not 2") { game.getLifeTotal(2) shouldBe 16 }
            }

            test("without the payment, pumping toughness changes nothing") {
                val game = attackWith("Test Bear")

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                val bearId = game.findPermanent("Test Bear")!!
                castFortify(game, bearId)

                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("no effect was created, so the 2/4 assigns its power") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }
        }
    }
}
