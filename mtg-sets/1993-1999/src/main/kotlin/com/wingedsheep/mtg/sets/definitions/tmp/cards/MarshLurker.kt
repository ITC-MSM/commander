package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Marsh Lurker
 * {3}{B}
 * Creature — Beast
 * 3/2
 * Sacrifice a Swamp: This creature gains fear until end of turn. (It can't be blocked except by artifact creatures and/or black creatures.)
 */
val MarshLurker = card("Marsh Lurker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 2
    oracleText = "Sacrifice a Swamp: This creature gains fear until end of turn. (It can't be blocked except by artifact creatures and/or black creatures.)"

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Land.withSubtype(Subtype.SWAMP))
        effect = Effects.GrantKeyword(Keyword.FEAR, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Tom Kyffin"
        flavorText = "From the mists it rises, into the mists it retreats; through the mists it walks, fearless and unseen."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90c4b759-f53d-4977-8d97-a93762622e75.jpg"
    }
}
