package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.RallyTheMonastery
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Rally the Monastery.
 *
 * Rally the Monastery: {3}{W} Instant
 * "This spell costs {2} less to cast if you've cast another spell this turn.
 *  Choose one —
 *   • Create two 1/1 white Monk creature tokens with prowess.
 *   • Up to two target creatures you control each get +2/+2 until end of turn.
 *   • Destroy target creature with power 4 or greater."
 *
 * Rules exercised:
 *  - Cast-time cost reduction gated by an intervening "if" (you've cast another spell this turn).
 *  - Modal "choose one" with three modes (token creation / up-to-two buff / conditional removal).
 */
class RallyTheMonasteryTest : FunSpec({

    val BigCreature = CardDefinition.creature(
        name = "Big Creature",
        manaCost = ManaCost.parse("{3}{G}"),
        subtypes = emptySet(),
        power = 4,
        toughness = 4,
        oracleText = ""
    )

    val SmallCreature = CardDefinition.creature(
        name = "Small Creature",
        manaCost = ManaCost.parse("{1}"),
        subtypes = emptySet(),
        power = 1,
        toughness = 1,
        oracleText = ""
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RallyTheMonastery, BigCreature, SmallCreature))
        return driver
    }

    test("mode 0 — creates two 1/1 white Monk tokens with prowess") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 3)
        val rally = driver.putCardInHand(player1, "Rally the Monastery")

        val before = driver.getCreatures(player1).size
        val result = driver.submit(CastSpell(player1, rally, chosenModes = listOf(0)))
        result.isSuccess shouldBe true
        driver.bothPass()

        val monks = driver.getCreatures(player1).filter { driver.getCardName(it) == "Monk Token" }
        monks.size shouldBe 2
        driver.getCreatures(player1).size shouldBe before + 2
    }

    test("mode 2 — destroys target creature with power 4 or greater") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Forest" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player2, "Big Creature")
        val target = driver.getCreatures(player2).first()

        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 3)
        val rally = driver.putCardInHand(player1, "Rally the Monastery")

        val result = driver.submit(CastSpell(
            player1, rally,
            targets = listOf(ChosenTarget.Permanent(target)),
            chosenModes = listOf(2),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(target)))
        ))
        result.isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(player2, "Big Creature") shouldBe null
    }

    test("costs {2} less when you've cast another spell this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Lightning Bolt" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // First spell of the turn.
        val bolt = driver.putCardInHand(player1, "Lightning Bolt")
        driver.giveMana(player1, Color.RED, 1)
        driver.castSpell(player1, bolt, listOf(player2))
        driver.bothPass()

        // Now Rally should cost {1}{W} instead of {3}{W}: give exactly that.
        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 1)
        val rally = driver.putCardInHand(player1, "Rally the Monastery")
        val result = driver.submit(CastSpell(player1, rally, chosenModes = listOf(0)))
        result.isSuccess shouldBe true
    }
})
