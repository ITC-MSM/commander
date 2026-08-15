package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodtallow Candle
 * {1}
 * Artifact
 * {6}, {T}, Sacrifice Bloodtallow Candle: Target creature gets -5/-5 until end of turn.
 */
val BloodtallowCandle = card("Bloodtallow Candle") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{6}, {T}, Sacrifice Bloodtallow Candle: Target creature gets -5/-5 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap, Costs.SacrificeSelf)
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(-5, -5, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "212"
        artist = "Alayna Danner"
        flavorText = "\"Bring me an angel feather, and I will give you one death in return. There can be no turning back once the candle is lit.\" —Whisper, blood liturgist"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f8125b6-911e-4663-962a-d741984c927d.jpg?1562736519"
    }
}
