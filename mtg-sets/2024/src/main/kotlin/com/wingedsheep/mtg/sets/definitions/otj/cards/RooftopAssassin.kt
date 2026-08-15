package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rooftop Assassin
 * {3}{B}
 * Creature — Vampire Assassin
 * 2/2
 * Flash
 * Flying, lifelink
 * When this creature enters, destroy target creature an opponent controls that was dealt
 * damage this turn.
 *
 * The target is constrained to creatures an opponent controls that already took damage this
 * turn via [StatePredicate.WasDealtDamageThisTurn]. If no such creature exists, the trigger
 * has no legal target and is removed from the stack.
 */
val RooftopAssassin = card("Rooftop Assassin") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Assassin"
    power = 2
    toughness = 2
    oracleText = "Flash\nFlying, lifelink\n" +
        "When this creature enters, destroy target creature an opponent controls that was dealt damage this turn."

    keywords(Keyword.FLASH, Keyword.FLYING, Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetCreature(
                filter = TargetFilter.Creature.dealtDamageThisTurn().opponentControls()
            )
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Josu Hernaiz"
        flavorText = "\"Sneaking behind people? Pedestrian.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4e4311a-8583-4cf0-8126-508dbfbcddb6.jpg?1712355656"
    }
}
