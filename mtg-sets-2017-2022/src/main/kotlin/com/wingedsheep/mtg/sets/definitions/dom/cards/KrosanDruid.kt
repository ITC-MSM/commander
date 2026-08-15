package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked

/**
 * Krosan Druid
 * {2}{G}
 * Creature — Centaur Druid
 * 2/3
 * Kicker {4}{G}
 * When this creature enters, if it was kicked, you gain 10 life.
 */
val KrosanDruid = card("Krosan Druid") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Druid"
    power = 2
    toughness = 3
    oracleText = "Kicker {4}{G}\nWhen this creature enters, if it was kicked, you gain 10 life."

    keywordAbility(KeywordAbility.kicker("{4}{G}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        effect = Effects.GainLife(10)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "Bastien L. Deharme"
        flavorText = "\"Druids endure disaster as seeds endure winter. Now Krosa blooms once again.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c332dc82-7664-44d0-b92a-a3d867399884.jpg?1562742431"
    }
}
