package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Kykar, Wind's Fury
 * {1}{U}{R}{W}
 * Legendary Creature — Bird Wizard
 * 3/3
 *
 * Flying
 * Whenever you cast a noncreature spell, create a 1/1 white Spirit creature token with flying.
 * Sacrifice a Spirit: Add {R}.
 *
 * "Sacrifice a Spirit" is any Spirit *permanent* you control, not only a Spirit creature —
 * hence [GameObjectFilter.Permanent] on the sacrifice cost.
 */
val KykarWindsFury = card("Kykar, Wind's Fury") {
    manaCost = "{1}{U}{R}{W}"
    colorIdentity = "RUW"
    typeLine = "Legendary Creature — Bird Wizard"
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, create a 1/1 white Spirit creature token with flying.\n" +
        "Sacrifice a Spirit: Add {R}."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT))
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "212"
        artist = "G-host Lee"
        flavorText = "\"The raging gale fans the flames of righteous wrath.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/9/594cb7dc-ea88-4909-ab40-1d40fecc9817.jpg?1783932950"
    }
}
