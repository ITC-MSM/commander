package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Civic Stalwart — Ravnica Allegiance #6
 * {3}{W} · Creature — Elephant Soldier · 3 / 3
 *
 * One group named once: [Patterns.Group] `modifyStatsForAll` gathers the creatures you control
 * in a single pass, which is the shape the printed sentence describes. The Stalwart is on the
 * battlefield by the time its own enters trigger resolves, so it pumps itself too.
 */
val CivicStalwart = card("Civic Stalwart") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Soldier"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, creatures you control get +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Gabor Szikszai"
        flavorText = "\"These are your streets. Defend them! This is your neighborhood. Honor it! This is your city. Save it!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa981489-4301-43f6-b1d7-2aa42e00cf75.jpg"
    }
}
