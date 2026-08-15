package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * The Ooze
 * {2}
 * Legendary Artifact
 *
 * Whenever a creature you control with a +1/+1 counter on it leaves the
 * battlefield, create a Mutagen token for each +1/+1 counter on it.
 * {T}: Exile target card from a graveyard. Create a Mutagen token.
 */
val TheOoze = card("The Ooze") {
    manaCost = "{2}"
    typeLine = "Legendary Artifact"
    oracleText = "Whenever a creature you control with a +1/+1 counter on it leaves the battlefield, create a Mutagen token for each +1/+1 counter on it. (A Mutagen token is an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")\n{T}: Exile target card from a graveyard. Create a Mutagen token."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE),
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateMutagenToken(
            DynamicAmount.EntityProperty(
                EntityReference.Triggering,
                EntityNumericProperty.CounterCount(CounterTypeFilter.PlusOnePlusOne)
            )
        )
        description = "Whenever a creature you control with a +1/+1 counter on it leaves the battlefield, create a Mutagen token for each +1/+1 counter on it."
    }

    activatedAbility {
        val graveyardCard = target("target card from a graveyard", Targets.CardInGraveyard)
        cost = Costs.Tap
        effect = Effects.Exile(graveyardCard).then(Effects.CreateMutagenToken())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "177"
        artist = "Gabriel Tanko"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f9bd4da-4626-40ba-95f4-14e3de36f989.jpg?1769006428"
    }
}
