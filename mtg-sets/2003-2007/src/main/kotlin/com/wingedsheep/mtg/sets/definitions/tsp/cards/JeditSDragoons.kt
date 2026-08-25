package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jedit's Dragoons
 * {5}{W}
 * Creature — Cat Soldier
 * 2 / 5
 * Vigilance
 * When this creature enters, you gain 4 life.
 */
val JeditSDragoons = card("Jedit's Dragoons") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Soldier"
    power = 2
    toughness = 5
    oracleText = "Vigilance\n" +
        "When this creature enters, you gain 4 life."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "John Matson"
        flavorText = "After Efrava was destroyed, the cat warriors scattered across Dominaria. Those who followed Jedit's example were strong enough to survive the ravages of apocalypse."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e29cc9e5-29b7-4e3c-a0cd-46265b0f74ac.jpg"
    }
}
