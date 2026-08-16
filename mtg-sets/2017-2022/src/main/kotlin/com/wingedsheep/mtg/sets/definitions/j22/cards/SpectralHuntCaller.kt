package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Spectral Hunt-Caller
 * {4}{G}
 * Creature — Wolf Spirit
 * 4/4
 * {5}{G}: Creatures you control get +1/+1 and gain trample until end of turn.
 *
 * The overrun shape: `Patterns.Group.pumpAndGrantToAll` fans a +1/+1 [ModifyStatsEffect] and a
 * trample [GrantKeywordEffect] — both `Duration.EndOfTurn` — over every creature you control at
 * resolution (same pattern as Glorious Sunrise's first mode).
 */
val SpectralHuntCaller = card("Spectral Hunt-Caller") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf Spirit"
    power = 4
    toughness = 4
    oracleText = "{5}{G}: Creatures you control get +1/+1 and gain trample until end of turn."

    // {5}{G}: Creatures you control get +1/+1 and gain trample until end of turn.
    activatedAbility {
        cost = Costs.Mana("{5}{G}")
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 1,
            toughness = 1,
            keyword = Keyword.TRAMPLE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Uriah Voth"
        flavorText = "The nightsong begins with a lone howl, but soon swells to a deafening chorus."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/358fc99b-b2e5-4967-ba5a-ab2caee1751c.jpg?1783919178"
    }
}
