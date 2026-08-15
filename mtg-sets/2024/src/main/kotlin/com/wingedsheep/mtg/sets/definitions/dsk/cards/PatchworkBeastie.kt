package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.CantBlockUnless
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Patchwork Beastie
 * {G}
 * Artifact Creature — Beast
 * 3/3
 * Delirium — This creature can't attack or block unless there are four or more card types
 * among cards in your graveyard.
 * At the beginning of your upkeep, you may mill a card. (You may put the top card of your
 * library into your graveyard.)
 */
val PatchworkBeastie = card("Patchwork Beastie") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Beast"
    power = 3
    toughness = 3
    oracleText = "Delirium — This creature can't attack or block unless there are four or more " +
        "card types among cards in your graveyard.\n" +
        "At the beginning of your upkeep, you may mill a card. (You may put the top card of your " +
        "library into your graveyard.)"

    // Delirium — can't attack or block unless four+ card types in your graveyard.
    staticAbility {
        ability = CantAttackUnless(Conditions.Delirium())
    }
    staticAbility {
        ability = CantBlockUnless(Conditions.Delirium())
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = MayEffect(
            effect = Patterns.Library.mill(1),
            descriptionOverride = "You may mill a card.",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "John Tedrick"
        flavorText = "It doesn't know it isn't real."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/895de583-36d2-43fd-927f-8876d4302c73.jpg?1726286592"
    }
}
