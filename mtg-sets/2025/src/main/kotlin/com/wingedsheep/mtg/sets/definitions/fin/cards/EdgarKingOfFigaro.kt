package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.WinCoinFlips
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Edgar, King of Figaro
 * {4}{U}{U}
 * Legendary Creature — Human Artificer Noble
 * 4/5
 *
 * When Edgar enters, draw a card for each artifact you control.
 * Two-Headed Coin — The first time you flip one or more coins each turn, those coins come up heads
 * and you win those flips.
 *
 * The Two-Headed Coin static is the engine's [WinCoinFlips] coin-flip result replacement
 * (CR 705.3), consulted by the coin-flip executors: on the controller's first coin-flip event each
 * turn, every coin is forced to heads/win.
 */
val EdgarKingOfFigaro = card("Edgar, King of Figaro") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Artificer Noble"
    power = 4
    toughness = 5
    oracleText = "When Edgar enters, draw a card for each artifact you control.\n" +
        "Two-Headed Coin — The first time you flip one or more coins each turn, those coins come " +
        "up heads and you win those flips."

    // When Edgar enters, draw a card for each artifact you control.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count()
        )
        description = "When Edgar enters, draw a card for each artifact you control."
    }

    // Two-Headed Coin — The first time you flip one or more coins each turn, those coins come up
    // heads and you win those flips.
    staticAbility {
        ability = WinCoinFlips(firstFlipEachTurn = true)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Jake Murray"
        flavorText = "\"Sabin...let's settle this with the toss of a coin.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/5/950ee302-5512-43c5-ac7c-b2b06f4177bf.jpg?1782686557"
    }
}
