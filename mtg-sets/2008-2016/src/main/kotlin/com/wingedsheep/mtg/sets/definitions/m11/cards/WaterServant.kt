package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Water Servant
 * {2}{U}{U}
 * Creature — Elemental
 * 3/4
 *
 * {U}: This creature gets +1/-1 until end of turn.
 * {U}: This creature gets -1/+1 until end of turn.
 */
val WaterServant = card("Water Servant") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 4
    oracleText = "{U}: This creature gets +1/-1 until end of turn.\n" +
        "{U}: This creature gets -1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.ModifyStats(-1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Igor Kieryluk"
        flavorText = "\"This creature has innate perceptiveness. It knows when to rise and when to vanish into the tides.\"\n" +
            "—Jestus Dreya, *Of Elements and Eternity*"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02a3062e-8b83-4ee4-8139-8eee84df37fe.jpg?1783941820"
    }
}
