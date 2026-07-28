package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.spm.cards.GwenomRemorseless
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Gwenom, Remorseless (SPM) — "Whenever Gwenom attacks, until end of turn, you may play cards from
 * the top of your library. If you cast a spell this way, pay life equal to its mana value rather
 * than pay its mana cost."
 *
 * Pins the new `PlayFromTopWithAlternativeCost` permission granted durationally: after Gwenom
 * attacks, the top card of the library becomes castable for life (mana waived), and it isn't
 * before the attack.
 */
class GwenomRemorselessScenarioTest : FunSpec({

    // A vanilla creature ({4}{G}, mana value 5) to sit on top of the library.
    val topBeast = CardDefinition(
        name = "Top Beast",
        manaCost = ManaCost.parse("{4}{G}"),
        typeLine = TypeLine.parse("Creature — Beast"),
        oracleText = "",
        creatureStats = CreatureStats(3, 3),
    )

    fun newGame(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GwenomRemorseless, topBeast))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != you }
        return Triple(driver, you, opponent)
    }

    fun resolveStack(driver: GameTestDriver) {
        var g = 0
        while (g++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun topOfLibraryCastCardIds(driver: GameTestDriver, playerId: EntityId) =
        LegalActionEnumerator.create(driver.cardRegistry).enumerate(driver.state, playerId)
            .filter { it.sourceZone == "LIBRARY" }
            .mapNotNull { it.action as? CastSpell }
            .map { it.cardId }

    test("after Gwenom attacks, the top spell is castable for life; not before") {
        val (driver, you, opponent) = newGame()
        val gwenom = driver.putCreatureOnBattlefield(you, "Gwenom, Remorseless")
        driver.removeSummoningSickness(gwenom)
        val beast = driver.putCardOnTopOfLibrary(you, "Top Beast")

        // Before attacking: no permission, so the top card isn't castable.
        (beast in topOfLibraryCastCardIds(driver, you)) shouldBe false

        // Gwenom attacks → the attack trigger grants the play-from-top permission until end of turn.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(gwenom), opponent)
        resolveStack(driver)
        driver.declareNoBlockers(opponent)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        resolveStack(driver)

        // Now the top card is castable from the library.
        (beast in topOfLibraryCastCardIds(driver, you)) shouldBe true

        // Cast it: no mana is given, and it costs 5 life (its mana value).
        val lifeBefore = driver.getLifeTotal(you)
        val result = driver.submit(
            CastSpell(playerId = you, cardId = beast, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.error shouldBe null
        resolveStack(driver)

        driver.getLifeTotal(you) shouldBe lifeBefore - 5           // paid life = mana value
        (driver.findPermanent(you, "Top Beast") != null) shouldBe true  // resolved onto the battlefield
    }
})
