package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dusk Legion Zealot
 * {1}{B}
 * Creature — Vampire Soldier
 * 1/1
 * When this creature enters, you draw a card and you lose 1 life.
 */
val DuskLegionZealot = card("Dusk Legion Zealot") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "When this creature enters, you draw a card and you lose 1 life."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1) then Effects.LoseLife(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Winona Nelson"
        flavorText = "Once they reached Orazca, the Legion's explorers ransacked tombs and " +
            "temples, hunting for the Immortal Sun."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/3190cea3-fbea-464f-999b-4b4473be745e.jpg?1783935312"
    }
}
