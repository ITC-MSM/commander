package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mistcutter Hydra
 * {X}{G}
 * Creature — Hydra
 * 0 / 0
 *
 * This spell can't be countered.
 * Haste, protection from blue
 * This creature enters with X +1/+1 counters on it.
 *
 * The printed P/T is 0/0; the body comes entirely from the entry counters, which is a replacement
 * effect ([EntersWithDynamicCounters]) rather than a triggered ability. "This spell can't be
 * countered" is a characteristic of the *spell*, so it rides the card-level `cantBeCountered` flag.
 */
val MistcutterHydra = card("Mistcutter Hydra") {
    manaCost = "{X}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Hydra"
    power = 0
    toughness = 0
    oracleText = "This spell can't be countered.\nHaste, protection from blue\nThis creature enters with X +1/+1 counters on it."

    cantBeCountered = true

    keywords(Keyword.HASTE)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLUE)))

    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "162"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c844ed7-8ed2-4a39-9134-e14e476ab0c4.jpg"
    }
}
