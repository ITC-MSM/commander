package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Elegant Edgecrafters
 * {4}{G}{G}
 * Creature — Elf Artificer
 * 3/4
 * This creature can't be blocked by creatures with power 2 or less.
 * Fabricate 2
 *
 * The evasion is the unified [CantBeBlockedBy] over `Creature.powerAtMost(2)`. Fabricate is
 * engine-derived: declaring [KeywordAbility.fabricate] is the whole implementation, and
 * hand-writing the enters-modal trigger beside it would stack a second copy on top of the
 * engine's.
 */
val ElegantEdgecrafters = card("Elegant Edgecrafters") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Artificer"
    oracleText = "This creature can't be blocked by creatures with power 2 or less.\n" +
        "Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless Servo artifact creature tokens.)"
    power = 3
    toughness = 4

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    keywordAbility(KeywordAbility.fabricate(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "154"
        artist = "Sean Sevestre"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/352941f3-951e-428d-bec4-7790c3ea7cb2.jpg?1783937179"
    }
}
