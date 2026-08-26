package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Halo Hunter
 * {2}{B}{B}{B}
 * Creature — Demon
 * 6/3
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)
 * When this creature enters, destroy target Angel.
 *
 * "Target Angel" is a bare tribal noun, so the filter is a *permanent* with the subtype — an
 * Angel that isn't currently a creature is still a legal target.
 */
val HaloHunter = card("Halo Hunter") {
    manaCost = "{2}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 3
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)\n" +
        "When this creature enters, destroy target Angel."

    keywords(Keyword.INTIMIDATE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val angel = target("Angel", TargetPermanent(filter = TargetFilter.Permanent.withSubtype("Angel")))
        effect = Effects.Destroy(angel)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "96"
        artist = "Chris Rahn"
        flavorText = "Hanging on the walls of his lair, the fallen halos cast his depravity in everlasting light."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f96344c-4b1f-42a6-bbf2-dc5cf11cdb02.jpg"
    }
}
