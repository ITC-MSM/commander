package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Insatiable Skittermaw
 * {2}{B}
 * Creature — Insect Horror
 * 2/2
 * Menace
 * Void — At the beginning of your end step, if a nonland permanent left the battlefield this turn
 *   or a spell was warped this turn, put a +1/+1 counter on this creature.
 */
val InsatiableSkittermaw = card("Insatiable Skittermaw") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Horror"
    power = 2
    toughness = 2
    oracleText = "Menace\nVoid — At the beginning of your end step, if a nonland permanent left the battlefield this turn or a spell was warped this turn, put a +1/+1 counter on this creature."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.Void
        effect = AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Diego Gisbert"
        flavorText = "\"Redd's gone. And the chittering—it won't stop!\"\n—Decrypted log 3.88"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9d30cca-ea33-418f-bba3-5103f1dbd751.jpg?1752946993"
    }
}
