package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.kld.cards.AuthorityOfTheConsuls
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Authority of the Consuls (KLD) — {W} Enchantment
 *
 * "Creatures your opponents control enter tapped.
 *  Whenever a creature an opponent controls enters, you gain 1 life."
 *
 * Both clauses only fire on the real entry pipeline, so the opponent's creature must actually be
 * cast and resolved (not stamped straight onto the battlefield).
 */
class AuthorityOfTheConsulsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AuthorityOfTheConsuls)
        return driver
    }

    test("an opponent's creature enters tapped and its controller's opponent gains 1 life") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AuthorityOfTheConsuls)
        // player2 is the active player — it will cast the creature; player1 controls Authority.
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingPlayer = 1)
        val authorityController = driver.player1
        val caster = driver.player2 // the active player / "an opponent" of the Authority controller

        driver.putPermanentOnBattlefield(authorityController, "Authority of the Consuls")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.getLifeTotal(authorityController) shouldBe 20

        driver.giveMana(caster, Color.WHITE, 1)
        val lions = driver.putCardInHand(caster, "Savannah Lions")
        driver.castSpell(caster, lions)
        driver.bothPass() // resolve the creature spell → it enters tapped, gain-life trigger on stack
        driver.bothPass() // resolve the "you gain 1 life" trigger

        val entered = driver.findPermanent(caster, "Savannah Lions")
        entered shouldNotBe null
        driver.isTapped(entered!!) shouldBe true
        driver.getLifeTotal(authorityController) shouldBe 21
    }

    test("your own creature enters untapped and does not gain the opponent life") {
        val driver = createDriver()
        // player1 is active and controls Authority; it casts its own creature.
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingPlayer = 0)
        val you = driver.player1

        driver.putPermanentOnBattlefield(you, "Authority of the Consuls")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.getLifeTotal(you) shouldBe 20

        driver.giveMana(you, Color.WHITE, 1)
        val lions = driver.putCardInHand(you, "Savannah Lions")
        driver.castSpell(you, lions)
        driver.bothPass() // resolve the creature spell

        val entered = driver.findPermanent(you, "Savannah Lions")
        entered shouldNotBe null
        // A creature YOU control is unaffected: not tapped and no life gained.
        driver.isTapped(entered!!) shouldBe false
        driver.getLifeTotal(you) shouldBe 20
    }
})
