package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AustereCommand
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Austere Command (LRW #3) — {4}{W}{W} Sorcery.
 *
 * Choose two —
 * • Destroy all artifacts. • Destroy all enchantments.
 * • Destroy all creatures with mana value 3 or less.
 * • Destroy all creatures with mana value 4 or greater.
 *
 * The whole card is two boundaries and a choose-two. Both boundaries are inclusive on their own
 * side and exclusive on the other — a mana value 3 creature dies to the third mode and survives the
 * fourth, a mana value 4 creature does the reverse — and getting either `manaValueAtMost` /
 * `manaValueAtLeast` off by one is silent. So each band is exercised alone, against creatures
 * sitting exactly on 3 and exactly on 4.
 */
class AustereCommandScenarioTest : FunSpec({

    // Mode order follows the printed bullets.
    val artifacts = 0
    val enchantments = 1
    val cheapCreatures = 2
    val expensiveCreatures = 3

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AustereCommand))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castCommand(caster: EntityId, modes: List<Int>) {
        giveMana(caster, Color.WHITE, 6)
        val spell = putCardInHand(caster, "Austere Command")
        submit(CastSpell(playerId = caster, cardId = spell, chosenModes = modes)).error shouldBe null
        bothPass()
    }

    test("'mana value 3 or less' takes the 3-drop and spares the 4-drop") {
        val driver = newDriver()
        val me = driver.activePlayer!!

        driver.putCreatureOnBattlefield(me, "Savannah Lions")   // mana value 1
        driver.putCreatureOnBattlefield(me, "Centaur Courser")  // mana value 3 — on the boundary
        driver.putCreatureOnBattlefield(me, "Frogmite")         // mana value 4 — on the boundary
        driver.putCreatureOnBattlefield(me, "Force of Nature")  // mana value 5

        driver.castCommand(me, listOf(cheapCreatures, enchantments))

        driver.getCreatures(me).map { driver.getCardName(it) }.toSet() shouldBe
            setOf("Frogmite", "Force of Nature")
    }

    test("'mana value 4 or greater' takes the 4-drop and spares the 3-drop") {
        val driver = newDriver()
        val me = driver.activePlayer!!

        driver.putCreatureOnBattlefield(me, "Savannah Lions")
        driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.putCreatureOnBattlefield(me, "Frogmite")
        driver.putCreatureOnBattlefield(me, "Force of Nature")

        driver.castCommand(me, listOf(expensiveCreatures, enchantments))

        driver.getCreatures(me).map { driver.getCardName(it) }.toSet() shouldBe
            setOf("Savannah Lions", "Centaur Courser")
    }

    test("both creature modes leave nothing behind — the two bands cover every mana value") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(me, "Savannah Lions")
        driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.putCreatureOnBattlefield(opponent, "Frogmite")
        driver.putCreatureOnBattlefield(opponent, "Force of Nature")

        driver.castCommand(me, listOf(cheapCreatures, expensiveCreatures))

        // "Destroy all creatures" is symmetric — the caster's board goes too.
        driver.getCreatures(me).size shouldBe 0
        driver.getCreatures(opponent).size shouldBe 0
    }

    test("artifacts and enchantments go, and a plain creature is untouched") {
        val driver = newDriver()
        val me = driver.activePlayer!!

        driver.putPermanentOnBattlefield(me, "Test Enchantment")
        driver.putCreatureOnBattlefield(me, "Artifact Creature")  // artifact *and* a creature
        driver.putCreatureOnBattlefield(me, "Savannah Lions")

        driver.castCommand(me, listOf(artifacts, enchantments))

        driver.findPermanent(me, "Test Enchantment") shouldBe null
        driver.findPermanent(me, "Artifact Creature") shouldBe null
        driver.getCreatures(me).map { driver.getCardName(it) } shouldBe listOf("Savannah Lions")
    }
})
