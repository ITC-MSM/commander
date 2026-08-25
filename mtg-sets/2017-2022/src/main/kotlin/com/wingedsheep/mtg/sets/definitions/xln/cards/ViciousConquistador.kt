package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vicious Conquistador
 * {B}
 * Creature — Vampire Soldier
 * 1/2
 *
 * Whenever this creature attacks, each opponent loses 1 life.
 */
val ViciousConquistador = card("Vicious Conquistador") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "Whenever this creature attacks, each opponent loses 1 life."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "128"
        artist = "Kieran Yanner"
        flavorText = "\"He is ambitious. Tireless. And utterly ruthless. Ideal for the frontier.\"\n—Viceroy Elia Sotonores, report to the queen"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a4b6ced-e8d3-47e9-bd27-3e0cb644afe4.jpg"
    }
}
