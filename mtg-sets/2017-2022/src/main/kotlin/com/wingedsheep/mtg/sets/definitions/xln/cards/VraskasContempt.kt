package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vraska's Contempt
 * {2}{B}{B}
 * Instant
 *
 * Exile target creature or planeswalker. You gain 2 life.
 */
val VraskasContempt = card("Vraska's Contempt") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature or planeswalker. You gain 2 life."

    spell {
        val victim = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.Move(victim, Zone.EXILE) then Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "129"
        artist = "Clint Cearley"
        flavorText = "It wasn't long before the taverns of High and Dry were full of whispers about the new captain who could turn a person to stone with a glance."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd7550dd-dfc5-43cd-a117-1244ee3086a8.jpg"
    }
}
