package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Desperate Futurescribe — the scry/surveil check is made as the trigger resolves, and picks
 * *instead*: a +1/+1 counter replaces the temporary +1/+1 rather than adding to it.
 */
val DesperateFuturescribe = card("Desperate Futurescribe") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Kor Scout"
    power = 3
    toughness = 4
    oracleText = "Flying\n" +
        "At the beginning of combat on your turn, another target creature you control gets +1/+1 " +
        "until end of turn. If you've scried or surveilled this turn, put a +1/+1 counter on that " +
        "creature instead."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target("another target creature you control", Targets.OtherCreatureYouControl)
        effect = ConditionalEffect(
            condition = Conditions.ScriedOrSurveiledThisTurn,
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
            elseEffect = Effects.ModifyStats(1, 1, creature),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Sidharth Chaturvedi"
        flavorText = "As realities merged, the futures she could read dwindled."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfaa3ebd-5c21-4e99-a0dd-8426d19b53a5.jpg?1789644852"
        inBooster = false
    }
}
