package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Loxodon Battle Priest
 * {4}{W}
 * Creature — Elephant Cleric
 * 3/5
 *
 * At the beginning of combat on your turn, put a +1/+1 counter on another
 * target creature you control.
 */
val LoxodonBattlePriest = card("Loxodon Battle Priest") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Cleric"
    power = 3
    toughness = 5
    oracleText = "At the beginning of combat on your turn, put a +1/+1 counter on another target creature you control."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target(
            "another target creature you control",
            TargetCreature(filter = TargetFilter.OtherCreatureYouControl)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "Yeong-Hao Han"
        flavorText = "\"I grant you the strength of Qatros's walls and the tenacity of Arashin's oasis.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a527cdb0-f54a-4b53-83a0-6b3e8cafa45e.jpg?1743204012"
    }
}
