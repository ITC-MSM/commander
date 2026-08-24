package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.m14.cards.AccursedSpirit
import com.wingedsheep.mtg.sets.definitions.m15.cards.KrenkosEnforcer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Engine coverage for intimidate (CR 702.13) — "can't be blocked except by artifact creatures
 * and/or creatures that share a color with it".
 *
 * `Keyword.INTIMIDATE` existed in the SDK before this change but `rules-engine` never read it, and
 * **no card in the corpus declared it** — the display-only-keyword failure mode (cascade, modular,
 * annihilator): the reminder text renders and the engine ignores the restriction. The M15 sweep is
 * the first batch to print the keyword, so
 * [com.wingedsheep.engine.mechanics.combat.rules.IntimidateRule] was added alongside it. These
 * tests prove both halves of 702.13b — the colour-sharing exception and the artifact exception.
 */
class IntimidateScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(KrenkosEnforcer)
        driver.registerCard(AccursedSpirit)
        return driver
    }

    test("a red intimidator can't be blocked by a green creature, but a red one may block") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = driver.putCreatureOnBattlefield(me, "Krenko's Enforcer")
        driver.removeSummoningSickness(attacker)
        val greenBlocker = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        val redBlocker = driver.putCreatureOnBattlefield(opponent, "Goblin Guide")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(attacker), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("Centaur Courser is green and not an artifact, so it shares no color with the red Enforcer") {
            driver.declareBlockers(opponent, mapOf(greenBlocker to listOf(attacker))).isSuccess shouldBe false
        }
        withClue("Goblin Guide is red, so it shares a color and the block is legal") {
            driver.declareBlockers(opponent, mapOf(redBlocker to listOf(attacker))).isSuccess shouldBe true
        }
    }

    test("an artifact creature may block an intimidator whose color it does not share") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = driver.putCreatureOnBattlefield(me, "Krenko's Enforcer")
        driver.removeSummoningSickness(attacker)
        val artifactBlocker = driver.putCreatureOnBattlefield(opponent, "Artifact Creature")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(attacker), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("the artifact half of CR 702.13b: a colorless artifact creature blocks regardless of color") {
            driver.declareBlockers(opponent, mapOf(artifactBlocker to listOf(attacker))).isSuccess shouldBe true
        }
    }

    test("intimidate reads the attacker's own color — a black intimidator is blocked by black") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = driver.putCreatureOnBattlefield(me, "Accursed Spirit")
        driver.removeSummoningSickness(attacker)
        val redBlocker = driver.putCreatureOnBattlefield(opponent, "Goblin Guide")
        val blackBlocker = driver.putCreatureOnBattlefield(opponent, "Black Creature")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(attacker), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("red shares no color with the black Accursed Spirit") {
            driver.declareBlockers(opponent, mapOf(redBlocker to listOf(attacker))).isSuccess shouldBe false
        }
        withClue("black shares a color with the black Accursed Spirit") {
            driver.declareBlockers(opponent, mapOf(blackBlocker to listOf(attacker))).isSuccess shouldBe true
        }
    }

    test("a creature without intimidate is blocked normally") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = driver.putCreatureOnBattlefield(me, "Goblin Guide")
        driver.removeSummoningSickness(attacker)
        val greenBlocker = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(attacker), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        withClue("the rule must not restrict blocks against attackers that lack the keyword") {
            driver.declareBlockers(opponent, mapOf(greenBlocker to listOf(attacker))).isSuccess shouldBe true
        }
    }
})
