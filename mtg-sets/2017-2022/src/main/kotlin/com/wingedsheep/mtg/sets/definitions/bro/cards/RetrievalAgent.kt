package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Retrieval Agent
 * {3}{U}
 * Creature — Human Soldier
 * 2/5
 * {2}: This creature gets +1/-1 until end of turn.
 */
val RetrievalAgent = card("Retrieval Agent") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 5
    oracleText = "{2}: This creature gets +1/-1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Vladimir Krisetskiy"
        flavorText = "The humming tome burned his leather glove, nearly taking his hand along with it. Then a strange power coursed through him."
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f333a7b1-936e-42f3-ba22-2c76dd2f1c9a.jpg?1783920107"
    }
}
