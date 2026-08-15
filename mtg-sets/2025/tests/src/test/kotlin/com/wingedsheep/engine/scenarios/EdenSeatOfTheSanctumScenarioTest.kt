package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.EdenSeatOfTheSanctum
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Eden, Seat of the Sanctum (FIN #277) — Land — Town.
 * {T}: Add {C}.
 * {5}, {T}: Mill two cards. Then you may sacrifice this land. When you do, return another
 *   target permanent card from your graveyard to your hand.
 *
 * Verifies: (1) the mana ability adds {C}; (2) the {5},{T} ability mills two and, when the
 * controller opts to sacrifice Eden, returns a chosen permanent card from the graveyard to
 * hand; (3) declining the sacrifice leaves Eden on the battlefield and returns nothing.
 */
class EdenSeatOfTheSanctumScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + EdenSeatOfTheSanctum)
        // A 30-card library so milling two has cards to move.
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("mana ability taps for {C}") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val eden = driver.putPermanentOnBattlefield(me, "Eden, Seat of the Sanctum")
        driver.untapPermanent(eden)

        val manaAbility = EdenSeatOfTheSanctum.activatedAbilities[0].id
        driver.submit(
            ActivateAbility(playerId = me, sourceId = eden, abilityId = manaAbility)
        ).isSuccess shouldBe true

        val pool = driver.state.getEntity(me)?.get<ManaPoolComponent>()!!
        pool.colorless shouldBe 1
    }

    test("{5},{T}: mills two, then sacrificing Eden returns a chosen permanent card to hand") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        // A permanent (creature) card seeded in the graveyard to be returned.
        val bear = driver.putCardInGraveyard(me, "Grizzly Bears")

        val eden = driver.putPermanentOnBattlefield(me, "Eden, Seat of the Sanctum")
        driver.untapPermanent(eden)
        driver.giveColorlessMana(me, 5)

        val graveyardBefore = driver.getGraveyard(me).size
        val handBefore = driver.getHandSize(me)

        val ability = EdenSeatOfTheSanctum.activatedAbilities[1].id
        driver.submit(
            ActivateAbility(playerId = me, sourceId = eden, abilityId = ability)
        ).isSuccess shouldBe true
        // Resolve the activated ability (mill two), pausing on the optional-sacrifice decision.
        while (!driver.isPaused && driver.stackSize > 0) driver.bothPass()

        // Two cards milled from the library into the graveyard.
        driver.getGraveyard(me).size shouldBe graveyardBefore + 2

        // "Then you may sacrifice this land." — accept.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, true)

        // The "when you do" reflexive trigger goes on the stack; pick the card to return.
        driver.submitTargetSelection(me, listOf(bear))
        while (driver.stackSize > 0) driver.bothPass() // resolve the reflexive return-to-hand

        // Eden was sacrificed; the chosen permanent card is back in hand.
        driver.getPermanents(me).contains(eden) shouldBe false
        driver.getGraveyard(me).contains(eden) shouldBe true
        driver.getHand(me).contains(bear) shouldBe true
        driver.getGraveyard(me).contains(bear) shouldBe false
        // Mill two minus the returned card; hand gains exactly the returned card.
        driver.getHandSize(me) shouldBe handBefore + 1
    }

    test("declining the sacrifice keeps Eden and returns nothing") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val bear = driver.putCardInGraveyard(me, "Grizzly Bears")

        val eden = driver.putPermanentOnBattlefield(me, "Eden, Seat of the Sanctum")
        driver.untapPermanent(eden)
        driver.giveColorlessMana(me, 5)

        val ability = EdenSeatOfTheSanctum.activatedAbilities[1].id
        driver.submit(
            ActivateAbility(playerId = me, sourceId = eden, abilityId = ability)
        ).isSuccess shouldBe true
        // Resolve the activated ability (mill two), pausing on the optional-sacrifice decision.
        while (!driver.isPaused && driver.stackSize > 0) driver.bothPass()

        // Decline the optional sacrifice.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, false)

        // Eden stays on the battlefield; the graveyard card is not returned.
        driver.getPermanents(me).contains(eden) shouldBe true
        driver.getHand(me).contains(bear) shouldBe false
        driver.getGraveyard(me).contains(bear) shouldBe true
    }
})
