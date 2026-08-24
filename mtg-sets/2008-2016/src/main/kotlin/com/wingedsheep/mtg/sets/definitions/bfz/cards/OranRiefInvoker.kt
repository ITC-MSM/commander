package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Oran-Rief Invoker
 * {1}{G}
 * Creature — Human Shaman
 * 2/2
 * {8}: This creature gets +5/+5 and gains trample until end of turn.
 */
val OranRiefInvoker = card("Oran-Rief Invoker") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{8}: This creature gets +5/+5 and gains trample until end of turn."

    activatedAbility {
        cost = Costs.Mana("{8}")
        effect = Effects.Composite(
            Effects.ModifyStats(5, 5, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Anastasia Ovchinnikova"
        flavorText = "\"The world was not hostile to us—we were beneath its notice, and presented no danger.\"\n" +
            "—*The Invokers' Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/0957875c-e1a2-4d14-8b61-c806903fc760.jpg?1783938186"
    }
}
