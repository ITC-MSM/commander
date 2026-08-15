package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CantBeRegeneratedEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Annihilate
 * {3}{B}{B}
 * Instant
 *
 * Destroy target nonblack creature. It can't be regenerated.
 * Draw a card.
 */
val Annihilate = card("Annihilate") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target nonblack creature. It can't be regenerated.\nDraw a card."

    spell {
        val creature = target(
            "target nonblack creature",
            TargetCreature(filter = TargetFilter.Creature.notColor(Color.BLACK))
        )
        effect = CantBeRegeneratedEffect(creature) then
            Effects.Destroy(creature) then
            Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "94"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a3bf039-ecf6-477e-997c-e32c55323c01.jpg?1562909994"
    }
}
