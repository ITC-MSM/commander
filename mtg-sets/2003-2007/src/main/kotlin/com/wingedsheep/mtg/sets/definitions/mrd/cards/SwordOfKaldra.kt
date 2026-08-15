package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sword of Kaldra — Mirrodin #251 (canonical printing, only printing)
 * {4} · Legendary Artifact — Equipment
 *
 * Equipped creature gets +5/+5.
 * Whenever equipped creature deals damage to a creature, exile that creature.
 * Equip {4}
 *
 * The first piece of the Kaldra cycle (with [com.wingedsheep.mtg.sets.definitions.dst.cards.ShieldOfKaldra]
 * and Helm of Kaldra in Darksteel).
 *
 * The trigger is [TriggerBinding.ATTACHED] over `DamageType.Any` — *any* damage, not just combat, so
 * it fires on a Viridian Longbow ping as readily as on a block. `RecipientFilter.AnyCreature` narrows
 * it to damage dealt to creatures, and [EffectTarget.TriggeringEntity] is the *damaged* creature: a
 * damage trigger stamps the recipient as the triggering entity, which is what "exile that creature"
 * refers to. The printed reminder "(Exile it only if it's still on the battlefield)" needs no wiring —
 * a creature that already died to the damage is simply no longer there to move.
 */
val SwordOfKaldra = card("Sword of Kaldra") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "Equipped creature gets +5/+5.\n" +
        "Whenever equipped creature deals damage to a creature, exile that creature. " +
        "(Exile it only if it's still on the battlefield.)\n" +
        "Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(5, 5)
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.AnyCreature,
            binding = TriggerBinding.ATTACHED,
        )
        effect = Effects.Exile(EffectTarget.TriggeringEntity)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "251"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a665bff-b57a-450c-9310-932b0686a03e.jpg?1783944501"
    }
}
