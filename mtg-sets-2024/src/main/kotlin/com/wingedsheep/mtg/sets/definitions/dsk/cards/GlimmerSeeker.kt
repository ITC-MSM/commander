package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Glimmer Seeker
 * {2}{W}
 * Creature — Human Survivor
 * 3/3
 *
 * Survival — At the beginning of your second main phase, if this creature is tapped, draw a card
 * if you control a Glimmer creature. If you don't control a Glimmer creature, create a 1/1 white
 * Glimmer enchantment creature token.
 */
val GlimmerSeeker = card("Glimmer Seeker") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Survivor"
    power = 3
    toughness = 3
    oracleText = "Survival — At the beginning of your second main phase, if this creature is tapped, " +
        "draw a card if you control a Glimmer creature. If you don't control a Glimmer creature, " +
        "create a 1/1 white Glimmer enchantment creature token."

    // Survival — intervening-if "this creature is tapped" at the postcombat main beginning.
    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        effect = ConditionalEffect(
            condition = Conditions.YouControl(GameObjectFilter.Creature.withSubtype("Glimmer")),
            effect = Effects.DrawCards(1),
            elseEffect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Glimmer"),
                enchantmentToken = true,
                imageUri = "https://cards.scryfall.io/normal/front/4/7/475c7449-2c95-4873-94de-68a5e06cdfb8.jpg?1754930946"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Kev Fang"
        flavorText = "\"Well, hello there. Want to come exploring?\""
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f06bbb1-0c7d-4803-9b35-8a2206803eed.jpg?1726285911"
    }
}
