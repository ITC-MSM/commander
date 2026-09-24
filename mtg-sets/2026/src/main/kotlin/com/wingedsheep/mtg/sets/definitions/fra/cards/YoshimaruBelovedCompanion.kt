package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyCounterPlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Yoshimaru, Beloved Companion — Hardened Scales on a body. The replacement also applies to the
 * counter its own {6} ability puts on a legendary creature you control, so that one gets two; an
 * opponent's legendary creature gets just the one.
 */
val YoshimaruBelovedCompanion = card("Yoshimaru, Beloved Companion") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Dog"
    power = 2
    toughness = 2
    oracleText = "If one or more +1/+1 counters would be put on a creature you control, that many plus one " +
        "+1/+1 counters are put on it instead.\n" +
        "{6}: Put a +1/+1 counter on target legendary creature."

    replacementEffect(ModifyCounterPlacement(modifier = 1))

    activatedAbility {
        cost = Costs.Mana("{6}")
        val creature = target(
            "target legendary creature",
            TargetCreature(filter = TargetFilter.Creature.legendary()),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Brian Valeza"
        flavorText = "The Emperor spared no comfort for the friend who had so diligently awaited her return."
        imageUri = "https://cards.scryfall.io/normal/front/3/8/384f3b7d-8d7f-41bf-bebd-64e8babe7fca.jpg?1789470881"
        inBooster = false
    }
}
