package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Molten Ravager
 * {2}{R}
 * Creature — Elemental
 * 0/4
 *
 * {R}: This creature gets +1/+0 until end of turn.
 */
val MoltenRavager = card("Molten Ravager") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    oracleText = "{R}: This creature gets +1/+0 until end of turn."
    power = 0
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Dave Kendall"
        flavorText = "\"Only the foolhardy would venture into the Akoum Mountains without a lullmage to tame the raging rocks and living fires.\"\n—Sachir, Akoum Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9ebc7622-e7a7-419c-abb4-d9d339e6cdb0.jpg?1783942142"
    }
}
