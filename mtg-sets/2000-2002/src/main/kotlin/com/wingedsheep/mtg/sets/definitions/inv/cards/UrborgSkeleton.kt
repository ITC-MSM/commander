package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Urborg Skeleton
 * {B}
 * Creature — Skeleton
 * 0/1
 * Kicker {3}
 * {B}: Regenerate this creature.
 * If this creature was kicked, it enters with a +1/+1 counter on it.
 */
val UrborgSkeleton = card("Urborg Skeleton") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton"
    power = 0
    toughness = 1
    oracleText = "Kicker {3}\n{B}: Regenerate this creature.\nIf this creature was kicked, it enters with a +1/+1 counter on it."

    keywordAbility(KeywordAbility.kicker("{3}"))

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Alan Pollack"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6e522a62-fbca-4362-9006-d4356c525704.jpg?1562917062"
    }
}
