package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Elfhame Palace
 * Land
 * This land enters tapped.
 * {T}: Add {G} or {W}.
 */
val ElfhamePalace = card("Elfhame Palace") {
    typeLine = "Land"
    colorIdentity = "GW"
    oracleText = "This land enters tapped.\n{T}: Add {G} or {W}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "322"
        artist = "Jerry Tiritilli"
        flavorText = "Llanowar has seven elfhames, or kingdoms, each with its own ruler. Their palaces are objects of awe, wonder, and envy."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65986555-a5d7-497e-876f-b8d967d6aa5b.jpg?1562915620"
    }
}
