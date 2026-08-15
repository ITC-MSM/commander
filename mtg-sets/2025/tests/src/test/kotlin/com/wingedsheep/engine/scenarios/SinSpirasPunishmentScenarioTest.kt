package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SinSpirasPunishment
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Tests for Sin, Spira's Punishment (FIN #242).
 *
 * Sin, Spira's Punishment {4}{B}{G}{U} Legendary Creature — Leviathan Avatar 7/7, Flying.
 * Whenever Sin enters or attacks, exile a permanent card from your graveyard at random, then
 * create a tapped token that's a copy of that card. If the exiled card is a land card, repeat
 * this process.
 *
 * The random exile has no player choice, so the outcome is made deterministic by graveyard
 * composition:
 *  - an all-land graveyard makes the loop repeat until the graveyard runs out of permanent cards,
 *    proving the land-repeat loop exiles additional cards and creates additional tapped tokens;
 *  - a single nonland permanent stops the loop after one exile/copy.
 * Both the "enters" and "attacks" halves ("enters or attacks" = two sibling triggers) are covered.
 */
class SinSpirasPunishmentScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SinSpirasPunishment)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun payForSin(driver: GameTestDriver, you: EntityId) {
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveMana(you, Color.GREEN, 1)
        driver.giveMana(you, Color.BLUE, 1)
        driver.giveColorlessMana(you, 4)
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (driver.stackSize > 0 && guard++ < 20) driver.bothPass()
    }

    fun tokensControlledBy(state: GameState, player: EntityId): List<EntityId> =
        state.getBattlefield().filter {
            val e = state.getEntity(it) ?: return@filter false
            e.has<TokenComponent>() && e.get<ControllerComponent>()?.playerId == player
        }

    test("enters: an all-land graveyard repeats until empty, creating a tapped copy of each land") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        // Only permanent cards in the graveyard are two lands — the loop repeats until both are gone.
        val forest = driver.putCardInGraveyard(you, "Forest")
        val island = driver.putCardInGraveyard(you, "Island")

        val sinCard = driver.putCardInHand(you, "Sin, Spira's Punishment")
        payForSin(driver, you)

        driver.castSpell(you, sinCard)
        resolveStack(driver) // resolve Sin onto the battlefield + its enters trigger to completion

        driver.isPaused shouldBe false

        // Both lands were exiled (order-independent because every exiled card was a land).
        val exile = driver.state.getZone(ZoneKey(you, Zone.EXILE))
        exile shouldContain forest
        exile shouldContain island

        // The graveyard no longer holds either land.
        val graveyard = driver.state.getZone(ZoneKey(you, Zone.GRAVEYARD))
        (graveyard.contains(forest) || graveyard.contains(island)) shouldBe false

        // Two tapped token copies — one Forest, one Island — were created.
        val tokens = tokensControlledBy(driver.state, you)
        tokens.size shouldBe 2
        tokens.forEach { driver.state.getEntity(it)!!.has<TappedComponent>() shouldBe true }
        tokens.forEach { driver.state.getEntity(it)!!.get<CardComponent>()!!.typeLine.isLand shouldBe true }
        tokens.map { driver.state.getEntity(it)!!.get<CardComponent>()!!.name }
            .shouldContainExactlyInAnyOrder("Forest", "Island")
    }

    test("enters: a nonland permanent stops the loop after one exile and one tapped copy") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        val bears = driver.putCardInGraveyard(you, "Grizzly Bears")

        val sinCard = driver.putCardInHand(you, "Sin, Spira's Punishment")
        payForSin(driver, you)

        driver.castSpell(you, sinCard)
        resolveStack(driver)

        driver.isPaused shouldBe false

        // The lone nonland permanent was exiled; the loop did not repeat.
        driver.state.getZone(ZoneKey(you, Zone.EXILE)) shouldContain bears

        val tokens = tokensControlledBy(driver.state, you)
        tokens.size shouldBe 1
        val token = driver.state.getEntity(tokens.single())!!
        token.has<TappedComponent>() shouldBe true
        token.get<CardComponent>()!!.name shouldBe "Grizzly Bears"
        token.get<CardComponent>()!!.typeLine.isCreature shouldBe true
    }

    test("attacks: attacking triggers the same exile-and-copy loop") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        // Direct-to-battlefield bypasses the enters trigger, so only the attacks half is exercised.
        val sin = driver.putCreatureOnBattlefield(you, "Sin, Spira's Punishment")
        driver.removeSummoningSickness(sin)

        val island = driver.putCardInGraveyard(you, "Island")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(sin), opponent)
        resolveStack(driver) // resolve the attack trigger to completion

        driver.isPaused shouldBe false

        driver.state.getZone(ZoneKey(you, Zone.EXILE)) shouldContain island

        val tokens = tokensControlledBy(driver.state, you)
        tokens.size shouldBe 1
        val token = driver.state.getEntity(tokens.single())!!
        token.has<TappedComponent>() shouldBe true
        token.get<CardComponent>()!!.name shouldBe "Island"
    }
})
