package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.GolgariGermination
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Golgari Germination (RAV #209) — {1}{B}{G} Enchantment
 *
 * "Whenever a nontoken creature you control dies, create a 1/1 green Saproling creature token."
 *
 * Two predicates carry the whole card and each gets a test: `youControl()` (an opponent's dying
 * creature does nothing) and `nontoken()` (the Saprolings it makes don't feed it — without that
 * predicate the enchantment would replace each dying token with a fresh one forever).
 */
class GolgariGerminationScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GolgariGermination))
        return driver
    }

    fun startGame(driver: GameTestDriver) {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /** Doom Blade [victim] off injected mana and let the kill (and any trigger) resolve. */
    fun doomBlade(driver: GameTestDriver, caster: EntityId, victim: EntityId) {
        val blade = driver.putCardInHand(caster, "Doom Blade")
        driver.giveMana(caster, Color.BLACK, 1)
        driver.giveColorlessMana(caster, 1)
        driver.castSpell(caster, blade, listOf(victim))
        driver.bothPass()   // resolve Doom Blade
        driver.bothPass()   // resolve the Germination trigger, if one went on the stack
    }

    /** Minted tokens are named "<name> Token", so match on the prefix. */
    fun saprolings(driver: GameTestDriver, playerId: EntityId): List<EntityId> =
        driver.getCreatures(playerId).filter {
            driver.getCardName(it)?.startsWith("Saproling") == true
        }

    test("a nontoken creature you control dying makes a Saproling") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        driver.putPermanentOnBattlefield(you, "Golgari Germination")
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")

        saprolings(driver, you).size shouldBe 0

        doomBlade(driver, you, bears)

        driver.findPermanent(you, "Grizzly Bears") shouldBe null
        saprolings(driver, you).size shouldBe 1
    }

    test("an opponent's creature dying does nothing — the filter is `you control`") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.putPermanentOnBattlefield(you, "Golgari Germination")
        val theirBears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        doomBlade(driver, you, theirBears)

        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
        saprolings(driver, you).size shouldBe 0
        saprolings(driver, opponent).size shouldBe 0
    }

    test("the Saproling it makes doesn't feed it — `nontoken` closes the loop") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        driver.putPermanentOnBattlefield(you, "Golgari Germination")
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")

        doomBlade(driver, you, bears)
        saprolings(driver, you).size shouldBe 1

        val saproling = saprolings(driver, you).single()
        doomBlade(driver, you, saproling)

        // The token died and was not replaced.
        saprolings(driver, you).size shouldBe 0
    }
})
