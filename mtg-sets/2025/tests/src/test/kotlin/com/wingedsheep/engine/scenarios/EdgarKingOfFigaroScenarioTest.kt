package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.FlippedCoinsThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.EdgarKingOfFigaro
import com.wingedsheep.mtg.sets.definitions.fin.cards.TheGoldSaucer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Edgar, King of Figaro (FIN #51).
 *
 * - When Edgar enters, draw a card for each artifact you control.
 * - Two-Headed Coin — the first time you flip one or more coins each turn, those coins come up
 *   heads and you win those flips (the [com.wingedsheep.sdk.scripting.WinCoinFlips] coin-flip result
 *   replacement, CR 705.3).
 *
 * The forced-win is deterministic regardless of the RNG seed, so The Gold Saucer's "{2},{T}: flip a
 * coin, if you win create a Treasure" makes a robust probe: with Edgar out, the first flip each turn
 * always wins and mints a Treasure.
 */
class EdgarKingOfFigaroScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all +
                com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens +
                listOf(EdgarKingOfFigaro, TheGoldSaucer)
        )
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        return driver
    }

    fun GameTestDriver.advanceToPlayer1(targetStep: Step) {
        passPriorityUntil(targetStep)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(targetStep)
            safety++
        }
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (guard++ < 40 && (state.stack.isNotEmpty() || isPaused)) {
            if (isPaused) autoResolveDecision() else bothPass()
        }
    }

    fun treasureCount(driver: GameTestDriver, player: EntityId): Int =
        driver.getPermanents(player).count { id ->
            driver.state.getEntity(id)
                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Treasure"
        }

    val flipAbilityId = TheGoldSaucer.activatedAbilities[1].id

    test("Two-Headed Coin forces the first coin flip of the turn to a win") {
        val driver = createDriver()
        val me = driver.player1

        driver.putCreatureOnBattlefield(me, "Edgar, King of Figaro")
        val saucer = driver.putLandOnBattlefield(me, "The Gold Saucer")
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)

        treasureCount(driver, me) shouldBe 0
        driver.state.getEntity(me)!!.has<FlippedCoinsThisTurnComponent>() shouldBe false

        // {2},{T}: flip a coin. With Edgar's Two-Headed Coin this first flip is a guaranteed win, so
        // The Gold Saucer mints a Treasure — no matter the RNG seed.
        driver.giveColorlessMana(me, 2)
        driver.submit(ActivateAbility(playerId = me, sourceId = saucer, abilityId = flipAbilityId))
            .isSuccess shouldBe true
        driver.resolveStack()

        treasureCount(driver, me) shouldBe 1
        // The flip is now recorded, so any later flip this turn is no longer the "first time" and
        // would be a genuine random flip.
        driver.state.getEntity(me)!!.has<FlippedCoinsThisTurnComponent>() shouldBe true
    }

    test("without Two-Headed Coin the flip is not forced (marker still tracks the flip)") {
        val driver = createDriver()
        val me = driver.player1

        // No Edgar on the battlefield — just The Gold Saucer.
        val saucer = driver.putLandOnBattlefield(me, "The Gold Saucer")
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)

        driver.giveColorlessMana(me, 2)
        driver.submit(ActivateAbility(playerId = me, sourceId = saucer, abilityId = flipAbilityId))
            .isSuccess shouldBe true
        driver.resolveStack()

        // The flip still happened and is tracked (so a first-flip replacement introduced later this
        // turn wouldn't retroactively fire), but the outcome was a genuine random flip — we only
        // assert the tracking, never the random result.
        driver.state.getEntity(me)!!.has<FlippedCoinsThisTurnComponent>() shouldBe true
    }

    test("Edgar's enters trigger draws a card for each artifact you control") {
        val driver = createDriver()
        val me = driver.player1

        driver.putCreatureOnBattlefield(me, "Artifact Creature")
        driver.putCreatureOnBattlefield(me, "Artifact Creature")
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)

        val edgar = driver.putCardInHand(me, "Edgar, King of Figaro")
        driver.giveColorlessMana(me, 4)
        driver.giveMana(me, Color.BLUE, 2)
        driver.castSpell(me, edgar).isSuccess shouldBe true

        // Edgar is on the stack now (out of hand); count draws from here.
        val handBeforeEtb = driver.getHandSize(me)
        driver.resolveStack()

        // Two artifacts controlled → the enters trigger drew two cards.
        driver.getHandSize(me) shouldBe handBeforeEtb + 2
    }
})
