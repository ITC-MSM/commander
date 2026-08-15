package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Skullsnap Nuisance
 * {U}{B}
 * Creature — Insect Skeleton
 * 1/4
 * Flying
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a
 * Room, surveil 1.
 */
val SkullsnapNuisance = card("Skullsnap Nuisance") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Insect Skeleton"
    power = 1
    toughness = 4
    oracleText = "Flying\nEerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"

    keywords(Keyword.FLYING, Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Library.surveil(1)
        description = "Eerie — Whenever an enchantment you control enters, surveil 1."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        effect = Patterns.Library.surveil(1)
        description = "Eerie — Whenever you fully unlock a Room, surveil 1."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Allen Douglas"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fdcdfd0-8c66-4767-a894-58cf0c6d7e07.jpg?1726286742"
    }
}
