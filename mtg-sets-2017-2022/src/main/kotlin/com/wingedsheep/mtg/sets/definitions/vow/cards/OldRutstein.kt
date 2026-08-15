package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Old Rutstein — Innistrad: Crimson Vow #244
 * {1}{B}{G} · Legendary Creature — Human Peasant · Rare · 1/4
 * Artist: Greg Staples
 *
 * When Old Rutstein enters and at the beginning of your upkeep, mill a card. If a land card is
 * milled this way, create a Treasure token. If a creature card is milled this way, create a 1/1
 * green Insect creature token. If a noncreature, nonland card is milled this way, create a Blood
 * token.
 *
 * "When … enters and at the beginning of your upkeep" is one ability on two events — modeled as two
 * [triggeredAbility] blocks sharing one hoisted effect ([Triggers.EntersBattlefield] +
 * [Triggers.YourUpkeep], the Obsessive Pursuit idiom). The effect mills one card into the "milled"
 * collection ([Patterns.Library.mill]) and then branches on that card's type with three independent
 * [ConditionalEffect] gates (Bonehoard Dracosaur shape). Because exactly one card is milled and the
 * three filters — land / creature / (noncreature ∧ nonland) — partition every card type, at most one
 * branch fires per resolution, matching the oracle's mutually-exclusive "If a … card is milled".
 */
val OldRutstein = card("Old Rutstein") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Human Peasant"
    power = 1
    toughness = 4
    oracleText = "When Old Rutstein enters and at the beginning of your upkeep, mill a card. If a " +
        "land card is milled this way, create a Treasure token. If a creature card is milled this " +
        "way, create a 1/1 green Insect creature token. If a noncreature, nonland card is milled " +
        "this way, create a Blood token."

    val millAndReact = Effects.Composite(
        // Mill a card into the "milled" collection (then to the graveyard).
        Patterns.Library.mill(1),
        // If a land card is milled this way, create a Treasure token.
        ConditionalEffect(
            condition = Conditions.CollectionContainsMatch("milled", GameObjectFilter.Land),
            effect = Effects.CreateTreasure()
        ),
        // If a creature card is milled this way, create a 1/1 green Insect creature token.
        ConditionalEffect(
            condition = Conditions.CollectionContainsMatch("milled", GameObjectFilter.Creature),
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Insect")
            )
        ),
        // If a noncreature, nonland card is milled this way, create a Blood token.
        ConditionalEffect(
            condition = Conditions.CollectionContainsMatch(
                "milled",
                GameObjectFilter.Noncreature and GameObjectFilter.Nonland
            ),
            effect = Effects.CreateBlood()
        )
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = millAndReact
        description = "When Old Rutstein enters, mill a card. If a land card is milled this way, " +
            "create a Treasure token. If a creature card is milled this way, create a 1/1 green " +
            "Insect creature token. If a noncreature, nonland card is milled this way, create a " +
            "Blood token."
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = millAndReact
        description = "At the beginning of your upkeep, mill a card. If a land card is milled this " +
            "way, create a Treasure token. If a creature card is milled this way, create a 1/1 " +
            "green Insect creature token. If a noncreature, nonland card is milled this way, " +
            "create a Blood token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "244"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/625b8023-2ef1-4b7b-9e48-4f774fee14e0.jpg?1783924790"
    }
}
