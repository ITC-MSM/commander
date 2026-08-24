package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Infectious Horror
 * {3}{B}
 * Creature — Zombie Horror
 * 2/2
 * Whenever this creature attacks, each opponent loses 2 life.
 */
val InfectiousHorror = card("Infectious Horror") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Horror"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature attacks, each opponent loses 2 life."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Pete Venters"
        flavorText = "Not once in the history of Grixis has anyone died of old age."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb460855-f09d-4460-9d3f-1bfcc7f3e626.jpg"
    }
}
