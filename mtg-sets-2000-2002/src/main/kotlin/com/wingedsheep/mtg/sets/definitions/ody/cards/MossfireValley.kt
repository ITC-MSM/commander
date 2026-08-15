package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Mossfire Valley
 * Land
 * {1}, {T}: Add {R}{G}.
 */
val MossfireValley = card("Mossfire Valley") {
    typeLine = "Land"
    colorIdentity = "RG"
    oracleText = "{1}, {T}: Add {R}{G}."

    activatedAbility {
        cost = AbilityCost.Composite(listOf(
            Costs.Mana(ManaCost.parse("{1}")),
            AbilityCost.Tap,
        ))
        effect = Effects.AddMana(Color.RED).then(Effects.AddMana(Color.GREEN))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "321"
        artist = "John Avon"
        flavorText = "Lush growth spreads like a ferocious blaze across the Otarian hillsides."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b6c08ce-d01d-4ae6-81d3-149679e27e6a.jpg?1562914931"
    }
}
