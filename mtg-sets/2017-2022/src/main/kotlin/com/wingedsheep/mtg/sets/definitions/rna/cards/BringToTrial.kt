package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Bring to Trial
 * {2}{W}
 * Sorcery
 * Exile target creature with power 4 or greater.
 */
val BringToTrial = card("Bring to Trial") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Exile target creature with power 4 or greater."

    spell {
        val t = target(
            "creature with power 4 or greater",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(4)))
        )
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Victor Adame Minguez"
        flavorText = "\"In you go, big guy. Watch your head.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63d566fc-0936-4035-96fd-f8b0c4eadbf5.jpg?1783933724"
    }
}
