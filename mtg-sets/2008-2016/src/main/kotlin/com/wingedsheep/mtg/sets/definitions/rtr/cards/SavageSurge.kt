package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Savage Surge
 * {1}{G}
 * Instant
 *
 * Target creature gets +2/+2 until end of turn. Untap that creature.
 */
val SavageSurge = card("Savage Surge") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. Untap that creature."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            Effects.Untap(t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Svetlin Velinov"
        flavorText = "Gruul warriors never need to be stirred to battle. They need only to be shown where the battle is."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fa74aae-e857-410c-8836-953c8623d0b0.jpg"
    }
}
