package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mob
 * {4}{B}
 * Instant
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Destroy target creature.
 *
 * Devouring Light's convoke, on the plainest possible body: [Keyword.CONVOKE] is read by the cast
 * enumerator when the total cost is calculated, so the reminder line needs no script of its own.
 */
val Mob = card("Mob") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Destroy target creature."

    keywords(Keyword.CONVOKE)

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Sidharth Chaturvedi"
        flavorText = "Not all monsters fight with teeth and claws."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c216e13-3779-4734-b481-9aad7aba9925.jpg?1783933124"
    }
}
