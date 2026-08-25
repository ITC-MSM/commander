package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Regisaur Alpha
 * {3}{R}{G}
 * Creature — Dinosaur
 * 4/4
 *
 * Other Dinosaurs you control have haste.
 * When this creature enters, create a 3/3 green Dinosaur creature token with trample.
 */
val RegisaurAlpha = card("Regisaur Alpha") {
    manaCost = "{3}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Dinosaur"
    oracleText = "Other Dinosaurs you control have haste.\n" +
        "When this creature enters, create a 3/3 green Dinosaur creature token with trample."
    power = 4
    toughness = 4

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.HASTE,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype("Dinosaur").youControl(),
                excludeSelf = true
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Dinosaur"),
            keywords = setOf(Keyword.TRAMPLE),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "227"
        artist = "Jonathan Kuo"
        flavorText = "\"Seeing a pack of these monsters hunt together, I'm at a loss to imagine the size of their prey.\"\n—Adrian Adanto of Lujio"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6a322c5-aa4c-4a99-a3ca-48c1353104f0.jpg"
    }
}
