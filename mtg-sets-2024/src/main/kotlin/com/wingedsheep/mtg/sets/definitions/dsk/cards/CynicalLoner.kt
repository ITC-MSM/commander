package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Cynical Loner
 * {1}{B}
 * Creature — Human Survivor
 * 3/1
 *
 * This creature can't be blocked by Glimmers.
 * Survival — At the beginning of your second main phase, if this creature is tapped, you may
 * search your library for a card, put it into your graveyard, then shuffle.
 *
 * "Survival" is an ability word (no rules meaning) — modeled as a postcombat-main-phase trigger
 * ([Triggers.YourPostcombatMain]) with an intervening-if ([Conditions.SourceIsTapped], CR 603.4 —
 * checked both when it would trigger and on resolution). The search is "for a card" (any card,
 * [GameObjectFilter.Any]) to the graveyard, and is optional ("you may"), so it's wrapped in a
 * [MayEffect]; when you don't search, no shuffle happens. The unblockable-by clause is the unified
 * [CantBeBlockedBy] static over the Glimmer subtype.
 */
val CynicalLoner = card("Cynical Loner") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Survivor"
    power = 3
    toughness = 1
    oracleText = "This creature can't be blocked by Glimmers.\n" +
        "Survival — At the beginning of your second main phase, if this creature is tapped, you " +
        "may search your library for a card, put it into your graveyard, then shuffle."

    staticAbility {
        ability = CantBeBlockedBy(
            blockerFilter = GameObjectFilter.Creature.withSubtype("Glimmer")
        )
    }

    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        effect = MayEffect(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any,
                destination = SearchDestination.GRAVEYARD,
                shuffleAfter = true,
            )
        )
        description = "Survival — At the beginning of your second main phase, if this creature is " +
            "tapped, you may search your library for a card, put it into your graveyard, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Miranda Meeks"
        flavorText = "\"Thanks, but no thanks. I'm done with hope.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc93bcb8-778d-491e-877b-e6ad432764cb.jpg?1726286181"
    }
}
