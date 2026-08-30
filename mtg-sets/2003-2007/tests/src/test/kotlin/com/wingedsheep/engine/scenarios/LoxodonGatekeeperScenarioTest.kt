package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.LoxodonGatekeeper
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Loxodon Gatekeeper (RAV #25) — {2}{W}{W} 2/3 Creature — Elephant Soldier
 *
 * "Artifacts, creatures, and lands your opponents control enter tapped."
 *
 * Three independent `PermanentsEnterTapped` replacements, so the test walks all three halves plus
 * the two cases they must *not* touch: your own permanents, and an opponent's *enchantment* —
 * the noun list stops at three types, which is the whole reason this isn't a `Permanent` filter.
 *
 * The permanents must be genuinely *cast* (or played, for the land): placing one on the battlefield
 * directly bypasses the entry replacement the card is made of, so this uses `GameTestDriver` rather
 * than a direct-placement scenario builder.
 */
class LoxodonGatekeeperScenarioTest : FunSpec({

    val bear = CardDefinition.creature(
        name = "Test Bear",
        manaCost = ManaCost.parse("{1}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    val trinket = CardDefinition.artifact(
        name = "Test Trinket",
        manaCost = ManaCost.parse("{1}")
    )

    val charm = CardDefinition.enchantment(
        name = "Test Charm",
        manaCost = ManaCost.parse("{1}")
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LoxodonGatekeeper, bear, trinket, charm))
        return driver
    }

    /** Cast [cardName] off injected mana and let it resolve. */
    fun castAndResolve(driver: GameTestDriver, playerId: EntityId, cardName: String): EntityId {
        val cardId = driver.putCardInHand(playerId, cardName)
        driver.giveColorlessMana(playerId, 1)
        driver.submitSuccess(
            CastSpell(playerId = playerId, cardId = cardId, paymentStrategy = PaymentStrategy.FromPool)
        )
        driver.bothPass()
        return cardId
    }

    /** Advance turns until [playerId] is the active player, then stop in their precombat main. */
    fun handTurnTo(driver: GameTestDriver, playerId: EntityId) {
        while (driver.activePlayer != playerId) {
            driver.passPriorityUntil(Step.END)
            driver.bothPass()
        }
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("an opponent's creature enters tapped; your own does not") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.putCreatureOnBattlefield(you, "Loxodon Gatekeeper")

        // Yours is unaffected — the filter is `opponentControls()`.
        val yourBear = castAndResolve(driver, you, "Test Bear")
        driver.isTapped(yourBear) shouldBe false

        handTurnTo(driver, opponent)
        val theirBear = castAndResolve(driver, opponent, "Test Bear")
        driver.isTapped(theirBear) shouldBe true
    }

    test("an opponent's artifact enters tapped") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.putCreatureOnBattlefield(you, "Loxodon Gatekeeper")

        handTurnTo(driver, opponent)
        val theirTrinket = castAndResolve(driver, opponent, "Test Trinket")
        driver.isTapped(theirTrinket) shouldBe true
    }

    test("an opponent's land enters tapped, basic or not") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.putCreatureOnBattlefield(you, "Loxodon Gatekeeper")

        // Unlike Thalia's "nonbasic lands", the Gatekeeper's filter is every land — a basic
        // Plains is caught too.
        handTurnTo(driver, opponent)
        val theirPlains = driver.putCardInHand(opponent, "Plains")
        driver.submitSuccess(PlayLand(playerId = opponent, cardId = theirPlains))
        driver.isTapped(theirPlains) shouldBe true
    }

    test("an opponent's enchantment is untouched — the noun list stops at three types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.putCreatureOnBattlefield(you, "Loxodon Gatekeeper")

        handTurnTo(driver, opponent)
        val theirCharm = castAndResolve(driver, opponent, "Test Charm")
        driver.isTapped(theirCharm) shouldBe false
    }
})
