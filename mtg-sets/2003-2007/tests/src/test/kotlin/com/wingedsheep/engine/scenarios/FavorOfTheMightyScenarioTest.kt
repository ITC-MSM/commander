package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.FavorOfTheMighty
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Favor of the Mighty (LRW #14).
 *
 * "Each creature with the greatest mana value has protection from each color."
 *
 * Three readings of that sentence are plausible and only one is right, so each test is aimed at a
 * board where the wrong ones disagree:
 *
 *  - **"Each creature" is not "each creature you control."** The first test puts the biggest
 *    creature across the table; the implicit-controller reading passes every same-side assertion
 *    and fails only here.
 *  - **A tie protects all of them** (2007-10-01). The second test gives both players an equal
 *    top-mana-value creature; a `maxOrNull`-then-pick-one reading would shield only one.
 *  - **The set is recomputed continuously** (2007-10-01: "if a new creature enters with the
 *    highest mana value, it gains protection … and the previous highest loses it"). The third
 *    test moves the maximum after the enchantment has resolved, which a set locked in at
 *    resolution time would not notice.
 */
class FavorOfTheMightyScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FavorOfTheMighty))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Cast and resolve Favor of the Mighty for [player]. */
    fun castFavor(driver: GameTestDriver, player: EntityId) {
        val card = driver.putCardInHand(player, "Favor of the Mighty")
        driver.giveMana(player, Color.WHITE, 2)
        driver.castSpell(player, card)
        driver.bothPass()
    }

    /** Protection from every colour, as five separate projected keywords. */
    fun hasProtectionFromEachColor(driver: GameTestDriver, entity: EntityId): Boolean {
        val projected = projector.project(driver.state)
        return Color.entries.all { projected.hasKeyword(entity, "PROTECTION_FROM_${it.name}") }
    }

    /** Protection from no colour at all — the "not in the affected set" assertion. */
    fun hasProtectionFromNoColor(driver: GameTestDriver, entity: EntityId): Boolean {
        val projected = projector.project(driver.state)
        return Color.entries.none { projected.hasKeyword(entity, "PROTECTION_FROM_${it.name}") }
    }

    test("the opponent's greatest-mana-value creature is protected, your smaller one is not") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        // {3}{G}{G} across the table, {W} on your side.
        val theirs = driver.putCreatureOnBattlefield(opponent, "Force of Nature")
        val yours = driver.putCreatureOnBattlefield(you, "Savannah Lions")

        castFavor(driver, you)

        hasProtectionFromEachColor(driver, theirs) shouldBe true
        hasProtectionFromNoColor(driver, yours) shouldBe true
    }

    test("a tie for the greatest mana value protects every creature sharing it") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        // Two {2}{G} creatures, one per side, and a {W} creature that misses the maximum.
        val yourCourser = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val theirCourser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        val lions = driver.putCreatureOnBattlefield(you, "Savannah Lions")

        castFavor(driver, you)

        hasProtectionFromEachColor(driver, yourCourser) shouldBe true
        hasProtectionFromEachColor(driver, theirCourser) shouldBe true
        hasProtectionFromNoColor(driver, lions) shouldBe true
    }

    test("a bigger creature entering later takes the protection off the previous maximum") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val lions = driver.putCreatureOnBattlefield(you, "Savannah Lions")
        castFavor(driver, you)

        // The only creature in play is the maximum, so it is protected.
        hasProtectionFromEachColor(driver, lions) shouldBe true

        val forceOfNature = driver.putCreatureOnBattlefield(opponent, "Force of Nature")

        hasProtectionFromEachColor(driver, forceOfNature) shouldBe true
        hasProtectionFromNoColor(driver, lions) shouldBe true
    }
})
