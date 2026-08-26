package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pharika's Cure
 * {B}{B}
 * Instant
 *
 * Pharika's Cure deals 2 damage to target creature and you gain 2 life.
 *
 * One sentence, two effects: the life gain is not conditional on the damage, so it is a plain
 * composite rather than a gated rider.
 */
val PharikasCure = card("Pharika's Cure") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Pharika's Cure deals 2 damage to target creature and you gain 2 life."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(2, creature),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Igor Kieryluk"
        flavorText = "\"The venom cleanses the sickness from your body, but it will not be pleasant, and you may not survive. Pharika's blessings are fickle.\"\n—Solon, acolyte of Pharika"
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0efc963-5848-44eb-a654-c08b5bd4501d.jpg"
    }
}
