package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfTheDark
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of the Dark (DSK #98) — "Whenever this creature attacks, if defending player controls no
 * Glimmer creatures, it gains menace and deathtouch until end of turn."
 *
 * Exercises the `Player.DefendingPlayer`-scoped intervening-if (CR 603.4): the trigger fires and the
 * grant resolves only when the defending player's battlefield holds zero Glimmer creatures.
 */
class FearOfTheDarkScenarioTest : FunSpec({

    // A 1/1 Glimmer creature — the disqualifying permanent for the intervening-if.
    val GlimmerToken = CardDefinition(
        name = "Test Glimmer",
        manaCost = ManaCost.parse("{W}"),
        typeLine = TypeLine(
            cardTypes = setOf(CardType.CREATURE),
            subtypes = setOf(Subtype("Glimmer")),
        ),
        creatureStats = CreatureStats(1, 1),
    )

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(FearOfTheDark, GlimmerToken))
        return d
    }

    test("gains menace and deathtouch on attack when defending player controls no Glimmer creatures") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightmare = driver.putCreatureOnBattlefield(p1, "Fear of the Dark")
        driver.removeSummoningSickness(nightmare)
        // p2 controls only a non-Glimmer creature — the condition is satisfied.
        driver.putCreatureOnBattlefield(p2, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(nightmare), p2).isSuccess shouldBe true
        driver.bothPass() // resolve the attack trigger

        val projected = StateProjector().project(driver.state)
        projected.hasKeyword(nightmare, Keyword.MENACE) shouldBe true
        projected.hasKeyword(nightmare, Keyword.DEATHTOUCH) shouldBe true
    }

    test("does not grant the keywords when the defending player controls a Glimmer creature") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightmare = driver.putCreatureOnBattlefield(p1, "Fear of the Dark")
        driver.removeSummoningSickness(nightmare)
        // p2 controls a Glimmer creature — the intervening-if fails, so the trigger does not resolve.
        driver.putCreatureOnBattlefield(p2, "Test Glimmer")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(nightmare), p2).isSuccess shouldBe true
        driver.bothPass()

        val projected = StateProjector().project(driver.state)
        projected.hasKeyword(nightmare, Keyword.MENACE) shouldBe false
        projected.hasKeyword(nightmare, Keyword.DEATHTOUCH) shouldBe false
    }
})
