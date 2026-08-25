package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ixalli's Keeper
 * {1}{G}
 * Creature — Human Shaman
 * 2/2
 *
 * {7}{G}, {T}, Sacrifice this creature: Target creature gets +5/+5 and gains trample until end
 * of turn.
 */
val IxallisKeeper = card("Ixalli's Keeper") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Shaman"
    oracleText = "{7}{G}, {T}, Sacrifice this creature: Target creature gets +5/+5 and gains " +
        "trample until end of turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}{G}"), Costs.Tap, Costs.SacrificeSelf)
        val boosted = target("target", Targets.Creature)
        effect = Effects.ModifyStats(5, 5, boosted) then
            Effects.GrantKeyword(Keyword.TRAMPLE, boosted)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Lake Hurwitz"
        flavorText = "The people of the Sun Empire worship the sun in three aspects. Ixalli is the Verdant Sun, who fosters growth in all things."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5b98537-abf4-46cb-853f-9561811ab439.jpg"
    }
}
