package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Soothing of Sméagol
 * {1}{U}
 * Instant
 *
 * Return target nontoken creature to its owner's hand. The Ring tempts you.
 */
val SoothingOfSmeagol = card("Soothing of Sméagol") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target nontoken creature to its owner's hand. The Ring tempts you."

    spell {
        val creature = target(
            "nontoken creature",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.nontoken()))
        )
        effect = Effects.ReturnToHand(creature)
            .then(Effects.TheRingTemptsYou())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Lixin Yin"
        flavorText = "Frodo's heart sank. This was too much like trickery, and certainly what he did would seem a treachery to the poor treacherous creature."
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be6400e8-7f99-4005-9a92-ffbc359d3871.jpg?1686968300"
    }
}
