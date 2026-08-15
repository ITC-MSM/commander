package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Scalestorm Summoner
 * {2}{R}
 * Creature — Human Warlock
 * 3/3
 *
 * Whenever this creature attacks, create a 3/1 red Dinosaur creature token if you control a
 * creature with power 4 or greater.
 */
val ScalestormSummoner = card("Scalestorm Summoner") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warlock"
    power = 3
    toughness = 3
    oracleText = "Whenever this creature attacks, create a 3/1 red Dinosaur creature token if you " +
        "control a creature with power 4 or greater."

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        effect = Effects.CreateToken(
            power = 3,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Dinosaur"),
            imageUri = "https://cards.scryfall.io/normal/front/c/7/c75c01ea-427d-4401-b5b0-166e87c4585e.jpg?1712316260"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Xabi Gaztelua"
        flavorText = "The herd followed him through an Omenpath and found their curiosity rewarded with an untamed world rich with unsuspecting prey."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d603c00f-048a-4a05-9df9-52844819d523.jpg?1712355842"
    }
}
