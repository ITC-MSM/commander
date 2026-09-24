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
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Hostility — Lorwyn #176
 *
 * The tokens are the rest of the prevention effect ([PreventDamage.onPrevented]): one per 1 damage
 * this application prevented, created as the damage is prevented. Only a *spell* you control
 * qualifies — an ability's damage (Hostility's own tokens attacking, a creature's ping) is dealt
 * normally.
 */
val Hostility = card("Hostility") {
    manaCost = "{3}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Incarnation"
    power = 6
    toughness = 6
    oracleText = "Haste\nIf a spell you control would deal damage to an opponent, prevent that damage. Create a 3/1 red Elemental Shaman creature token with haste for each 1 damage prevented this way.\nWhen Hostility is put into a graveyard from anywhere, shuffle it into its owner's library."

    keywords(Keyword.HASTE)

    replacementEffect(
        PreventDamage(
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Opponent,
                source = SourceFilter.SpellYouControl
            ),
            onPrevented = Effects.CreateToken(
                count = DynamicAmounts.preventedDamage(),
                power = 3,
                toughness = 1,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Elemental", "Shaman"),
                keywords = setOf(Keyword.HASTE)
            )
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
        collectorNumber = "176"
        artist = "Omar Rayyan"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f560199-7703-4e2a-8e2d-5728f6efef46.jpg?1783942873"
        ruling("2007-10-01", "The last ability triggers when the Incarnation is put into its owner’s graveyard from any zone, not just from on the battlefield.")
        ruling("2007-10-01", "Although this ability triggers when the Incarnation is put into a graveyard from the battlefield, it doesn’t *specifically* trigger on leaving the battlefield, so it doesn’t behave like other leaves-the-battlefield abilities. The ability will trigger from the graveyard.")
        ruling("2007-10-01", "If the Incarnation had lost this ability while on the battlefield (due to Lignify, for example) and then was destroyed, the ability would still trigger and it would get shuffled into its owner’s library. However, if the Incarnation lost this ability when it was put into the graveyard (due to Yixlid Jailer, for example), the ability wouldn’t trigger and the Incarnation would remain in the graveyard.")
        ruling("2007-10-01", "If the Incarnation is removed from the graveyard after the ability triggers but before it resolves, it will remain in its new zone when its owner shuffles their library. Similarly, if a replacement effect has the Incarnation move to a different zone instead of being put into the graveyard, the ability won’t trigger at all.")
    }
}
