package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ravenous Daggertooth
 * {2}{G}
 * Creature — Dinosaur
 * 3/2
 *
 * Enrage — Whenever this creature is dealt damage, you gain 2 life.
 */
val RavenousDaggertooth = card("Ravenous Daggertooth") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, you gain 2 life."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.GainLife(2)
        description = "Enrage — Whenever this creature is dealt damage, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "202"
        artist = "Jakub Kasper"
        flavorText = "A daggertooth's triumphant roar makes all the sounds of jungle life fall silent."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/309b204b-774a-47bc-aead-c78db90f3be2.jpg"
    }
}
