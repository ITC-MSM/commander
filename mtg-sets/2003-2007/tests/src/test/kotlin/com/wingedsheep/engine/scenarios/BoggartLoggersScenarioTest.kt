package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.BoggartLoggers
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Boggart Loggers (LRW #103) — "Forestwalk. {2}{B}, Sacrifice this creature: Destroy target
 * Treefolk or Forest."
 *
 * The printed target names two subtypes and no card type, so it is one permanent filter with a
 * subtype union. That is exactly the thing worth proving from the outside: a Forest *land* has to be
 * a legal target, not just a Treefolk creature, and an unrelated creature must not be.
 */
class BoggartLoggersScenarioTest : FunSpec({

    val destroyAbility = BoggartLoggers.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BoggartLoggers)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("a Forest land is a legal target and is destroyed") {
        val d = driver()
        val loggers = d.putCreatureOnBattlefield(d.player1, "Boggart Loggers")
        val forest = d.putLandOnBattlefield(d.player2, "Forest")
        d.giveColorlessMana(d.player1, 2)
        d.giveMana(d.player1, Color.BLACK, 1)

        d.submit(
            ActivateAbility(d.player1, loggers, destroyAbility, targets = listOf(ChosenTarget.Permanent(forest)))
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("The Forest should be destroyed by the subtype-union target") {
            d.state.getBattlefield().contains(forest) shouldBe false
        }
        withClue("Sacrificing itself is part of the cost, so the Loggers are gone too") {
            d.state.getBattlefield().contains(loggers) shouldBe false
        }
    }

    test("a Treefolk creature is a legal target and is destroyed") {
        val d = driver()
        val loggers = d.putCreatureOnBattlefield(d.player1, "Boggart Loggers")
        val oak = d.putCreatureOnBattlefield(d.player2, "Cloudcrown Oak")
        d.giveColorlessMana(d.player1, 2)
        d.giveMana(d.player1, Color.BLACK, 1)

        d.submit(
            ActivateAbility(d.player1, loggers, destroyAbility, targets = listOf(ChosenTarget.Permanent(oak)))
        ).isSuccess shouldBe true
        d.bothPass()

        d.state.getBattlefield().contains(oak) shouldBe false
    }

    test("a creature that is neither Treefolk nor Forest is not a legal target") {
        val d = driver()
        val loggers = d.putCreatureOnBattlefield(d.player1, "Boggart Loggers")
        val bear = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.giveColorlessMana(d.player1, 2)
        d.giveMana(d.player1, Color.BLACK, 1)

        d.submitExpectFailure(
            ActivateAbility(d.player1, loggers, destroyAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        )

        withClue("An illegal activation pays nothing — the Loggers are still around") {
            d.state.getBattlefield().contains(loggers) shouldBe true
            d.state.getBattlefield().contains(bear) shouldBe true
        }
    }
})
