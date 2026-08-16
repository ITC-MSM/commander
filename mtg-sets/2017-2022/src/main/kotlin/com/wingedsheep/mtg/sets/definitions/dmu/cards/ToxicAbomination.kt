package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Toxic Abomination
 * {1}{B}
 * Creature — Phyrexian Zombie
 * 3/2
 * When this creature enters, you lose 2 life.
 */
val ToxicAbomination = card("Toxic Abomination") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Zombie"
    oracleText = "When this creature enters, you lose 2 life."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.LoseLife(2, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Igor Kieryluk"
        flavorText = "Deep in the forgotten reaches of Urborg, ancient Phyrexian monstrosities shamble endlessly, putrid ichor leaking from their rusted forms."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f435329e-6de7-4b05-ba70-cb63d121116e.jpg?1783921324"
    }
}
