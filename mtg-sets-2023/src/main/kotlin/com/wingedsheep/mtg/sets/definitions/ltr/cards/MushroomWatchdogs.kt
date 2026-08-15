package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mushroom Watchdogs
 * {1}{G}
 * Creature — Dog
 * 2/2
 *
 * Sacrifice a Food: Put a +1/+1 counter on this creature. It gains vigilance until end of turn.
 * Activate only as a sorcery.
 */
val MushroomWatchdogs = card("Mushroom Watchdogs") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dog"
    power = 2
    toughness = 2
    oracleText = "Sacrifice a Food: Put a +1/+1 counter on this creature. It gains vigilance until end of turn. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Any.withSubtype("Food"))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self))
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "Alexander Ostrowski"
        flavorText = "\"They won't harm you—not unless I tell 'em to. Here, Grip! Fang! Heel!\"\n—Farmer Maggot"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d15fd66d-fa7e-411d-9014-a56caa879d93.jpg?1686969514"
    }
}
