package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Keldon Marauders
 * {1}{R}
 * Creature — Human Warrior
 * 3/3
 * Vanishing 2
 * When this creature enters or leaves the battlefield, it deals 1 damage to target player or planeswalker.
 *
 * "Enters **or** leaves" is two triggered abilities; the leave half fires off last-known
 * information, which is why vanishing's sacrifice still gets the second ping in.
 */
val KeldonMarauders = card("Keldon Marauders") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 3
    oracleText = "Vanishing 2 (This creature enters with two time counters on it. At the beginning of your upkeep, remove a time counter from it. When the last is removed, sacrifice it.)\n" +
        "When this creature enters or leaves the battlefield, it deals 1 damage to target player or planeswalker."

    keywordAbility(KeywordAbility.vanishing(2))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, t)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        val t = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Alex Horley-Orlandelli"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/677c28db-34d2-4325-99d1-9b689753802f.jpg"
    }
}
