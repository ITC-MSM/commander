package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kithkin Healer
 * {2}{W}
 * Creature — Kithkin Cleric
 * 2/2
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 */
val KithkinHealer = card("Kithkin Healer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Cleric"
    power = 2
    toughness = 2
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to any target this turn."

    activatedAbility {
        cost = Costs.Tap
        val recipient = target("any target", Targets.Any)
        effect = Effects.PreventNextDamage(1, recipient)
        description = "{T}: Prevent the next 1 damage that would be dealt to any target this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Rebecca Guay"
        flavorText = "The empathetic nature of the thoughtweft allows kithkin healers to treat the cause of an illness rather than fight its symptoms."
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44032574-a5bc-4366-af65-9824fd4302a2.jpg?1783942912"
    }
}
