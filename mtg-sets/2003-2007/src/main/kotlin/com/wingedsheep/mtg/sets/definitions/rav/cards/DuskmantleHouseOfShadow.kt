package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Duskmantle, House of Shadow
 * Land
 * {T}: Add {C}.
 * {U}{B}, {T}: Target player mills a card.
 */
val DuskmantleHouseOfShadow = card("Duskmantle, House of Shadow") {
    manaCost = ""
    colorIdentity = "UB"
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{U}{B}, {T}: Target player mills a card."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{B}"), Costs.Tap)
        val p = target("target player", Targets.Player)
        effect = Patterns.Library.mill(1, p)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "277"
        artist = "Martina Pilcerova"
        flavorText = "In a space where there is no room, in a structure that was never built, meets the guild that doesn't exist."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d3c85e2-58a5-4469-85ea-7e89268f310c.jpg"
    }
}
