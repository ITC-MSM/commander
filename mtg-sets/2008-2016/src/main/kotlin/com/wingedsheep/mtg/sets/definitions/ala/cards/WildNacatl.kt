package com.wingedsheep.mtg.sets.definitions.ala.cards

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
 * Wild Nacatl
 * {G}
 * Creature — Cat Warrior
 * 1 / 1
 * This creature gets +1/+1 as long as you control a Mountain.
 * This creature gets +1/+1 as long as you control a Plains.
 *
 * Two printed lines, so **two** separate [ConditionalStaticAbility] entries rather than one folded
 * +2/+2 — each buff has its own land condition and either can be live on its own. Both wrap the same
 * [ModifyStats] over [Filters.Self] (the source-scoped group filter), gated by
 * [Exists] on [Player.You]'s battlefield for a land with the named basic subtype. Being statics they
 * re-evaluate through the layer projection every time the board changes, so the pump appears and
 * disappears with the land.
 */
val WildNacatl = card("Wild Nacatl") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Warrior"
    power = 1
    toughness = 1
    oracleText = "This creature gets +1/+1 as long as you control a Mountain.\n" +
        "This creature gets +1/+1 as long as you control a Plains."

    // +1/+1 as long as you control a Mountain
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 1, toughnessBonus = 1, filter = Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain"))
        )
    }

    // +1/+1 as long as you control a Plains
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 1, toughnessBonus = 1, filter = Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Wayne Reynolds"
        flavorText = "\"The Cloud Nacatl sit and think, a bunch of soft paws. We are the Claws of Marisi, stalking, pouncing, drawing blood.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5eb18038-eb6a-4d27-ba52-52a1cee6b512.jpg"
    }
}
