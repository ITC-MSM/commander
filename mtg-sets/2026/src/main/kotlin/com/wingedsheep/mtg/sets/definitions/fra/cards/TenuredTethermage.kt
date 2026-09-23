package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tenured Tethermage — the Heartwoods are gated on a land actually being sacrificed
 * ([SuccessCriterion.PermanentsSacrificed]), so accepting the "may" with no land creates nothing.
 */
val TenuredTethermage = card("Tenured Tethermage") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Human Artificer"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, you may sacrifice a land. If you do, create two tapped Heartwood tokens. (They're red and green artifacts with \"{T}: Add {R} or {G}.\")\nTap two untapped artifacts you control: Put two +1/+1 counters on this creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Effects.IfYouDo(
                action = Effects.Sacrifice(GameObjectFilter.Land, count = 1, target = EffectTarget.Controller),
                ifYouDo = Effects.CreateHeartwood(count = 2, tapped = true),
                successCriterion = SuccessCriterion.PermanentsSacrificed
            )
        )
        description = "When this creature enters, you may sacrifice a land. If you do, create two tapped Heartwood tokens."
    }

    activatedAbility {
        cost = Costs.TapPermanents(2, GameObjectFilter.Artifact)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        description = "Put two +1/+1 counters on this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "154"
        artist = "Javier Charro"
        flavorText = "\"Today, you will be learning material repurposing and adaptation. This will be on the test.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/7/1703306d-6a3d-4ab8-bf58-a9992236ef0f.jpg?1789385792"
        inBooster = false
    }
}
