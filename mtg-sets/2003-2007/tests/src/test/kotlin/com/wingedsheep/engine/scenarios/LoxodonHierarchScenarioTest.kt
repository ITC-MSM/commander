package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.LoxodonHierarch
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Loxodon Hierarch (RAV #214) — {2}{G}{W} 4/4 Creature — Elephant Cleric
 *
 * "When this creature enters, you gain 4 life.
 *  {G}{W}, Sacrifice this creature: Regenerate each creature you control."
 *
 * The second ability is the corpus's first group regeneration — `ForEachInGroup` over the creatures
 * you control with a `RegenerateEffect(EffectTarget.Self)` body. The tests prove the three things
 * that composition has to get right:
 *
 *  - every creature you control gets its own shield, not just one of them;
 *  - the shields are *individual* (per the RAV ruling), so spending one leaves the others up;
 *  - the Hierarch itself doesn't come back — `Costs.SacrificeSelf` is paid on activation, so it is
 *    already in the graveyard when the group snapshot is taken.
 */
class LoxodonHierarchScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LoxodonHierarch))
        return driver
    }

    fun startGame(driver: GameTestDriver) {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("entering the battlefield gains its controller 4 life") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        val before = driver.getLifeTotal(you)

        val hierarch = driver.putCardInHand(you, "Loxodon Hierarch")
        driver.giveMana(you, Color.GREEN, 1)
        driver.giveMana(you, Color.WHITE, 1)
        driver.giveColorlessMana(you, 2)
        driver.submitSuccess(
            CastSpell(playerId = you, cardId = hierarch, paymentStrategy = PaymentStrategy.FromPool)
        )
        driver.bothPass()   // resolve the Hierarch
        driver.bothPass()   // resolve the enters-the-battlefield trigger

        driver.getLifeTotal(you) shouldBe before + 4
    }

    test("the sacrifice ability regenerates every other creature you control") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        val hierarch = driver.putCreatureOnBattlefield(you, "Loxodon Hierarch")
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")

        val abilityId = driver.cardRegistry.requireCard("Loxodon Hierarch")
            .activatedAbilities.first().id

        driver.giveMana(you, Color.GREEN, 1)
        driver.giveMana(you, Color.WHITE, 1)
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = hierarch,
                abilityId = abilityId,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        driver.bothPass()

        // Sacrifice is an activation cost, so the Hierarch is gone before the effect resolves.
        driver.findPermanent(you, "Loxodon Hierarch") shouldBe null

        // The shield shows up as survival: Doom Blade destroys the Bears, regeneration saves it.
        val doomBlade = driver.putCardInHand(you, "Doom Blade")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveColorlessMana(you, 1)
        driver.castSpell(you, doomBlade, listOf(bears))
        driver.bothPass()

        driver.findPermanent(you, "Grizzly Bears") shouldBe bears
        driver.isTapped(bears) shouldBe true
    }

    test("the shields are individual — using one leaves the other creature's intact") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        val hierarch = driver.putCreatureOnBattlefield(you, "Loxodon Hierarch")
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val wall = driver.putCreatureOnBattlefield(you, "Wall of Wood")

        val abilityId = driver.cardRegistry.requireCard("Loxodon Hierarch")
            .activatedAbilities.first().id

        driver.giveMana(you, Color.GREEN, 1)
        driver.giveMana(you, Color.WHITE, 1)
        driver.submitSuccess(
            ActivateAbility(
                playerId = you,
                sourceId = hierarch,
                abilityId = abilityId,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        driver.bothPass()

        // Spend the Bears' shield.
        val firstBlade = driver.putCardInHand(you, "Doom Blade")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveColorlessMana(you, 1)
        driver.castSpell(you, firstBlade, listOf(bears))
        driver.bothPass()
        driver.findPermanent(you, "Grizzly Bears") shouldBe bears

        // The Wall's own shield is untouched by that.
        val secondBlade = driver.putCardInHand(you, "Doom Blade")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveColorlessMana(you, 1)
        driver.castSpell(you, secondBlade, listOf(wall))
        driver.bothPass()
        driver.findPermanent(you, "Wall of Wood") shouldBe wall
    }
})
