package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenCopyOfSourceEffect

/**
 * Stormsplitter {3}{R}
 * Creature — Otter Wizard
 * 1/4
 *
 * Haste
 * Whenever you cast an instant or sorcery spell, create a token that's a copy of
 * this creature. Exile that token at the beginning of the next end step.
 */
val Stormsplitter = card("Stormsplitter") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Otter Wizard"
    oracleText = "Haste\nWhenever you cast an instant or sorcery spell, create a token that's a copy of this creature. Exile that token at the beginning of the next end step."
    power = 1
    toughness = 4

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = CreateTokenCopyOfSourceEffect(exileAtStep = Step.END)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "154"
        artist = "Lius Lasahido"
        flavorText = "\"We'll cover more ground if I split up.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56f214d3-6b93-40db-a693-55e491c8a283.jpg?1721426711"
    }
}
