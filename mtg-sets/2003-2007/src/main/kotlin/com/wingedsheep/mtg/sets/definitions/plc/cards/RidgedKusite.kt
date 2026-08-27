package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ridged Kusite
 * {B}
 * Creature — Horror Spellshaper
 * 1/1
 * {1}{B}, {T}, Discard a card: Target creature gets +1/+0 and gains first strike until end of turn.
 */
val RidgedKusite = card("Ridged Kusite") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror Spellshaper"
    power = 1
    toughness = 1
    oracleText = "{1}{B}, {T}, Discard a card: Target creature gets +1/+0 and gains first strike until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap, Costs.DiscardCard)
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, t) then Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
        description = "{1}{B}, {T}, Discard a card: Target creature gets +1/+0 and gains first strike until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "rk post"
        flavorText = "\"It offers but a taste of the power that the shadows have to offer. But even that is a heady wine indeed.\"\n—Ratadrabik of Urborg"
        imageUri = "https://cards.scryfall.io/normal/front/5/1/51b27919-6b36-49f2-a6df-d63e5db6de0b.jpg"
    }
}
