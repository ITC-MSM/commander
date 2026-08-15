package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Exorcise
 * {1}{W}
 * Sorcery
 *
 * Exile target artifact, enchantment, or creature with power 4 or greater.
 *
 * The "power 4 or greater" clause binds only to the creature branch (it's "artifact,
 * enchantment, or [creature with power 4+]"), so an artifact or enchantment with any
 * power is a legal target — modelled as a three-way OR where only the creature branch
 * carries the [powerAtLeast] predicate.
 */
val Exorcise = card("Exorcise") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Exile target artifact, enchantment, or creature with power 4 or greater."

    spell {
        val permanent = target(
            "target artifact, enchantment, or creature with power 4 or greater",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Artifact or
                        GameObjectFilter.Enchantment or
                        GameObjectFilter.Creature.powerAtLeast(4)
                )
            )
        )
        effect = Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Dominik Mayer"
        flavorText = "\"Strictly speaking, you can't kill what's already dead, but trapping and disintegrating it is pretty close.\"\n—Garden, survivor technologist"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f49006f2-a097-417d-8eb0-b8016ff2e0d5.jpg?1726285887"
    }
}
