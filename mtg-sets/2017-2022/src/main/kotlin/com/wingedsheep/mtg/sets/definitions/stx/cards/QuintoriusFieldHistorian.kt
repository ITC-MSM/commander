package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Quintorius, Field Historian — Strixhaven: School of Mages #220 (canonical printing)
 * {3}{R}{W} · Legendary Creature — Elephant Cleric · 2/4
 *
 * Spirits you control get +1/+0.
 * Whenever one or more cards leave your graveyard, create a 3/2 red and white Spirit creature token.
 *
 * "Spirits you control" is a bare tribal noun, so the anthem is a static [ModifyStats] over
 * *permanents* with the Spirit subtype, not creatures. "One or more … leave" is CR 603.2c batch
 * wording, so [Triggers.CardsLeaveYourGraveyard] fires once per event batch however many cards
 * moved.
 */
val QuintoriusFieldHistorian = card("Quintorius, Field Historian") {
    manaCost = "{3}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Elephant Cleric"
    oracleText =
        "Spirits you control get +1/+0.\n" +
        "Whenever one or more cards leave your graveyard, create a 3/2 red and white Spirit creature token."
    power = 2
    toughness = 4

    staticAbility {
        ability = ModifyStats(1, 0, GroupFilter(GameObjectFilter.Permanent.withSubtype("Spirit").youControl()))
    }

    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard()
        effect = Effects.CreateToken(
            power = 3,
            toughness = 2,
            colors = setOf(Color.RED, Color.WHITE),
            creatureTypes = setOf("Spirit")
        )
        description = "Whenever one or more cards leave your graveyard, create a 3/2 red and white Spirit creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Bryan Sola"
        flavorText = "\"Every life has a story to tell. I won't let them be forgotten.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06c55ef1-8e62-4d43-bd4c-b2c3c5203338.jpg?1783927298"
    }
}
