package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity

/**
 * Craterhoof Behemoth — Avacyn Restored #172 (canonical printing)
 * {5}{G}{G}{G} · Creature — Beast · Mythic
 * 5/5
 *
 * Haste
 * When this creature enters, creatures you control gain trample and get +X/+X until end of
 * turn, where X is the number of creatures you control.
 *
 * X is locked in at resolution as the number of creatures you control (which already
 * includes Craterhoof itself, since it is on the battlefield when the ability resolves).
 * Both the keyword grant and the stat boost apply to every creature you control at that
 * time via [Patterns.Group] group helpers (each iterates the projected battlefield), so a
 * creature that enters after resolution is unaffected.
 */
val CraterhoofBehemoth = card("Craterhoof Behemoth") {
    manaCost = "{5}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Haste\n" +
        "When this creature enters, creatures you control gain trample and get +X/+X until " +
        "end of turn, where X is the number of creatures you control."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.grantKeywordToAll(Keyword.TRAMPLE, Filters.Group.creaturesYouControl)
            .then(
                Patterns.Group.modifyStatsForAll(
                    DynamicAmounts.creaturesYouControl(),
                    DynamicAmounts.creaturesYouControl(),
                    Filters.Group.creaturesYouControl
                )
            )
        description = "creatures you control gain trample and get +X/+X until end of turn, " +
            "where X is the number of creatures you control."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "172"
        artist = "Chris Rahn"
        flavorText = "Its footsteps of today are the lakes of tomorrow."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a249be17-73ed-4108-89c0-f7e87939beb8.jpg?1592709311"
    }
}
