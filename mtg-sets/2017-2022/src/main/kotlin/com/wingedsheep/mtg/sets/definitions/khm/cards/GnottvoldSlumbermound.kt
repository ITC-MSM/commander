package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Gnottvold Slumbermound
 * Land
 * This land enters tapped.
 * {T}: Add {R}.
 * {3}{R}{G}{G}, {T}, Sacrifice this land: Destroy target land. Create a 4/4 green Troll Warrior creature token with trample.
 *
 * The Gruul realm land: a Stone Rain stapled to a 4/4 trampler. The land sacrifices itself as part
 * of the cost, so the target land is destroyed by an ability whose source has already left the
 * battlefield.
 */
val GnottvoldSlumbermound = card("Gnottvold Slumbermound") {
    manaCost = ""
    colorIdentity = "GR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {R}.\n" +
        "{3}{R}{G}{G}, {T}, Sacrifice this land: Destroy target land. Create a 4/4 green Troll Warrior creature token with trample."

    replacementEffect(EntersTapped())

    // {T}: Add {R}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED, 1)
        manaAbility = true
    }

    // {3}{R}{G}{G}, {T}, Sacrifice this land: Destroy target land. Create a 4/4 green Troll
    // Warrior creature token with trample.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{R}{G}{G}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        val victim = target("target land", Targets.Land)
        effect = Effects.Composite(
            Effects.Destroy(victim),
            Effects.CreateToken(
                power = 4,
                toughness = 4,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Troll", "Warrior"),
                keywords = setOf(Keyword.TRAMPLE)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "258"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/735470df-65d8-43e9-837e-4869c8e4f052.jpg"
    }
}
