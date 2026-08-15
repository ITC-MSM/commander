package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Guy in the Chair
 * {2}{G}
 * Creature — Human Advisor
 * 2/3
 * {T}: Add one mana of any color.
 * Web Support — {2}{G}, {T}: Put a +1/+1 counter on target Spider. Activate only as a sorcery.
 */
val GuyInTheChair = card("Guy in the Chair") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Advisor"
    oracleText = "{T}: Add one mana of any color.\nWeb Support — {2}{G}, {T}: Put a +1/+1 counter on target Spider. Activate only as a sorcery."
    power = 2
    toughness = 3
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{G}"), Costs.Tap)
        val t = target("target", TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Spider"))))
        effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t)
        timing = TimingRule.SorcerySpeed
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Zoltan Boros"
        flavorText = "\"Heads up, Miles. There's a disturbance over on Fifth you might want to check out.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65e97c06-55a6-4841-be0f-055c015df90a.jpg?1757377435"
    }
}
