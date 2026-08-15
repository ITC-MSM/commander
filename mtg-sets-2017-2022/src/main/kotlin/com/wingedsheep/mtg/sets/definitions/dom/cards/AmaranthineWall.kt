package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Amaranthine Wall
 * {4}
 * Artifact Creature — Wall
 * 0/6
 * Defender
 * {2}: This creature gains indestructible until end of turn.
 */
val AmaranthineWall = card("Amaranthine Wall") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Wall"
    power = 0
    toughness = 6
    oracleText = "Defender\n{2}: This creature gains indestructible until end of turn."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Jason Felix"
        flavorText = "\"Neither its appearance nor its temperature varies as the years pass, an eternal testament to the forces that shaped Dominaria.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73b49065-0a46-4813-a721-71d718e73d18.jpg?1562737811"
    }
}
