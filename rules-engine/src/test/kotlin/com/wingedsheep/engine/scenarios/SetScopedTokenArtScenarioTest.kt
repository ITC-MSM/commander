package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.effects.token.TokenArt
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

/**
 * A creature token shows the art of the set that printed the card creating it.
 *
 * Arahbo, the First Fang (Foundations) creates a 1/1 white Cat. Foundations prints its own Cat
 * (Scryfall `tfdn` #1), so that is the art the token must carry — not the engine-wide generic Cat,
 * and not the arbitrary printing the client used to land on by asking Scryfall for a card named
 * "Cat" when the token carried no image at all.
 *
 * The art comes from `FoundationsSet.tokenArt`, resolved through
 * [com.wingedsheep.engine.registry.TokenArtRegistry] — the card's own script says nothing about
 * art, which is what lets a reprint mint its own set's token.
 */
class SetScopedTokenArtScenarioTest : ScenarioTestBase() {

    /** `tfdn` #1 — Foundations' Cat token. */
    private val foundationsCat = "2885d54c-9fb2-4f01-8937-54f8ac1ce5bc"

    init {
        test("Arahbo's Cat token carries the Foundations Cat art, not the generic one") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Arahbo, the First Fang")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardInLibrary(1, "Plains")
                .build()

            game.castSpell(1, "Arahbo, the First Fang").error shouldBe null
            game.resolveStack()

            val catId = game.findPermanent("Cat Token")
            catId shouldNotBe null
            val art = game.state.getEntity(catId!!)?.get<CardComponent>()?.imageUri

            art shouldNotBe null
            art!! shouldContain foundationsCat
            // Specifically *not* the set-agnostic fallback, which is what every Cat-making card
            // outside Foundations still gets.
            art shouldNotBe TokenArt.IMAGES["Cat"]
        }

        test("a set that prints no token art falls back to the generic table") {
            // Ajani's Pridemate is not a token maker; Raise the Alarm (a non-FDN Soldier maker)
            // stands in for "any card whose set declares no token art of its own".
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Raise the Alarm")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardInLibrary(1, "Plains")
                .build()

            game.castSpell(1, "Raise the Alarm").error shouldBe null
            game.resolveStack()

            val soldier = game.findPermanent("Soldier Token")
            soldier shouldNotBe null
            game.state.getEntity(soldier!!)?.get<CardComponent>()?.imageUri shouldBe
                TokenArt.IMAGES["Soldier"]
        }
    }
}

