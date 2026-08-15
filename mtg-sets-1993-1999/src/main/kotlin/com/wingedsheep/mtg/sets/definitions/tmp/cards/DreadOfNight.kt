package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Dread of Night
 * {B}
 * Enchantment
 * White creatures get -1/-1.
 */
val DreadOfNight = card("Dread of Night") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "White creatures get -1/-1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = -1,
            toughnessBonus = -1,
            filter = GroupFilter(GameObjectFilter.Creature.withColor(Color.WHITE))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Richard Thomas"
        flavorText = "\"These moonless, foreign skies keep me in thrall. Dark whispers echo in the night, and I cannot resist.\"\n—Selenia, dark angel"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d08586d4-8163-454c-b8d8-c5034c4aee6c.jpg?1562056862"
    }
}
