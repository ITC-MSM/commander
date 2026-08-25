package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Excommunicate
 * {2}{W}
 * Sorcery
 * Put target creature on top of its owner's library.
 *
 * Time Ebb in white: one [TargetCreature] and [Effects.PutOnTopOfLibrary], the forced (no player
 * choice) top-of-library move. The destination is the *owner's* library, which the zone move
 * derives on its own — nothing on the effect needs to name the player.
 */
val Excommunicate = card("Excommunicate") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Put target creature on top of its owner's library."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.PutOnTopOfLibrary(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Matt Stewart"
        flavorText = "Rebels and other malcontents forced into the ritual awake lost in Topa's vast savannahs. Those who find their way back return humble and repentant."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6fe080bc-e642-4fce-b4ee-bbe25d735673.jpg"
    }
}
