package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.MidnightSnack
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Midnight Snack {2}{B} — Enchantment
 *   Raid — At the beginning of your end step, if you attacked this turn, create a Food token.
 *   {2}{B}, Sacrifice this enchantment: Target opponent loses X life, where X is the amount of
 *   life you gained this turn.
 *
 * Proves the Raid intervening-"if" gates the Food creation, and that the sacrifice drain reads the
 * LIFE_GAINED turn tracker (X = life gained this turn).
 */
class MidnightSnackScenarioTest : FunSpec({

    // A cheap instant that gains 4 life — seeds the LIFE_GAINED turn tracker for the drain test.
    val GainFourLife = CardDefinition.instant(
        name = "Gain Four Life",
        manaCost = ManaCost.parse("{W}"),
        oracleText = "You gain 4 life.",
        script = CardScript.spell(effect = GainLifeEffect(4))
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MidnightSnack, GainFourLife) + PredefinedTokens.Food)
        return driver
    }

    fun foodCount(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD)).count { entityId ->
            driver.state.getEntity(entityId)?.get<CardComponent>()?.name == "Food"
        }

    test("raid active: attacked this turn → Food token at end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(you, "Midnight Snack")
        val attacker = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.removeSummoningSickness(attacker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), defendingPlayer = opponent).error shouldBe null

        driver.passPriorityUntil(Step.END)
        driver.bothPass() // resolve the end-step raid trigger

        foodCount(driver, you) shouldBe 1
    }

    test("no attack this turn: raid does nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(you, "Midnight Snack")

        driver.passPriorityUntil(Step.END)
        var safety = 0
        while (driver.stackSize > 0 && safety < 10) {
            driver.bothPass()
            safety++
        }

        foodCount(driver, you) shouldBe 0
    }

    test("drain: sacrifice makes target opponent lose X = life gained this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val snack = driver.putPermanentOnBattlefield(you, "Midnight Snack")

        // Gain 4 life this turn so the drain's X = 4.
        val gain = driver.putCardInHand(you, "Gain Four Life")
        driver.giveMana(you, Color.WHITE, 1)
        driver.castSpell(you, gain).isSuccess shouldBe true
        driver.bothPass()
        driver.assertLifeTotal(you, 24)

        // Activate: {2}{B}, Sacrifice this enchantment: target opponent loses 4.
        val abilityId = MidnightSnack.activatedAbilities.first().id
        driver.giveMana(you, Color.BLACK, 3)
        driver.submit(
            ActivateAbility(
                playerId = you,
                sourceId = snack,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        ).isSuccess shouldBe true
        driver.bothPass() // resolve the drain

        driver.findPermanent(you, "Midnight Snack") shouldBe null // sacrificed
        driver.assertLifeTotal(opponent, 16) // lost 4
        driver.assertLifeTotal(you, 24)      // unchanged
    }
})
