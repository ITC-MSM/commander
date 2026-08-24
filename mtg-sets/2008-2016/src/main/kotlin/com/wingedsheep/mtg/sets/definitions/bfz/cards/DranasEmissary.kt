package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Drana's Emissary
 * {1}{W}{B}
 * Creature — Vampire Cleric Ally
 * 2/2
 * Flying
 * At the beginning of your upkeep, each opponent loses 1 life and you gain 1 life.
 */
val DranasEmissary = card("Drana's Emissary") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Vampire Cleric Ally"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "At the beginning of your upkeep, each opponent loses 1 life and you gain 1 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Karl Kopinski"
        flavorText = "\"The taste of freedom is sweeter than blood.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99e82d47-9bbb-4bf9-a935-2c0b27b64a84.jpg?1783938180"
    }
}
