package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Court Cleric
 * {W}
 * Creature — Human Cleric
 * 1/1
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 * This creature gets +1/+1 as long as you control an Ajani planeswalker.
 *
 * "An Ajani planeswalker" is a planeswalker whose *subtype* is Ajani (CR 205.3j), so the gate is an
 * [Exists] over `GameObjectFilter.Planeswalker.withSubtype("Ajani")` — it sees every Ajani card, and
 * any other permanent that has been given the subtype, rather than a fixed list of card names.
 * The bonus is a layer-7c [ModifyStats] recomputed at projection, so it appears and disappears with
 * the planeswalker.
 */
val CourtCleric = card("Court Cleric") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)\n" +
        "This creature gets +1/+1 as long as you control an Ajani planeswalker."

    keywords(Keyword.LIFELINK)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 1, GroupFilter.source()),
            condition = Exists(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Planeswalker.withSubtype("Ajani")
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "283"
        artist = "Mark Behm"
        flavorText = "\"The healers of Bant are second to none. I owe them a great deal for their tutelage.\"\n" +
            "—Ajani Goldmane"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dc2e1a93-4b5c-4f89-9e11-1693dee64b63.jpg"
    }
}
