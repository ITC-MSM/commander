package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Furtive Analyst
 * {2}{U}
 * Creature — Human Wizard
 * 1/4
 * Vigilance
 * {2}, {T}: Draw a card, then discard a card.
 */
val FurtiveAnalyst = card("Furtive Analyst") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "Vigilance\n{2}, {T}: Draw a card, then discard a card."
    power = 1
    toughness = 4

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Effects.DrawCards(1) then Effects.Discard(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Marcela Bolívar"
        flavorText = "Where had she first seen that symbol? She usually took such detailed notes, " +
            "but her memory of it was like a fading nightmare."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98c55aff-baed-4fb5-a490-abd59b8df5e7.jpg?1783917037"
    }
}
