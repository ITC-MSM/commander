package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.LieInWait
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Lie in Wait — {B}{G}{U} Sorcery.
 *   "Return target creature card from your graveyard to your hand. Lie in Wait deals damage
 *    equal to that card's power to target creature."
 *
 * Verifies the two-target spell: the graveyard creature card is returned to hand, and the
 * damage dealt equals that card's (printed) power.
 */
class LieInWaitScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(LieInWait)
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("returns the graveyard creature and deals damage equal to its power") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != player }

        val spell = driver.putCardInHand(player, "Lie in Wait")
        // Force of Nature is a 5/5 — power 5 — sitting in our graveyard.
        val graveyardCreature = driver.putCardInGraveyard(player, "Force of Nature")
        // Target a 3/3 Centaur Courser; 5 damage destroys it.
        val damageTarget = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)

        driver.submit(
            CastSpell(
                player, spell,
                targets = listOf(
                    ChosenTarget.Card(graveyardCreature, player, Zone.GRAVEYARD),
                    ChosenTarget.Permanent(damageTarget)
                ),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // The Force of Nature was returned to hand.
        driver.state.getZone(ZoneKey(player, Zone.HAND)).contains(graveyardCreature) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(graveyardCreature) shouldBe false
        driver.state.getEntity(graveyardCreature)?.get<CardComponent>()?.name shouldBe "Force of Nature"

        // 5 damage (Force of Nature's power) to a 3/3 → destroyed.
        driver.state.getBattlefield().contains(damageTarget) shouldBe false
        driver.state.getZone(ZoneKey(opponent, Zone.GRAVEYARD)).contains(damageTarget) shouldBe true
    }

    test("lower-power returned card deals only that much damage; tougher target survives") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != player }

        val spell = driver.putCardInHand(player, "Lie in Wait")
        // Grizzly Bears is a 2/2 — power 2.
        val graveyardCreature = driver.putCardInGraveyard(player, "Grizzly Bears")
        // Target a 3/3 Centaur Courser; 2 damage leaves it alive with 2 marked.
        val damageTarget = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)

        driver.submit(
            CastSpell(
                player, spell,
                targets = listOf(
                    ChosenTarget.Card(graveyardCreature, player, Zone.GRAVEYARD),
                    ChosenTarget.Permanent(damageTarget)
                ),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getZone(ZoneKey(player, Zone.HAND)).contains(graveyardCreature) shouldBe true
        driver.state.getBattlefield().contains(damageTarget) shouldBe true
        (driver.state.getEntity(damageTarget)?.get<DamageComponent>()?.amount ?: 0) shouldBe 2
    }
})
