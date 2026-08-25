package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Inventor's Apprentice
 * {R}
 * Creature — Human Artificer
 * 1/2
 * This creature gets +1/+1 as long as you control an artifact.
 *
 * A [ConditionalStaticAbility] over [Filters.Self], gated by an [Exists] check for an artifact
 * you control — re-evaluated continuously in Layer 7c, so the bonus comes and goes with the
 * artifact.
 */
val InventorsApprentice = card("Inventor's Apprentice") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Artificer"
    oracleText = "This creature gets +1/+1 as long as you control an artifact."
    power = 1
    toughness = 2

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 1, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Ryan Pancoast"
        flavorText = "\"Everyone starts off making garbage. If you finally make something halfway decent, it'll be the best day of your life.\"\n—Nehra, inventor"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f737109b-15fb-4b92-9007-99a33ae68628.jpg?1783937192"
    }
}
