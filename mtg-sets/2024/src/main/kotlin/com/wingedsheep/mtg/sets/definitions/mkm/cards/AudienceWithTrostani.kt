package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Audience with Trostani — Murders at Karlov Manor #152
 * {2}{G} · Sorcery · Rare
 *
 * Create a 0/1 green Plant creature token, then draw cards equal to the number of differently
 * named creature tokens you control.
 *
 * The "then" is load-bearing: the Plant is created *before* the count is taken, so the card always
 * draws at least one — and exactly one when the Plant is the only token, or when you already
 * controlled other Plant tokens (a name already counted doesn't count twice).
 *
 * `distinctNames()` over `Creature.token()` is the whole count: [DynamicAmounts] resolves it
 * against the battlefield at resolution time, keying on each permanent's name, so two Plants are
 * one and a Plant plus a Detective is two. Non-creature tokens (a Clue, a Treasure) are filtered
 * out before the names are gathered, and nontoken creatures never enter the set at all.
 */
val AudienceWithTrostani = card("Audience with Trostani") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create a 0/1 green Plant creature token, then draw cards equal to the number of " +
        "differently named creature tokens you control."

    spell {
        effect = Effects.CreateToken(
            power = 0,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Plant"),
        ) then Effects.DrawCards(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature.token()).distinctNames()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "152"
        artist = "Ben Hill"
        flavorText = "Trostani was as regal and vibrant as ever, but Kaya couldn't shake the " +
            "sense that there was something off, some discordant note lurking deep within the " +
            "chorus."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8e23d15-33af-4fd8-964b-8ca4efdebc37.jpg?1783912873"

        ruling(
            "2024-02-02",
            "To determine the number of differently named creature tokens you control, count each " +
                "creature token you control once, but only if its English name isn't exactly the " +
                "same as another creature token you've already counted this way."
        )
    }
}
