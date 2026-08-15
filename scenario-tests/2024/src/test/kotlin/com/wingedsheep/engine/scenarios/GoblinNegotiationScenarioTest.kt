package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.GoblinNegotiation
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Goblin Negotiation (FDN) — {X}{R}{R} Sorcery.
 *
 * "Goblin Negotiation deals X damage to target creature. Create a number of 1/1 red Goblin
 *  creature tokens equal to the amount of excess damage dealt to that creature this way."
 *
 * Mirrors Hell to Pay: excess = max(0, marked − toughness), read via
 * [com.wingedsheep.sdk.scripting.values.EntityNumericProperty.ExcessMarkedDamage] in the same
 * composite resolution — one Goblin per point of overkill.
 */
class GoblinNegotiationScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + GoblinNegotiation)
        return driver
    }

    fun goblinCount(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD)).count { entityId ->
            driver.state.getEntity(entityId)?.get<CardComponent>()?.name == "Goblin Token"
        }

    test("X=5 to a 2/2 → 3 excess → 3 Goblins, and the creature dies") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = driver.putCreatureOnBattlefield(opponent, "Black Creature") // 2/2

        val spell = driver.putCardInHand(player, "Goblin Negotiation")
        driver.giveMana(player, Color.RED, 2)  // {R}{R}
        driver.giveColorlessMana(player, 5)     // X = 5
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(victim)),
                xValue = 5
            )
        )
        driver.bothPass()

        // 5 damage to toughness 2 → 3 excess → 3 Goblins.
        goblinCount(driver, player) shouldBe 3
        driver.findPermanent(opponent, "Black Creature") shouldBe null
    }

    test("X exactly lethal (X=2 to a 2/2) → no excess → no Goblins") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = driver.putCreatureOnBattlefield(opponent, "Black Creature") // 2/2

        val spell = driver.putCardInHand(player, "Goblin Negotiation")
        driver.giveMana(player, Color.RED, 2)
        driver.giveColorlessMana(player, 2)
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(victim)),
                xValue = 2
            )
        )
        driver.bothPass()

        goblinCount(driver, player) shouldBe 0
        driver.findPermanent(opponent, "Black Creature") shouldBe null
    }
})
