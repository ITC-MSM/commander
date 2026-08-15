package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Seashell Cameo
 * {3}
 * Artifact
 *
 * {T}: Add {W} or {U}.
 */
val SeashellCameo = card("Seashell Cameo") {
    manaCost = "{3}"
    colorIdentity = "WU"
    typeLine = "Artifact"
    oracleText = "{T}: Add {W} or {U}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "311"
        artist = "Tony Szczudlo"
        flavorText = "\"Today a seashell fell from the empty sky here in Kinymu, a hundred leagues from the sea. I'm torn—shall I carve a woman or a bird?\"\n—Isel, master carver"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9efdbcad-e2e4-4f54-ade5-920b1853109e.jpg?1562927074"
    }
}
