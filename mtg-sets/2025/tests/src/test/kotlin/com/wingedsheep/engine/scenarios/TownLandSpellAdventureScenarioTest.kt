package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.IshgardTheHolySee
import com.wingedsheep.mtg.sets.definitions.fin.cards.LindblumIndustrialRegency
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Town land // spell DFC (FIN) — the inverse-Adventure layout (CR 715).
 *
 * These cards are land-primary Adventure cards: front = `Land — Town`, back = an instant/sorcery
 * `— Adventure`. From hand you either **play the land** or **cast the Adventure spell**; casting the
 * Adventure resolves its effect, exiles the card, and grants permission to **play the land** from
 * exile later (CR 715.3d — the engine's generic may-play permission covers playing a land, not just
 * casting a creature).
 *
 * No new layout was needed: `CardLayout.ADVENTURE` already models "spell resolves → exile → replay
 * the main face from exile"; the only seams that needed work were (a) enumerating the spell face for
 * a land-primary card (covered by `TownLandSpellAdventureEnumerationTest`) and (b) the existing
 * `CastFromZoneEnumerator` / `PlayLandHandler` exile-land path, exercised here end to end.
 *
 * Lindblum, Industrial Regency // Mage Siege — land face `Land — Town` ({T}: Add {R}); Adventure
 * face Mage Siege {2}{R}, Instant, "Create a 0/1 black Wizard creature token ...".
 */
class TownLandSpellAdventureScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(LindblumIndustrialRegency)
        driver.registerCard(IshgardTheHolySee)
        return driver
    }

    fun startAtMain(driver: GameTestDriver): com.wingedsheep.sdk.model.EntityId {
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40, "Plains" to 20), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return player
    }

    test("the land primary can be played from hand and enters tapped") {
        val driver = createDriver()
        val player = startAtMain(driver)

        val ishgard = driver.putCardInHand(player, "Ishgard, the Holy See")
        driver.playLand(player, ishgard).isSuccess shouldBe true

        driver.getPermanents(player) shouldContain ishgard
        driver.isTapped(ishgard) shouldBe true // "This land enters tapped."
    }

    test("casting the Adventure spell resolves its effect, exiles the card, then the land is playable from exile") {
        val driver = createDriver()
        val player = startAtMain(driver)

        val lindblum = driver.putCardInHand(player, "Lindblum, Industrial Regency")
        driver.giveMana(player, Color.RED, 3) // {2}{R}

        val permanentsBefore = driver.getPermanents(player).size

        // Cast Mage Siege (the Adventure spell face, faceIndex = 0) — not the land.
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = lindblum,
                faceIndex = 0,
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        // The spell effect happened: a 0/1 Wizard token was created.
        val tokenWizard = driver.getPermanents(player).firstOrNull { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.typeLine?.toString()?.contains("Wizard") == true
        }
        (tokenWizard != null) shouldBe true
        driver.getPermanents(player).size shouldBe permanentsBefore + 1 // the token, not the land

        // The card was exiled by its own resolution (CR 715.3d), not put in the graveyard.
        driver.getExile(player) shouldContain lindblum
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)) shouldNotContain lindblum

        // A may-play permission for the exiled card was granted to its caster.
        driver.state.mayPlayPermissions.any { lindblum in it.cardIds && it.controllerId == player } shouldBe true

        // Now play the land half from exile.
        driver.playLand(player, lindblum).isSuccess shouldBe true

        driver.getPermanents(player) shouldContain lindblum
        driver.getExile(player) shouldNotContain lindblum
        driver.isTapped(lindblum) shouldBe true // the land still "enters tapped" from exile

        // The permission is cleaned up so the land can't be re-authorized if it returns to exile.
        driver.state.mayPlayPermissions.any { lindblum in it.cardIds } shouldBe false
    }

    test("playing the land from exile consumes the turn's land drop") {
        val driver = createDriver()
        val player = startAtMain(driver)

        val lindblum = driver.putCardInHand(player, "Lindblum, Industrial Regency")
        val mountain = driver.putCardInHand(player, "Mountain")
        driver.giveMana(player, Color.RED, 3)

        // Cast Mage Siege → Lindblum is exiled with play permission.
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = lindblum,
                faceIndex = 0,
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // Play the land from exile — this is a normal land play and uses the land drop.
        driver.playLand(player, lindblum).isSuccess shouldBe true

        // A second land play this turn is now illegal.
        driver.submit(PlayLand(player, mountain)).isSuccess shouldBe false
    }
})
