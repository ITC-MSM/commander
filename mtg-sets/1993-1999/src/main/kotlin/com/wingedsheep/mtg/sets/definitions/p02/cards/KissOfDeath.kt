package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kiss of Death
 * {4}{B}{B}
 * Sorcery
 * Kiss of Death deals 4 damage to target opponent or planeswalker. You gain 4 life.
 *
 * "Target opponent or planeswalker" is the single [Targets.OpponentOrPlaneswalker] requirement, not a
 * pair; the life gain is unconditional and defaults to the controller.
 */
val KissOfDeath = card("Kiss of Death") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Kiss of Death deals 4 damage to target opponent or planeswalker. You gain 4 life."

    spell {
        val victim = target("target", Targets.OpponentOrPlaneswalker)
        effect = Effects.Composite(
            Effects.DealDamage(4, victim),
            Effects.GainLife(4)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Melissa A. Benson"
        flavorText = "\"I'd sooner lock lips with a viper. At least I might walk away from *that*.\"\n—Elvish scout"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf6832c1-a0a9-49ec-a787-879e510aee08.jpg"
    }
}
