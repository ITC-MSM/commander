package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kiln Fiend
 * {1}{R}
 * Creature — Elemental Beast
 * 1/2
 *
 * Whenever you cast an instant or sorcery spell, this creature gets +3/+0 until end of turn.
 */
val KilnFiend = card("Kiln Fiend") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Beast"
    oracleText = "Whenever you cast an instant or sorcery spell, this creature gets +3/+0 until end of turn."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.ModifyStats(3, 0, EffectTarget.Self)
        description = "Whenever you cast an instant or sorcery spell, this creature gets +3/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Adi Granov"
        flavorText = "It traps an explosion within its stony skin."
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c584268-67c3-411b-a26c-aee3adf23872.jpg?1783941974"
    }
}
