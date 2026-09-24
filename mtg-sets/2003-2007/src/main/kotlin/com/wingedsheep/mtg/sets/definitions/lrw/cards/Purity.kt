package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Purity — Lorwyn #37
 *
 * The life gain is the rest of the prevention effect ([PreventDamage.onPrevented]), not a trigger:
 * it happens as the damage is prevented, for exactly the amount this application prevented. Damage
 * that can't be prevented is dealt, and then nothing was prevented and no life is gained.
 */
val Purity = card("Purity") {
    manaCost = "{3}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elemental Incarnation"
    power = 6
    toughness = 6
    oracleText = "Flying\nIf noncombat damage would be dealt to you, prevent that damage. You gain life equal to the damage prevented this way.\nWhen Purity is put into a graveyard from anywhere, shuffle it into its owner's library."

    keywords(Keyword.FLYING)

    replacementEffect(
        PreventDamage(
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.You,
                damageType = DamageType.NonCombat
            ),
            onPrevented = Effects.GainLife(DynamicAmounts.preventedDamage())
        )
    )

    triggeredAbility {
        triggerZone = Zone.GRAVEYARD
        trigger = TriggerSpec(
            event = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            binding = TriggerBinding.SELF
        )
        // Shuffle even if the card has left the graveyard before this resolves.
        effect = Effects.Move(EffectTarget.Self, Zone.LIBRARY, fromZone = Zone.GRAVEYARD) then
            ShuffleLibraryEffect()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/154a335d-188d-49c3-af6d-c8e702b4b3ba.jpg?1783942909"
        ruling("2007-10-01", "The last ability triggers when the Incarnation is put into its owner’s graveyard from any zone, not just from on the battlefield.")
        ruling("2007-10-01", "Although this ability triggers when the Incarnation is put into a graveyard from the battlefield, it doesn’t *specifically* trigger on leaving the battlefield, so it doesn’t behave like other leaves-the-battlefield abilities. The ability will trigger from the graveyard.")
        ruling("2007-10-01", "If the Incarnation had lost this ability while on the battlefield (due to Lignify, for example) and then was destroyed, the ability would still trigger and it would get shuffled into its owner’s library. However, if the Incarnation lost this ability when it was put into the graveyard (due to Yixlid Jailer, for example), the ability wouldn’t trigger and the Incarnation would remain in the graveyard.")
        ruling("2007-10-01", "If the Incarnation is removed from the graveyard after the ability triggers but before it resolves, it will remain in its new zone when its owner shuffles their library. Similarly, if a replacement effect has the Incarnation move to a different zone instead of being put into the graveyard, the ability won’t trigger at all.")
    }
}
