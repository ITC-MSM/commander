package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Elvish Promenade
 * {3}{G}
 * Kindred Sorcery — Elf
 * Create a 1/1 green Elf Warrior creature token for each Elf you control.
 *
 * The count is an `AggregateBattlefield` tally over your own battlefield, evaluated on resolution —
 * so the Elves it counts are the ones there when the spell resolves, and the tokens it makes are
 * not among them.
 *
 * Ruling (2024-06-07): the card was printed as "Tribal"; that type is now spelled "Kindred" with no
 * change in function.
 */
val ElvishPromenade = card("Elvish Promenade") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Kindred Sorcery — Elf"
    oracleText = "Create a 1/1 green Elf Warrior creature token for each Elf you control."

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Permanent.withSubtype(Subtype.ELF),
            ),
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/2/7/27b171ac-b2ef-4a80-92d1-6d9e71f3e3ca.jpg?1783942838",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Steve Ellis"
        flavorText = "The faultless and immaculate castes form the lower tiers of elvish society, with the exquisite caste above them. At the pinnacle is the perfect, a consummate blend of aristocrat and predator."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec9d5049-c1a6-4186-952e-bd7481b1974a.jpg?1783942865"
        ruling("2024-06-07", "This cards was originally printed with the \"tribal\" card type. That card type has been replaced with \"kindred\". This change does not affect the gameplay function of this card.")
    }
}
