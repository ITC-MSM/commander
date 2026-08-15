package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.DealsDamageEvent
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.AttachEquipmentEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Quintessential Katana
 * {W}
 * Artifact — Equipment
 *
 * Equipped creature gets +1/+1 and has "Whenever this creature deals
 * combat damage, untap it and you gain 2 life."
 * Whenever a Ninja you control enters, you may attach this Equipment
 * to it.
 * Equip {2}
 */
val QuintessentialKatana = card("Quintessential Katana") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1 and has \"Whenever this creature deals combat damage, untap it and you gain 2 life.\"\nWhenever a Ninja you control enters, you may attach this Equipment to it.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            TriggeredAbility.create(
                trigger = DealsDamageEvent(
                    damageType = DamageType.Combat,
                    recipient = RecipientFilter.Any
                ),
                binding = TriggerBinding.SELF,
                effect = Effects.Untap(EffectTarget.Self)
                    .then(Effects.GainLife(2))
            )
        )
    }

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Creature.withSubtype("Ninja").youControl(),
                to = Zone.BATTLEFIELD
            ),
            binding = TriggerBinding.ANY
        )
        optional = true
        effect = AttachEquipmentEffect(EffectTarget.TriggeringEntity)
        description = "Whenever a Ninja you control enters, you may attach this Equipment to it."
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "23"
        artist = "Anthony Devine"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef9e227e-c581-479c-a962-2f191352b07f.jpg?1771502541"
    }
}
