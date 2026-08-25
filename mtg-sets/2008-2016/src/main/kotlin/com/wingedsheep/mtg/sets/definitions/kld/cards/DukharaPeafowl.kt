package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dukhara Peafowl
 * {4}
 * Artifact Creature — Bird
 * 2/4
 * {U}: This creature gains flying until end of turn.
 *
 * A colorless artifact creature whose only ability costs blue mana — hence the `U` color identity
 * on a `{4}` card. [Effects.GrantKeyword] defaults to `Duration.EndOfTurn`, so the printed duration
 * needs no argument.
 */
val DukharaPeafowl = card("Dukhara Peafowl") {
    manaCost = "{4}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Bird"
    oracleText = "{U}: This creature gains flying until end of turn."
    power = 2
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Craig J Spearing"
        flavorText = "Elegant filigree birds perch along the eleven bridges that cross Canal Dukhara."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5c54795-d555-4972-b72d-b2d2374bed9b.jpg?1783937158"
    }
}
