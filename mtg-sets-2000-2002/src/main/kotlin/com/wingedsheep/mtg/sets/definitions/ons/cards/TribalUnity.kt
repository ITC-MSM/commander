package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tribal Unity
 * {X}{2}{G}
 * Instant
 * Creatures of the creature type of your choice get +X/+X until end of turn.
 */
val TribalUnity = card("Tribal Unity") {
    manaCost = "{X}{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Creatures of the creature type of your choice get +X/+X until end of turn."

    spell {
        effect = Effects.ChooseCreatureTypeModifyStats(
            powerModifier = DynamicAmount.XValue,
            toughnessModifier = DynamicAmount.XValue
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "294"
        artist = "Ron Spears"
        flavorText = "\"I may have left the ways of violence behind,\" Kamahl said, \"but I still believe in the power of muscle and bone.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f7b5ddf-d5a6-42bf-a196-7e834dbdb3dc.jpg?1562921272"
    }
}
