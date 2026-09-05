package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Shambling Shell
 * {1}{B}{G}
 * Creature — Plant Zombie
 * 3/1
 * Sacrifice this creature: Put a +1/+1 counter on target creature.
 * Dredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)
 */
val ShamblingShell = card("Shambling Shell") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Plant Zombie"
    oracleText = "Sacrifice this creature: Put a +1/+1 counter on target creature.\nDredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)"
    power = 3
    toughness = 1
    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }
    keywordAbility(KeywordAbility.dredge(3))
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "230"
        artist = "Joel Thomas"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c93baa2-a23e-4e18-b4cd-779c992d2042.jpg?1783943611"
    }
}
