package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inner Demons Gangsters
 * {3}{B}
 * Creature — Human Rogue Villain
 * 3/4
 * Discard a card: This creature gets +1/+0 and gains menace until end of turn. Activate only as a sorcery. (It can't be blocked except by two or more creatures.)
 */
val InnerDemonsGangsters = card("Inner Demons Gangsters") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue Villain"
    oracleText = "Discard a card: This creature gets +1/+0 and gains menace until end of turn. Activate only as a sorcery. (It can't be blocked except by two or more creatures.)"
    power = 3
    toughness = 4
    activatedAbility {
        cost = Costs.DiscardCard
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self)
        )
        timing = TimingRule.SorcerySpeed
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Nathaniel Himawan"
        flavorText = "\"Inner Demons? Even for a guy named Mister Negative, that's a bit on the nose.\"\n—Spider-Man"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0252819-4eda-457d-9688-b08b83b1edc9.jpg?1757377113"
    }
}
