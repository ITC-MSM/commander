package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sea Spirit
 * {4}{U}
 * Creature — Elemental Spirit
 * 2/3
 *
 * {U}: This creature gets +1/+0 until end of turn.
 *
 * Firebreathing in blue — the same shape as its red sibling Flame Spirit: a mana-only activated
 * ability whose effect is `Effects.ModifyStats` onto `EffectTarget.Self`, taking the facade's
 * default `Duration.EndOfTurn`.
 */
val SeaSpirit = card("Sea Spirit") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Spirit"
    power = 2
    toughness = 3
    oracleText = "{U}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Rob Alexander"
        flavorText = "\"It rose above our heads, above the ship, and still higher yet. No foggy, ice-laden sea in the world could frighten me more.\"\n—General Jarkeld, the Arctic Fox"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2d93d05-98bc-4504-9045-dedb925895ae.jpg"
    }
}
