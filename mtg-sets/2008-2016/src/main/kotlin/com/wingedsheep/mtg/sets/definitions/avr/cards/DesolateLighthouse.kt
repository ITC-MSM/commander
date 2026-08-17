package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Desolate Lighthouse
 *
 * Land
 * {T}: Add {C}.
 * {1}{U}{R}, {T}: Draw a card, then discard a card.
 *
 * Two plain activated abilities: a written [Effects.AddColorlessMana] mana ability — the type line
 * carries no basic land subtype, so nothing grants it intrinsically — and [Patterns.Hand.loot],
 * whose default draw-1/discard-1 is exactly the printed rummage, behind a [Costs.Composite] of the
 * mana and the tap.
 */
val DesolateLighthouse = card("Desolate Lighthouse") {
    manaCost = ""
    colorIdentity = "RU"
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{1}{U}{R}, {T}: Draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}{R}"), Costs.Tap)
        effect = Patterns.Hand.loot()
        description = "{1}{U}{R}, {T}: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "227"
        artist = "Scott Chou"
        flavorText = "A lonely sentinel facing gales, hurricanes, and tides of homicidal spirits."
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16fb45bc-6152-4b01-9831-a8e80b1c1852.jpg?1783940649"
    }
}
