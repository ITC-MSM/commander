package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Darling of the Masses
 * {2}{G}{W}
 * Creature — Elf Citizen
 * 2 / 4
 * Other Citizens you control get +1/+0.
 * Whenever this creature attacks, create a 1/1 green and white Citizen creature token.
 *
 * "Other Citizens you control" is a bare tribal noun, so the anthem's filter is a *permanent*
 * filter with the subtype ([GameObjectFilter.Permanent] + `withSubtype`), not a creature filter,
 * with `excludeSelf` for "other".
 */
val DarlingOfTheMasses = card("Darling of the Masses") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Citizen"
    oracleText = "Other Citizens you control get +1/+0.\nWhenever this creature attacks, create a 1/1 green and white Citizen creature token."
    power = 2
    toughness = 4

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype("Citizen").youControl(),
                excludeSelf = true,
            ),
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf("Citizen"),
        )
        description = "Whenever this creature attacks, create a 1/1 green and white Citizen creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "181"
        artist = "Mila Pesic"
        flavorText = "The people flocked to the glittering parade, turning their backs on the threats in the shadows."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52aa9815-6b47-4c80-87c7-4277166ea0df.jpg?1783923087"
    }
}
