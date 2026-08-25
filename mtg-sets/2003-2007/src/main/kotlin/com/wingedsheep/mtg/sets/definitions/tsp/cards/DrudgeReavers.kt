package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Drudge Reavers
 * {3}{B}
 * Creature — Skeleton
 * 2/1
 * Flash (You may cast this spell any time you could cast an instant.)
 * {B}: Regenerate this creature.
 *
 * Flash on a regenerating Skeleton makes it an ambush blocker that survives the trade — the
 * shield taps it and removes it from combat instead of letting it die (CR 701.15).
 */
val DrudgeReavers = card("Drudge Reavers") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton"
    power = 2
    toughness = 1
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "{B}: Regenerate this creature."

    keywords(Keyword.FLASH)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Greg Staples"
        flavorText = "\"The surface of the land is blanched with salt, but the soil beneath is reddened with death—a fertile ground for necromancy.\"\n—Lim-Dûl the Necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03c07d9a-afed-4028-9fa1-ec439b60f08f.jpg"
    }
}
