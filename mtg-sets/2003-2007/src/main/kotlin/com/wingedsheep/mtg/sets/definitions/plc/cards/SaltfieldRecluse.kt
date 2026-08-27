package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Saltfield Recluse
 * {2}{W}
 * Creature — Human Rebel Cleric
 * 1/2
 * {T}: Target creature gets -2/-0 until end of turn.
 */
val SaltfieldRecluse = card("Saltfield Recluse") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Rebel Cleric"
    power = 1
    toughness = 2
    oracleText = "{T}: Target creature gets -2/-0 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-2, 0, t)
        description = "{T}: Target creature gets -2/-0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Brian Despain"
        flavorText = "He remembers a past of light and healing. But he lives the bitter present—parching salt, scouring wind, and the withering heat of the desert."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5ffed4e4-0f54-4ab1-8563-ac4be7ae8309.jpg"
    }
}
