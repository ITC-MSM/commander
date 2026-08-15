package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * South Wind Avatar
 * {3}{B}
 * Creature — Snake Spirit Avatar
 * 3/4
 *
 * Deathtouch
 * Whenever another creature you control dies, you gain life equal to
 * its toughness.
 * Whenever you gain life, each opponent loses 1 life.
 */
val SouthWindAvatar = card("South Wind Avatar") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Snake Spirit Avatar"
    oracleText = "Deathtouch\nWhenever another creature you control dies, you gain life equal to its toughness.\nWhenever you gain life, each opponent loses 1 life."
    power = 3
    toughness = 4

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Creature.youControl(),
                from = Zone.BATTLEFIELD,
                to = Zone.GRAVEYARD
            ),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.GainLife(
            DynamicAmount.EntityProperty(EntityReference.Triggering, EntityNumericProperty.Toughness)
        )
        description = "Whenever another creature you control dies, you gain life equal to its toughness."
    }

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "78"
        artist = "InHyuk Lee"
        flavorText = "Snake-Eyes's gift takes others back to their worst memory. To the weak, it is a curse; to the wise, a second chance."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c17bc07c-7147-4c84-aa0b-8a0865fba94e.jpg?1769005861"
    }
}
