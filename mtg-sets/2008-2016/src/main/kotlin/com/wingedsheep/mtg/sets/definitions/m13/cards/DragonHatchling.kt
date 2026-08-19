package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dragon Hatchling
 * {1}{R}
 * Creature — Dragon
 * 0/1
 *
 * Flying
 * {R}: This creature gets +1/+0 until end of turn.
 */
val DragonHatchling = card("Dragon Hatchling") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Flying\n{R}: This creature gets +1/+0 until end of turn."
    power = 0
    toughness = 1

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "David Palumbo"
        flavorText = "\"Those dragons grow fast. For a while they feed on squirrels and goblins and then suddenly you're missing a mammoth.\"\n—Hurdek, Mazar mammoth trainer"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed599d52-f2d9-4913-ad88-70f8aa4af7b9.jpg?1783940484"
    }
}
