package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Feed the Serpent
 * {2}{B}{B}
 * Instant
 * Exile target creature or planeswalker.
 *
 * Unconditional exile at instant speed — no "destroy", so indestructible and regeneration are both
 * irrelevant, and the permanent leaves without a dies trigger.
 */
val FeedTheSerpent = card("Feed the Serpent") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature or planeswalker."

    spell {
        val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.Exile(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Nicholas Gregory"
        flavorText = "He spent the final moments of his existence tumbling down the length of the serpent's jaws, driven mad by the magnitude of the Cosmos."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99a1a75b-20cf-4db9-a244-cc54411446c4.jpg"
    }
}
