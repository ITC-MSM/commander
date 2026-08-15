package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Linden, the Steadfast Queen
 * {W}{W}{W}
 * Legendary Creature — Human Noble
 * 3/3
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * Whenever a white creature you control attacks, you gain 1 life.
 *
 * Canonical printing lives in Throne of Eldraine (the earliest real-expansion printing); the
 * Foundations release is a [com.wingedsheep.sdk.model.Printing] row (see
 * `mtg-sets/.../definitions/fdn/cards/LindenTheSteadfastQueenReprint.kt`).
 *
 * The attack trigger fires once per qualifying attacker via [TriggerBinding.ANY] over a white
 * creature you control — Linden is herself a white creature, so she counts when she attacks.
 */
val LindenTheSteadfastQueen = card("Linden, the Steadfast Queen") {
    manaCost = "{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Noble"
    power = 3
    toughness = 3
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
        "Whenever a white creature you control attacks, you gain 1 life."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.withColor(Color.WHITE).youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GainLife(1)
        description = "Whenever a white creature you control attacks, you gain 1 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Ryan Pancoast"
        flavorText = "\"Until my last breath, I will defend the realm.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa3ab467-be97-4b84-a73d-b03484d06b97.jpg?1782707922"
    }
}
