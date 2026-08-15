package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.DiaryOfDreams
import com.wingedsheep.mtg.sets.definitions.sos.cards.TabletOfDiscovery
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual

/**
 * Secrets of Strixhaven — Tablet of Discovery and Diary of Dreams.
 *
 * Tablet of Discovery: ETB mills a card (to graveyard) and grants a may-play permission keyed to
 * it; {T}: Add {R}; {T}: Add {R}{R} usable only on instants/sorceries.
 *
 * Diary of Dreams: a cast-an-instant-or-sorcery trigger accrues a page counter; the {5},{T}: draw
 * ability costs {1} less per page counter (genericCostReduction = countersOnSelf(page)).
 */
class SosArtifactsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TabletOfDiscovery)
        driver.registerCard(DiaryOfDreams)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20, skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("Tablet of Discovery mills a card on entry and grants permission to play it") {
        val driver = createDriver()
        val p1 = driver.player1
        val graveyardBefore = driver.getGraveyard(p1).size

        // Cast Tablet so its enters-the-battlefield trigger actually fires.
        driver.putCardOnTopOfLibrary(p1, "Mountain")
        val tablet = driver.putCardInHand(p1, "Tablet of Discovery")
        driver.giveMana(p1, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, tablet).isSuccess shouldBe true
        driver.bothPass() // resolve the spell (Tablet enters)
        if (driver.stackSize > 0) driver.bothPass() // resolve the ETB trigger

        // A card was milled into the graveyard, and it carries a may-play permission.
        val graveyard = driver.getGraveyard(p1)
        graveyard.size shouldBe graveyardBefore + 1
        val milled = graveyard.last()
        driver.state.mayPlayPermissions.any { milled in it.cardIds } shouldBe true
    }

    test("Tablet of Discovery's first ability adds one red mana") {
        val driver = createDriver()
        val p1 = driver.player1
        val tablet = driver.putPermanentOnBattlefield(p1, "Tablet of Discovery")
        driver.bothPass()

        driver.submit(ActivateAbility(p1, tablet, TabletOfDiscovery.activatedAbilities[0].id)).isSuccess shouldBe true
        driver.state.getEntity(p1)?.get<ManaPoolComponent>()?.red shouldBe 1
    }

    test("Tablet of Discovery's second ability adds two red mana restricted to instants/sorceries") {
        val driver = createDriver()
        val p1 = driver.player1
        val tablet = driver.putPermanentOnBattlefield(p1, "Tablet of Discovery")
        driver.bothPass()

        driver.submit(ActivateAbility(p1, tablet, TabletOfDiscovery.activatedAbilities[1].id)).isSuccess shouldBe true
        val pool = driver.state.getEntity(p1)?.get<ManaPoolComponent>()
        // The two red mana land in the restricted pool, not the unrestricted red counter.
        pool?.red shouldBe 0
        val restricted = pool?.restrictedMana ?: emptyList()
        restricted.size shouldBe 2
        restricted.all { it.restriction == ManaRestriction.InstantOrSorceryOnly } shouldBe true
    }

    test("Diary of Dreams gains a page counter whenever you cast an instant or sorcery") {
        val driver = createDriver()
        val p1 = driver.player1
        val diary = driver.putPermanentOnBattlefield(p1, "Diary of Dreams")

        // Cast Lightning Bolt (an instant) targeting the opponent.
        val bolt = driver.putCardInHand(p1, "Lightning Bolt")
        driver.giveMana(p1, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(p1, bolt, targets = listOf(driver.player2)).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getEntity(diary)?.get<CountersComponent>()?.counters?.get(CounterType.PAGE) shouldBe 1
    }

    test("Diary of Dreams draw ability costs {1} less per page counter") {
        val driver = createDriver()
        val p1 = driver.player1
        val diary = driver.putPermanentOnBattlefield(p1, "Diary of Dreams")

        // Accrue two page counters by casting two instants.
        repeat(2) {
            val bolt = driver.putCardInHand(p1, "Lightning Bolt")
            driver.giveMana(p1, com.wingedsheep.sdk.core.Color.RED, 1)
            driver.castSpell(p1, bolt, targets = listOf(driver.player2)).isSuccess shouldBe true
            driver.bothPass()
        }
        driver.state.getEntity(diary)?.get<CountersComponent>()?.counters?.get(CounterType.PAGE) shouldBe 2

        // {5} reduced by 2 page counters = {3}. Three mana is exactly enough.
        val handBefore = driver.getHandSize(p1)
        driver.giveColorlessMana(p1, 3)
        driver.submit(ActivateAbility(p1, diary, DiaryOfDreams.activatedAbilities[0].id)).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(p1) shouldBeGreaterThanOrEqual handBefore + 1
    }

    test("Diary of Dreams draw ability is unaffordable below its reduced cost") {
        val driver = createDriver()
        val p1 = driver.player1
        val diary = driver.putPermanentOnBattlefield(p1, "Diary of Dreams")

        // One page counter -> {5} - 1 = {4}. Three mana is not enough.
        val bolt = driver.putCardInHand(p1, "Lightning Bolt")
        driver.giveMana(p1, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(p1, bolt, targets = listOf(driver.player2)).isSuccess shouldBe true
        driver.bothPass()

        driver.giveColorlessMana(p1, 3)
        driver.submitExpectFailure(ActivateAbility(p1, diary, DiaryOfDreams.activatedAbilities[0].id))
    }
})
