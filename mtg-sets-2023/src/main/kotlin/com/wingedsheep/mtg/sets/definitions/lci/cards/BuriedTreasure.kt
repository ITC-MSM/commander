package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Buried Treasure
 * {2}
 * Artifact — Treasure
 * {T}, Sacrifice this artifact: Add one mana of any color.
 * {5}, Exile this card from your graveyard: Discover 5. Activate only as a sorcery.
 */
val BuriedTreasure = card("Buried Treasure") {
    manaCost = "{2}"
    typeLine = "Artifact — Treasure"
    oracleText = "{T}, Sacrifice this artifact: Add one mana of any color.\n{5}, Exile this card from your graveyard: Discover 5. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.ExileSelf)
        effect = Effects.Discover(5)
        timing = TimingRule.SorcerySpeed
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Jarel Threat"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c9c45b6-dedd-4481-a06a-c83ace2f18fa.jpg?1782694415"
    }
}
