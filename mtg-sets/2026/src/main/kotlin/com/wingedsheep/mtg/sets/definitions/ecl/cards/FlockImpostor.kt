package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Flock Impostor
 * {2}{W}
 * Creature — Shapeshifter
 * 2/2
 *
 * Changeling (This card is every creature type.)
 * Flash
 * Flying
 * When this creature enters, return up to one other target creature you control to its owner's hand.
 */
val FlockImpostor = card("Flock Impostor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shapeshifter"
    power = 2
    toughness = 2
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Flash\n" +
        "Flying\n" +
        "When this creature enters, return up to one other target creature you control to its owner's hand."

    keywords(Keyword.CHANGELING, Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "up to one other target creature you control",
            TargetCreature(optional = true, filter = TargetFilter.OtherCreatureYouControl)
        )
        effect = Effects.ReturnToHand(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Ilse Gort"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d32d0336-5140-41f9-bc67-f3d743b9231d.jpg?1767658337"
    }
}
