package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Carrion Ants
 * {2}{B}{B}
 * Creature — Insect
 * 0/1
 *
 * {1}: This creature gets +1/+1 until end of turn.
 */
val CarrionAnts = card("Carrion Ants") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 0
    toughness = 1
    oracleText = "{1}: This creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "90"
        artist = "Richard Thomas"
        flavorText = "\"'War is no picnic,' my father liked to say. But the Ants seemed to disagree.\" —*General " +
            "Chanek Valteroth*"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbc0b009-3951-4aa3-985a-97139882da7e.jpg?1783948069"
    }
}
