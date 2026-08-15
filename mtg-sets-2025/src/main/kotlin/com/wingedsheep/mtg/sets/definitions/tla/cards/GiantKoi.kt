package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Giant Koi
 * {4}{U}{U}
 * Creature — Fish
 * 5/7
 * Waterbend {3}: This creature can't be blocked this turn. (While paying a waterbend cost, you can
 *   tap your artifacts and creatures to help. Each one pays for {1}.)
 * Islandcycling {2} ({2}, Discard this card: Search your library for an Island card, reveal it, put
 *   it into your hand, then shuffle.)
 */
val GiantKoi = card("Giant Koi") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    oracleText = "Waterbend {3}: This creature can't be blocked this turn. (While paying a waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)\nIslandcycling {2} ({2}, Discard this card: Search your library for an Island card, reveal it, put it into your hand, then shuffle.)"
    power = 5
    toughness = 7

    activatedAbility {
        cost = Costs.Mana("{3}")
        hasWaterbend = true
        effect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, EffectTarget.Self)
    }

    keywordAbility(KeywordAbility.typecycling("Island", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Nathaniel Himawan"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3938ec1f-979c-48e5-bb13-bebace006a8f.jpg?1764120259"
    }
}
