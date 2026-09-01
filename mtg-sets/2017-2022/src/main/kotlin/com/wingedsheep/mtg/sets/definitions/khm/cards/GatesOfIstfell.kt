package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Gates of Istfell
 * Land
 * This land enters tapped.
 * {T}: Add {W}.
 * {2}{W}{U}{U}, {T}, Sacrifice this land: You gain 2 life and draw two cards.
 *
 * One of Kaldheim's five "realm" lands: a tapped mono-colour source with an expensive sacrifice
 * ability in two colours. The sacrifice is part of the cost, so the land is already gone when the
 * draw resolves and countering the ability does not give it back.
 */
val GatesOfIstfell = card("Gates of Istfell") {
    manaCost = ""
    colorIdentity = "UW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {W}.\n" +
        "{2}{W}{U}{U}, {T}, Sacrifice this land: You gain 2 life and draw two cards."

    replacementEffect(EntersTapped())

    // {T}: Add {W}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE, 1)
        manaAbility = true
    }

    // {2}{W}{U}{U}, {T}, Sacrifice this land: You gain 2 life and draw two cards.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{W}{U}{U}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.Composite(
            Effects.GainLife(2),
            Effects.DrawCards(2)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "256"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/2627acb7-57d9-4429-9bc5-e7dd444d8d48.jpg"
    }
}
