package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Worldslayer — Mirrodin #276 (canonical printing; reprinted in M12)
 * {5} · Artifact — Equipment
 *
 * Whenever equipped creature deals combat damage to a player, destroy all permanents other than this
 * Equipment.
 * Equip {5}
 *
 * Same trigger shape as [MaskOfMemory] — combat damage to a player, [TriggerBinding.ATTACHED] so it
 * reads the equipped creature rather than the Equipment itself.
 *
 * "All permanents other than this Equipment" is a single [Effects.DestroyAll] over
 * `GameObjectFilter.Permanent.notSourceItself()`; the source-relative exclusion is what keeps
 * Worldslayer on the battlefield to be equipped again. Nothing else is spared — the 2011-09-22
 * ruling ("The equipped creature is also destroyed") falls straight out of the filter rather than
 * needing a carve-out, and lands go with everything else.
 */
val Worldslayer = card("Worldslayer") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Whenever equipped creature deals combat damage to a player, destroy all permanents " +
        "other than this Equipment.\n" +
        "Equip {5} ({5}: Attach to target creature you control. Equip only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            binding = TriggerBinding.ATTACHED,
        )
        effect = Effects.DestroyAll(GameObjectFilter.Permanent.notSourceItself())
    }

    equipAbility("{5}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cb1b869-3e2d-4447-a12d-e790883feeee.jpg?1783944496"
        ruling("2011-09-22", "The equipped creature is also destroyed.")
    }
}
