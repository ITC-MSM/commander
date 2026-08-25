package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Senate Griffin — Ravnica Allegiance #219
 * {2}{W/U}{W/U} · Creature — Griffin · 3 / 2
 *
 * A flier with an enters-scry.
 */
val SenateGriffin = card("Senate Griffin") {
    manaCost = "{2}{W/U}{W/U}"
    colorIdentity = "UW"
    typeLine = "Creature — Griffin"
    power = 3
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, scry 1."

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "219"
        artist = "Lucas Graciano"
        flavorText = "\"The Senate griffins overhead used to make people think of order and safety. Not anymore.\"\n" +
        "—Lavinia"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/465adbb4-4c64-44eb-8323-61d23282c6b8.jpg"
    }
}
