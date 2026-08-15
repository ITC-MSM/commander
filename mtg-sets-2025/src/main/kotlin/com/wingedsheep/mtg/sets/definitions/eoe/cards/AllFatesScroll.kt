package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * All-Fates Scroll
 * {3}
 * Artifact
 * {T}: Add one mana of any color.
 * {7}, {T}, Sacrifice this artifact: Draw X cards, where X is the number of differently named
 * lands you control.
 */
val AllFatesScroll = card("All-Fates Scroll") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Add one mana of any color.\n" +
        "{7}, {T}, Sacrifice this artifact: Draw X cards, where X is the number of differently named lands you control."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(DynamicAmounts.differentlyNamedLandsYouControl())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Sam Guay"
        flavorText = "For navigating that which was, is, and will be."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a5ed010-cb17-45df-b169-ebc807dae534.jpg?1752947516"
        ruling("2025-07-25", "To determine the number of differently named lands you control, count each land you control once, but only if its English name isn't exactly the same as another land you've already counted this way.")
        ruling("2025-07-25", "The value of X is determined only once, as All-Fates Scroll's last ability resolves.")
    }
}
