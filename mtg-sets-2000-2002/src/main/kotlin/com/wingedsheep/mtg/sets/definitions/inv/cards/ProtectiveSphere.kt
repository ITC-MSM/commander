package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Protective Sphere
 * {2}{W}
 * Enchantment
 * {1}, Pay 1 life: Prevent all damage that would be dealt to you this turn by a source of
 * your choice that shares a color with the mana spent on this activation cost. (Colorless mana
 * prevents no damage.)
 *
 * Modeling note: rather than tracking the color of the single generic mana spent on the cost,
 * the "shares a color with the mana spent" restriction is captured by only offering colored
 * sources for the choice (`PreventionSourceFilter.ChosenColoredSource`). A colorless source
 * shares a color with no mana, so it can never be chosen — which is exactly what the reminder
 * text means by "colorless mana prevents no damage." The player picks the colored source they
 * want to stop (paying mana of a matching color, as the rules require).
 */
val ProtectiveSphere = card("Protective Sphere") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "{1}, Pay 1 life: Prevent all damage that would be dealt to you this turn by a " +
        "source of your choice that shares a color with the mana spent on this activation cost. " +
        "(Colorless mana prevents no damage.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.PayLife(1))
        effect = Effects.PreventAllDamageFromChosenColoredSource()
        description = "{1}, Pay 1 life: Prevent all damage that would be dealt to you this turn by " +
            "a source of your choice that shares a color with the mana spent on this activation cost."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef5ef13e-1cf0-42a9-95d0-30ade254d6a8.jpg?1562943080"
    }
}
