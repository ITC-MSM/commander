package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Necroduality
 * {3}{U}
 * Enchantment
 * Whenever a nontoken Zombie you control enters, create a token that's a copy of that creature.
 */
val Necroduality = card("Necroduality") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever a nontoken Zombie you control enters, create a token that's a copy of that creature."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype("Zombie").youControl().nontoken(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateTokenCopyOfTarget(target = EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "70"
        artist = "Billy Christian"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b674053-1f0f-436d-b106-f91f41cf3959.jpg?1782703142"
    }
}
