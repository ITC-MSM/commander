package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
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
 * Foundry Screecher
 * {2}{B}
 * Creature — Bat
 * 2 / 1
 *
 * Flying
 * This creature gets +1/+0 as long as you control an artifact.
 *
 * The bonus is a [ConditionalStaticAbility] over [Filters.Self], not a one-shot pump: the [Exists]
 * condition is re-read continuously in Layer 7c, so the Screecher shrinks back to 2/1 the moment
 * the last artifact leaves.
 */
val FoundryScreecher = card("Foundry Screecher") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bat"
    oracleText = "Flying\n" +
        "This creature gets +1/+0 as long as you control an artifact."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 0, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Dan Murayama Scott"
        flavorText = "\"My creations still can't match the dive speed of those floppy-winged mammals. Maybe I should enter one of them in the Fair.\"\n—Viprikti, thopterist"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4313802f-b969-47d0-b4aa-b049df0755c0.jpg?1783937207"
    }
}
