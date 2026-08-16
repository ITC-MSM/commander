package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mandible Justiciar
 * {1}{W}
 * Artifact Creature — Phyrexian Cleric
 * 2/1
 *
 * Lifelink
 * Whenever another artifact you control enters, this creature gets +1/+1 until end of turn.
 *
 * The word "another" is [TriggerBinding.OTHER]: the Justiciar entering does not pump itself.
 */
val MandibleJusticiar = card("Mandible Justiciar") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Phyrexian Cleric"
    power = 2
    toughness = 1
    oracleText = "Lifelink\n" +
        "Whenever another artifact you control enters, this creature gets +1/+1 until end of turn."

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Mike Franchina"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/544a707d-4090-4a74-91aa-60fbd8a4ae96.jpg?1783918079"
    }
}
