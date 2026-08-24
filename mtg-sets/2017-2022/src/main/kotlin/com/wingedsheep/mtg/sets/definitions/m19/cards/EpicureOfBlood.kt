package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Epicure of Blood
 * {4}{B}
 * Creature — Vampire
 * 4/4
 * Whenever you gain life, each opponent loses 1 life.
 */
val EpicureOfBlood = card("Epicure of Blood") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 4
    toughness = 4
    oracleText = "Whenever you gain life, each opponent loses 1 life."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Anna Steinbauer"
        flavorText = "\"Fleshy, with just a hint of leather. A fine vintage.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40b03528-f4ec-4825-ba4c-c485cb4eab3a.jpg"
    }
}
