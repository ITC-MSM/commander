package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Eyeblight's Ending
 * {2}{B}
 * Kindred Instant — Elf
 * Destroy target non-Elf creature.
 */
val EyeblightsEnding = card("Eyeblight's Ending") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Kindred Instant — Elf"
    oracleText = "Destroy target non-Elf creature."

    spell {
        val creature = target(
            "target non-Elf creature",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.notSubtype(Subtype.ELF)))
        )
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Ron Spears"
        flavorText = "\"Those without beauty are Lorwyn's greatest tumor. The winnowers have an unpleasant duty, but a necessary one.\"\n—Eidren, perfect of Lys Alana"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0c08701-7038-4d6b-bbf8-056fd8ffb226.jpg?1783942892"
    }
}
