package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.ArchaicsAgony
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Archaic's Agony (SOS) — {4}{R} Sorcery.
 *
 * "Converge — Archaic's Agony deals X damage to target creature, where X is the number of colors of
 *  mana spent to cast this spell. Exile cards from the top of your library equal to the excess
 *  damage dealt to that creature this way. You may play those cards until the end of your next turn."
 *
 * Pins: X = distinct colors of mana spent (not pips); the exile count = excess damage
 * (max(0, marked − toughness), CR 120.4a); zero excess exiles nothing.
 */
class ArchaicsAgonyScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + ArchaicsAgony)
        return driver
    }

    test("5 colors → 5 damage to a 1/1 → 4 excess → 4 cards exiled, target dies") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = driver.putCreatureOnBattlefield(opponent, "Savannah Lions") // 2/1

        val spell = driver.putCardInHand(player, "Archaic's Agony")
        // {4}{R}: R pays {R}; W/U/B/G pay the {4} generic → 5 distinct colors → X = 5.
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.RED, 1)

        val exileBefore = driver.getExile(player).size
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(victim)),
            ),
        )
        driver.bothPass()

        // 5 damage to toughness 1 → 4 excess → 4 cards exiled (plus the spell itself goes to GY).
        driver.findPermanent(opponent, "Savannah Lions") shouldBe null
        val exiledCards = driver.getExile(player).size - exileBefore
        exiledCards shouldBe 4
    }

    test("exactly lethal → no excess → nothing exiled") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = driver.putCreatureOnBattlefield(opponent, "Black Creature") // 2/2

        val spell = driver.putCardInHand(player, "Archaic's Agony")
        // 2 colors → X = 2 = toughness → exactly lethal, no excess.
        driver.giveColorlessMana(player, 3)
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.GREEN, 1)

        val exileBefore = driver.getExile(player).size
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(victim)),
            ),
        )
        driver.bothPass()

        driver.findPermanent(opponent, "Black Creature") shouldBe null
        driver.getExile(player).size shouldBe exileBefore
    }
})
