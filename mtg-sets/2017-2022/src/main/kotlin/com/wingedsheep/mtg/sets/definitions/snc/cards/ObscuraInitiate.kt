package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Obscura Initiate
 * {2}{U}
 * Creature — Bird Citizen
 * 2 / 2
 * Flying
 * {1}{W/B}: This creature gains lifelink until end of turn.
 */
val ObscuraInitiate = card("Obscura Initiate") {
    manaCost = "{2}{U}"
    colorIdentity = "BUW"
    typeLine = "Creature — Bird Citizen"
    oracleText = "Flying\n{1}{W/B}: This creature gains lifelink until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{W/B}")
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Kim Sokol"
        flavorText = "Obscura initiates are required to bring a gift of information. They are judged on both how useful it is and how hard it was to obtain."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67774a11-158b-437c-ac16-2d42fbb5c223.jpg?1783923142"
    }
}
