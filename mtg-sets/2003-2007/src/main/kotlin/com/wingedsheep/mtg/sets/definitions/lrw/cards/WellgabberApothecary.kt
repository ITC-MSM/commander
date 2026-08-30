package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wellgabber Apothecary
 * {4}{W}
 * Creature — Merfolk Cleric
 * 2/3
 * {1}{W}: Prevent all damage that would be dealt to target tapped Merfolk or Kithkin creature this turn.
 *
 * "Tapped Merfolk or Kithkin creature" is one noun phrase, so it is one filter — `tapped()` plus a
 * [GameObjectFilter.withAnySubtype] union — not a [TargetFilter.or] of two clauses, which is for
 * targets that span different zones. Every [PreventDamageEffect] field stays at its default: a null
 * `amount` is "prevent all", the scope covers non-combat damage, and the duration is "this turn".
 * The ability has no tap symbol, so it can be activated repeatedly.
 */
val WellgabberApothecary = card("Wellgabber Apothecary") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Merfolk Cleric"
    power = 2
    toughness = 3
    oracleText = "{1}{W}: Prevent all damage that would be dealt to target tapped Merfolk or Kithkin creature this turn."

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        val t = target(
            "target tapped Merfolk or Kithkin creature",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature
                        .tapped()
                        .withAnySubtype(Subtype.MERFOLK.value, Subtype.KITHKIN.value)
                )
            )
        )
        effect = PreventDamageEffect(target = t)
        description = "Prevent all damage that would be dealt to target tapped Merfolk or Kithkin creature this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Brandon Dorman"
        flavorText = "\"You've discovered that boggarts bite, I see. And will the militia be chasing this lot? . . . Ah, you're staying in town to avoid Nath's hunt. Wise. Now this poultice . . .\""
        imageUri = "https://cards.scryfall.io/normal/front/1/2/129933ba-60a0-4764-a705-28fa7bb10bd3.jpg?1783942907"
    }
}
