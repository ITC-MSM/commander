package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Veil of Assimilation
 * {1}{W}
 * Artifact
 *
 * Whenever this artifact or another artifact you control enters, target creature you control gets
 * +1/+1 and gains vigilance until end of turn.
 *
 * "This artifact **or** another" includes the Veil itself, so the binding is
 * [TriggerBinding.ANY] — not `OTHER`. The Veil entering triggers its own ability.
 */
val VeilOfAssimilation = card("Veil of Assimilation") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "Whenever this artifact or another artifact you control enters, target creature " +
        "you control gets +1/+1 and gains vigilance until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY,
        )
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, creature),
            Effects.GrantKeyword(Keyword.VIGILANCE, creature),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Wayne Wu"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f9d8d87-e32f-4ad9-8d72-d1b88cd14510.jpg?1783918072"
    }
}
