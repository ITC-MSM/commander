package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Weftwalking — Edge of Eternities mythic enchantment, {4}{U}{U}.
 *
 *   When this enchantment enters, if you cast it, shuffle your hand and graveyard into your
 *   library, then draw seven cards.
 *   The first spell each player casts during each of their turns may be cast without paying
 *   its mana cost.
 *
 * Two new pieces of behaviour are exercised here:
 *
 * 1. **ETB-if-cast** ([Conditions.WasCast]) — the shuffle-and-draw fires only when Weftwalking is
 *    cast, not when it enters via another path (verified by checking the cast path triggers the
 *    library reshuffle and a 7-card draw).
 *
 * 2. **Free-cast static** ([MayCastWithoutPayingManaCost] with `firstSpellOfTurnOnly = true`) —
 *    the caster's first spell during each of their own turns gets a `{0}` alternative cost; once
 *    the caster has cast a spell this turn the gate closes.
 */
class WeftwalkingScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 30, "Island" to 30),
            startingLife = 20
        )
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("casting Weftwalking shuffles hand and graveyard into the library and draws seven") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        // Seed the graveyard with three cards and the hand with two extra non-Weftwalking cards
        // so we can verify both zones get shuffled away.
        repeat(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }
        driver.putCardInHand(player, "Grizzly Bears")
        driver.putCardInHand(player, "Grizzly Bears")

        val weftwalking = driver.putCardInHand(player, "Weftwalking")
        driver.giveMana(player, Color.BLUE, 6) // {4}{U}{U}

        val handBefore = driver.state.getZone(ZoneKey(player, Zone.HAND)).size
        val graveyardBefore = driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).size
        val libraryBefore = driver.state.getZone(ZoneKey(player, Zone.LIBRARY)).size

        driver.submit(
            CastSpell(player, weftwalking, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        // Spell resolves on first pair of passes; the ETB trigger goes onto the stack and needs
        // a second pair to resolve.
        driver.bothPass()
        driver.bothPass()

        // After resolution: Weftwalking sits on the battlefield, the rest of the hand and the
        // graveyard have been shuffled into the library, and the controller has drawn seven.
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(weftwalking) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).size shouldBe 0
        driver.state.getZone(ZoneKey(player, Zone.HAND)).size shouldBe 7

        // Library net change: + (handBefore - 1 [Weftwalking itself stays out]) + graveyardBefore - 7 drawn.
        val libraryAfter = driver.state.getZone(ZoneKey(player, Zone.LIBRARY)).size
        libraryAfter shouldBe libraryBefore + (handBefore - 1) + graveyardBefore - 7
    }

    test("with Weftwalking on the battlefield, first spell may be cast for free via useWithoutPayingManaCost") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putPermanentOnBattlefield(player, "Weftwalking")

        // Pre-cast sanity: the static is detected, and the active player has cast zero spells
        // this turn — the cost calculator should expose the {0} free-cast alternative.
        (driver.state.playerSpellsCastThisTurn[player] ?: 0) shouldBe 0
        val costCalculator = CostCalculator(driver.cardRegistry)
        costCalculator.hasFreeCastPermission(driver.state, player) shouldBe true

        // Cast Grizzly Bears for free via the dedicated flag — pool is empty, paid nothing.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.submit(
            CastSpell(player, bears, useWithoutPayingManaCost = true, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(bears) shouldBe true
    }

    test("the free-cast gate closes after the first spell — useWithoutPayingManaCost fails on the second cast") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putPermanentOnBattlefield(player, "Weftwalking")

        val first = driver.putCardInHand(player, "Grizzly Bears")
        driver.submit(
            CastSpell(player, first, useWithoutPayingManaCost = true, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        // After one spell has resolved this turn, the gate is shut.
        ((driver.state.playerSpellsCastThisTurn[player] ?: 0) >= 1) shouldBe true
        val costCalculator = CostCalculator(driver.cardRegistry)
        costCalculator.hasFreeCastPermission(driver.state, player) shouldBe false

        // A second free-cast attempt fails — the gate is closed.
        val second = driver.putCardInHand(player, "Grizzly Bears")
        driver.submit(
            CastSpell(player, second, useWithoutPayingManaCost = true, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe false
    }

    test("legal-action label for the free-cast variant reads 'Cast X (Free)' with actionType 'CastWithoutPayingManaCost' and manaCostString '{0}'") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putPermanentOnBattlefield(player, "Weftwalking")
        val bears = driver.putCardInHand(player, "Grizzly Bears")

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)

        val freeCast = actions.firstOrNull { la ->
            la.actionType == "CastWithoutPayingManaCost" &&
                (la.action as? CastSpell)?.cardId == bears
        }
        freeCast shouldNotBe null
        freeCast!!.description shouldBe "Cast Grizzly Bears (Free)"
        freeCast.manaCostString shouldBe "{0}"
        (freeCast.action as CastSpell).useWithoutPayingManaCost shouldBe true
    }

    test("free-cast variant is not offered on opponent's turn (gate keys on active player)") {
        val driver = createDriver()
        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)

        driver.putPermanentOnBattlefield(controller, "Weftwalking")
        driver.putCardInHand(controller, "Grizzly Bears")

        // Active player is still `controller` here — flip to confirm the gate keys to whose
        // turn it actually is. The simplest way is to verify the cost-calculator predicate
        // returns false when queried for a non-active player.
        val costCalculator = CostCalculator(driver.cardRegistry)
        costCalculator.hasFreeCastPermission(driver.state, opponent) shouldBe false
    }

    test("Weftwalking entering via non-cast means (intervening-if 'if you cast it') does NOT shuffle/draw") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        // Seed graveyard so a shuffle would be detectable.
        repeat(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }
        val graveyardBefore = driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).size
        val handBefore = driver.state.getZone(ZoneKey(player, Zone.HAND)).size

        // Drop Weftwalking directly onto the battlefield (simulates reanimation / Show and Tell / etc.).
        driver.putPermanentOnBattlefield(player, "Weftwalking")
        driver.bothPass()
        driver.bothPass()

        // The ETB's intervening-if (Conditions.WasCast) didn't fire — hand and graveyard are untouched.
        driver.state.getZone(ZoneKey(player, Zone.HAND)).size shouldBe handBefore
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).size shouldBe graveyardBefore
    }

    test("opponent's Weftwalking grants the active player a free first-spell cast (controllerOnly = false)") {
        val driver = createDriver()
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        // The wording is "each player ... during each of their own turns" — the source's controller
        // is irrelevant when controllerOnly = false. Opponent's Weftwalking still benefits us.
        driver.putPermanentOnBattlefield(opponent, "Weftwalking")

        val costCalculator = CostCalculator(driver.cardRegistry)
        costCalculator.hasFreeCastPermission(driver.state, activePlayer) shouldBe true

        val bears = driver.putCardInHand(activePlayer, "Grizzly Bears")
        driver.submit(
            CastSpell(activePlayer, bears, useWithoutPayingManaCost = true, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getZone(ZoneKey(activePlayer, Zone.BATTLEFIELD)).contains(bears) shouldBe true
    }

    test("Jodah + Weftwalking on the battlefield: the player picks which alternative cost — both are offered as distinct LegalActions") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putPermanentOnBattlefield(player, "Weftwalking")
        driver.putPermanentOnBattlefield(player, "Jodah, Archmage Eternal")
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        // Give Jodah's full {W}{U}{B}{R}{G} so the Jodah variant is affordable; without it the
        // enumerator wouldn't emit the Jodah action at all and the test would degenerate.
        driver.giveMana(player, Color.WHITE)
        driver.giveMana(player, Color.BLUE)
        driver.giveMana(player, Color.BLACK)
        driver.giveMana(player, Color.RED)
        driver.giveMana(player, Color.GREEN)

        // Both Jodah's GrantAlternativeCastingCost ({W}{U}{B}{R}{G}) and Weftwalking's free
        // cast ({0}) must surface — the enumerator emits them as distinct legal actions so the
        // player can choose (CR 118.9a — alternative costs don't combine, but which one to use
        // is the player's call).
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
            .filter { (it.action as? CastSpell)?.cardId == bears }

        val freeCast = actions.firstOrNull { it.actionType == "CastWithoutPayingManaCost" }
        val jodahCast = actions.firstOrNull {
            it.actionType == "CastWithAlternativeCost" &&
                (it.action as CastSpell).useAlternativeCost
        }
        freeCast shouldNotBe null
        jodahCast shouldNotBe null
        freeCast!!.manaCostString shouldBe "{0}"
        jodahCast!!.manaCostString shouldBe "{W}{U}{B}{R}{G}"

        // The player picks the free cast — and it works at {0}, the WUBRG pool stays untouched.
        driver.submit(
            CastSpell(player, bears, useWithoutPayingManaCost = true, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(bears) shouldBe true
    }

    test("combining useWithoutPayingManaCost with useAlternativeCost is rejected (CR 118.9a — alt costs don't combine)") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putPermanentOnBattlefield(player, "Weftwalking")
        val bears = driver.putCardInHand(player, "Grizzly Bears")

        val result = driver.submit(
            CastSpell(
                player, bears,
                useWithoutPayingManaCost = true,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe false
    }

    test("free-casting Embrace Oblivion still requires sacrificing an artifact or creature (mandatory additional cost survives 'without paying its mana cost')") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        driver.putPermanentOnBattlefield(player, "Weftwalking")
        // Two creatures: one to sacrifice for the mandatory cost, one to destroy with the spell.
        val sacFodder = driver.putPermanentOnBattlefield(player, "Grizzly Bears")
        val victim = driver.putPermanentOnBattlefield(opponent, "Grizzly Bears")
        val embrace = driver.putCardInHand(player, "Embrace Oblivion")

        // Free-cast Embrace Oblivion — its `{B}` is waived, but the printed "As an additional
        // cost ... sacrifice an artifact or creature" must still be paid (CR 118.9 ruling cited
        // on Weftwalking itself).
        val result = driver.submit(
            CastSpell(
                player, embrace,
                useWithoutPayingManaCost = true,
                targets = listOf(ChosenTarget.Permanent(victim)),
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(sacFodder)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()

        // The sacrifice resolved (fodder is in the graveyard) and Embrace Oblivion destroyed the victim.
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(sacFodder) shouldBe true
        driver.state.getZone(ZoneKey(opponent, Zone.GRAVEYARD)).contains(victim) shouldBe true
    }

    test("free-casting Embrace Oblivion without nominating a sacrifice fails — the mandatory additional cost is enforced even at {0}") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        driver.putPermanentOnBattlefield(player, "Weftwalking")
        driver.putPermanentOnBattlefield(player, "Grizzly Bears")  // legal sacrifice exists
        val victim = driver.putPermanentOnBattlefield(opponent, "Grizzly Bears")
        val embrace = driver.putCardInHand(player, "Embrace Oblivion")

        val result = driver.submit(
            CastSpell(
                player, embrace,
                useWithoutPayingManaCost = true,
                targets = listOf(ChosenTarget.Permanent(victim)),
                // additionalCostPayment intentionally null — no sacrifice nominated
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe false
    }
})
