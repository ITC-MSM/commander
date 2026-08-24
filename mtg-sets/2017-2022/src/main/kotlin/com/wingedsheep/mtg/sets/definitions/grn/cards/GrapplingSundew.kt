package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grappling Sundew
 * {1}{G}
 * Creature — Plant
 * 0/4
 * Defender, reach
 * {4}{G}: This creature gains indestructible until end of turn. (Damage and effects that say "destroy" don't destroy this creature.)
 */
val GrapplingSundew = card("Grappling Sundew") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    oracleText = "Defender, reach\n" +
        "{4}{G}: This creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy this creature.)"
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER, Keyword.REACH)
    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "131"
        artist = "Sung Choi"
        flavorText = "Some rooftop gardens attract bees; others capture dragons."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f74d8d76-8091-424d-ad11-e8a1faae584d.jpg?1783934152"
    }
}
