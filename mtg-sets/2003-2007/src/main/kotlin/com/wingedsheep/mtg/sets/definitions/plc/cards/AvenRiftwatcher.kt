package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Aven Riftwatcher
 * {2}{W}
 * Creature — Bird Rebel Soldier
 * 2/3
 * Flying
 * Vanishing 3
 * When this creature enters or leaves the battlefield, you gain 2 life.
 *
 * "Enters **or** leaves" is two triggered abilities, not one — the two halves key off opposite
 * ends of the same zone change, so neither trigger can spell both.
 */
val AvenRiftwatcher = card("Aven Riftwatcher") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Rebel Soldier"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "Vanishing 3 (This creature enters with three time counters on it. At the beginning of your upkeep, remove a time counter from it. When the last is removed, sacrifice it.)\n" +
        "When this creature enters or leaves the battlefield, you gain 2 life."

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.vanishing(3))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Don Hazeltine"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6a4394a-9ce2-4876-8a04-38e3775123af.jpg"
    }
}
