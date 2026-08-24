package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skyshroud Vampire
 * {3}{B}{B}
 * Creature — Vampire
 * 3/3
 * Flying
 * Discard a creature card: This creature gets +2/+2 until end of turn.
 */
val SkyshroudVampire = card("Skyshroud Vampire") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Discard a creature card: This creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Discard(GameObjectFilter.Creature)
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Discard a creature card: This creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "157"
        artist = "Gary Leach"
        flavorText = "\"If it tastes one drop of elvish blood I will cast it from the shroud to see it burn.\"\n" +
            "—Eladamri, Lord of Leaves"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eed2c97b-f003-436c-9faa-5518aba42fc1.jpg"
    }
}
