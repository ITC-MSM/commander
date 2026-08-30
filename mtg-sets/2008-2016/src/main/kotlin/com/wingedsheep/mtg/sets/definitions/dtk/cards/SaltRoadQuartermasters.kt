package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Salt Road Quartermasters
 * {2}{G}
 * Creature — Human Soldier
 * 1 / 1
 *
 * This creature enters with two +1/+1 counters on it.
 * {2}{G}, Remove a +1/+1 counter from this creature: Put a +1/+1 counter on target creature.
 *
 * "Enters with" is a replacement effect, never an ETB trigger — the counters are there as it
 * arrives, so nothing can respond in between. The activation is two cost atoms in one line: the
 * mana and a self-scoped counter removal, which is what makes the ability self-limiting.
 */
val SaltRoadQuartermasters = card("Salt Road Quartermasters") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "This creature enters with two +1/+1 counters on it.\n" +
        "{2}{G}, Remove a +1/+1 counter from this creature: Put a +1/+1 counter on target creature."

    replacementEffect(EntersWithCounters(count = 2, selfOnly = true))

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{G}"),
            Costs.RemoveCounterFromSelf(Counters.PLUS_ONE_PLUS_ONE)
        )
        val t = target("target", TargetCreature())
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "199"
        artist = "Anthony Palumbo"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/4076bf6b-c8b1-49ef-8f23-afaf7a234e2d.jpg?1783938577"
    }
}
