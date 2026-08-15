package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.GroupProject
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Group Project {1}{W} — Sorcery.
 * "Create a 2/2 red and white Spirit creature token.
 *  Flashback—Tap three untapped creatures you control."
 *
 * Exercises the non-mana flashback cost: a {0} mana flashback cost bundled with
 * `Costs.additional.TapPermanents(3, Creature)`. Flashback-casting from the graveyard taps three
 * untapped creatures, makes a fresh Spirit token, and exiles Group Project.
 */
class GroupProjectTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GroupProject))
        return driver
    }

    fun spiritCount(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId): Int =
        driver.getCreatures(playerId).count {
            driver.getCardName(it) == "Spirit Token"
        }

    test("normal cast makes a 2/2 Spirit token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spell = driver.putCardInHand(me, "Group Project")
        repeat(2) { driver.putLandOnBattlefield(me, "Plains") }

        driver.castSpell(me, spell)
        driver.bothPass()

        spiritCount(driver, me) shouldBe 1
    }

    test("flashback taps three creatures, makes another token, and exiles Group Project") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gp = driver.putCardInGraveyard(me, "Group Project")
        val c1 = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val c2 = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val c3 = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = gp,
                useAlternativeCost = true,
                additionalCostPayment = AdditionalCostPayment(tappedPermanents = listOf(c1, c2, c3)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // The three tapped creatures are tapped.
        driver.isTapped(c1) shouldBe true
        driver.isTapped(c2) shouldBe true
        driver.isTapped(c3) shouldBe true

        // A Spirit token was created.
        spiritCount(driver, me) shouldBe 1

        // Flashback exiled Group Project from the graveyard.
        driver.state.getZone(ZoneKey(me, Zone.EXILE)).contains(gp) shouldBe true
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(gp) shouldBe false
    }
})
