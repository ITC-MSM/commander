package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Chasm Guide
 * {3}{R}
 * Creature — Goblin Scout Ally
 * 3/2
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain haste until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val ChasmGuide = card("Chasm Guide") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Scout Ally"
    power = 3
    toughness = 2
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "haste until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.HASTE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "143"
        artist = "Johannes Voss"
        flavorText = "With a single act of bravery, she went from expendable to indispensable."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbd0fad7-264d-40fc-a616-a88dc94fa4d7.jpg?1783938195"
    }
}
