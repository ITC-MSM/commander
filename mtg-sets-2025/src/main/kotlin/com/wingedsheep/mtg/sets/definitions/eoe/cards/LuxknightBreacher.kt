package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Luxknight Breacher
 * {3}{W}
 * Creature — Human Knight
 * 2/2
 *
 * This creature enters with a +1/+1 counter on it for each other creature and/or artifact you control.
 */
val LuxknightBreacher = card("Luxknight Breacher") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "This creature enters with a +1/+1 counter on it for each other creature and/or artifact you control."

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.CreatureOrArtifact,
                excludeSelf = true
            )
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Cristi Balanescu"
        flavorText = "\"Only when a vessel is emptied of shallow ego may the depths of light flow in to fill it.\"\n" +
            "—Seraphus Chek, Sunstar poet"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1236731-0d33-4705-8077-3cf58acf9a39.jpg?1752946655"

        ruling(
            "2025-07-25",
            "In the rare case where Luxknight Breacher enters at the same time as one or more other " +
                "creatures and/or artifacts you control, it doesn't count those other creatures and/or " +
                "artifacts. It counts only creatures and/or artifacts that are already on the battlefield " +
                "under your control."
        )
    }
}
