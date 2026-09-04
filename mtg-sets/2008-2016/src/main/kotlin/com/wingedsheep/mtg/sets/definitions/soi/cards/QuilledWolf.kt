package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Quilled Wolf (Shadows over Innistrad #222)
 * {1}{G}
 * Creature — Wolf
 * 2 / 2
 *
 * {5}{G}: This creature gets +4/+4 until end of turn.
 */
val QuilledWolf = card("Quilled Wolf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 2
    oracleText = "{5}{G}: This creature gets +4/+4 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{5}{G}")
        effect = Effects.ModifyStats(4, 4, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "222"
        artist = "Bastien L. Deharme"
        flavorText = "\"I know a ranger that thought to make a cloak of its hide. He ended up losing an eye for his trouble.\"\n—Raf Gyel of the Quiver of Kessig"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa1569b5-94ef-4ba5-98c6-f1bd4f73c7d5.jpg?1783937724"
    }
}
