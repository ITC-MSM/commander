package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Blighted Gorge
 * Land
 * {T}: Add {C}.
 * {4}{R}, {T}, Sacrifice this land: It deals 2 damage to any target.
 */
val BlightedGorge = card("Blighted Gorge") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{4}{R}, {T}, Sacrifice this land: It deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{R}"), Costs.Tap, Costs.SacrificeSelf)
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "231"
        artist = "Jung Park"
        flavorText = "The land is dying, but it will not go peacefully."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88cbfe5d-79b7-45db-b3cd-4ae7a9964a37.jpg?1783938175"
    }
}
