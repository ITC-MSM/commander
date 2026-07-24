package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsDiscardedEvent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.shm.cards.WiltLeafLiege
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Wilt-Leaf Liege (SHM #245, reprinted as FDN #668) — {1}{G/W}{G/W}{G/W} 4/4 Creature — Elf Knight.
 *
 * "Other green creatures you control get +1/+1.
 *  Other white creatures you control get +1/+1.
 *  If a spell or ability an opponent controls causes you to discard this card, put it onto the
 *  battlefield instead of putting it into your graveyard."
 *
 * Covers the two cumulative colour lords and the new [ZoneChangeCause.DiscardedByOpponentEffect]
 * self-replacement — including that it stays off for a discard with no opponent's spell behind it.
 */
class WiltLeafLiegeScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(WiltLeafLiege)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun power(driver: GameTestDriver, id: EntityId) = driver.state.projectedState.getPower(id)
    fun toughness(driver: GameTestDriver, id: EntityId) = driver.state.projectedState.getToughness(id)

    test("pumps other green creatures you control but not itself") {
        val driver = newDriver()
        val me = driver.player1

        val liege = driver.putCreatureOnBattlefield(me, "Wilt-Leaf Liege")
        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears") // green 2/2

        power(driver, bears) shouldBe 3
        toughness(driver, bears) shouldBe 3
        // The Liege is green and white itself, but both clauses say "other".
        power(driver, liege) shouldBe 4
        toughness(driver, liege) shouldBe 4
    }

    test("the two lord clauses stack on a green and white creature") {
        val driver = newDriver()
        val me = driver.player1

        driver.putCreatureOnBattlefield(me, "Wilt-Leaf Liege")
        // A second Liege is itself both green and white, so the first one's two clauses both apply.
        val other = driver.putCreatureOnBattlefield(me, "Wilt-Leaf Liege")

        power(driver, other) shouldBe 6
        toughness(driver, other) shouldBe 6
    }

    test("does not pump creatures an opponent controls") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        driver.putCreatureOnBattlefield(me, "Wilt-Leaf Liege")
        val theirBears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears")

        power(driver, theirBears) shouldBe 2
    }

    test("an opponent's discard spell puts it onto the battlefield instead of the graveyard") {
        val driver = newDriver()
        val me = driver.player1
        val foe = driver.player2

        val liege = driver.putCardInHand(foe, "Wilt-Leaf Liege")
        // Give the victim a second card so Mind Rot's two discards have something else to take.
        val bears = driver.putCardInHand(foe, "Grizzly Bears")

        val mindRot = driver.putCardInHand(me, "Mind Rot")
        driver.giveMana(me, Color.BLACK, 3)
        driver.castSpell(me, mindRot, listOf(foe)).isSuccess shouldBe true
        driver.bothPass()
        driver.submitCardSelection(foe, listOf(liege, bears))

        // It ends up on its owner's battlefield, not in their graveyard.
        driver.state.getZone(ZoneKey(foe, Zone.BATTLEFIELD)).contains(liege) shouldBe true
        driver.state.getZone(ZoneKey(foe, Zone.GRAVEYARD)).contains(liege) shouldBe false

        // ...and it still counts as having been discarded (Scryfall ruling 2024-11-08).
        driver.events.filterIsInstance<CardsDiscardedEvent>()
            .any { liege in it.cardIds } shouldBe true
    }

    test("your own discard spell leaves it in the graveyard") {
        val driver = newDriver()
        val me = driver.player1

        val liege = driver.putCardInHand(me, "Wilt-Leaf Liege")
        val bears = driver.putCardInHand(me, "Grizzly Bears")

        // Mind Rot targeting yourself: the causing spell is one you control, so the clause is off.
        val mindRot = driver.putCardInHand(me, "Mind Rot")
        driver.giveMana(me, Color.BLACK, 3)
        driver.castSpell(me, mindRot, listOf(me)).isSuccess shouldBe true
        driver.bothPass()
        driver.submitCardSelection(me, listOf(liege, bears))

        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(liege) shouldBe true
        driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD)).contains(liege) shouldBe false
    }
})
