package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thunder Wall
 * {1}{U}{U}
 * Creature — Wall
 * 0/2
 *
 * Defender (This creature can't attack.)
 * Flying
 * {U}: This creature gets +1/+1 until end of turn.
 *
 * Two engine-live keywords plus firebreathing: the pump is `Effects.ModifyStats` onto
 * `EffectTarget.Self` at the facade's default `Duration.EndOfTurn`.
 */
val ThunderWall = card("Thunder Wall") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 2
    oracleText = "Defender (This creature can't attack.)\n" +
        "Flying\n" +
        "{U}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.DEFENDER, Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Richard Thomas"
        flavorText = "\"The Lemures had barely taken wing when the sky roared with thunder. The swarm of little beasts wavered, divided, and fell, crashing to the earth.\"\n—General Jarkeld, the Arctic Fox"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fc5d510-c4f7-4a09-bf86-83c3fa3f8928.jpg"
    }
}
