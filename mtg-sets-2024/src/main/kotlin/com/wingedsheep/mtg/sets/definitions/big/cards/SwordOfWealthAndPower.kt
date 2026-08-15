package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantProtectionFromCardType
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Sword of Wealth and Power
 * {3}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+2 and has protection from instants and from sorceries.
 * Whenever equipped creature deals combat damage to a player, create a Treasure token. When you
 * next cast an instant or sorcery spell this turn, copy that spell. You may choose new targets
 * for the copy.
 * Equip {2}
 *
 * Protection from a card type is granted via [GrantProtectionFromCardType] (one per type), enforced
 * at targeting by the engine. The combat-damage trigger binds to the equipped creature
 * ([TriggerBinding.ATTACHED]); the "create a Treasure" half plus the "copy your next instant or
 * sorcery" delayed trigger are the existing [Effects.CreateTreasure] and [Effects.CopyNextSpellCast]
 * primitives (the latter defaults to instant-or-sorcery and lets you choose new targets).
 */
val SwordOfWealthAndPower = card("Sword of Wealth and Power") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+2 and has protection from instants and from sorceries.\n" +
        "Whenever equipped creature deals combat damage to a player, create a Treasure token. " +
        "When you next cast an instant or sorcery spell this turn, copy that spell. You may " +
        "choose new targets for the copy.\n" +
        "Equip {2}"

    // Equipped creature gets +2/+2 ...
    staticAbility {
        ability = ModifyStats(+2, +2, Filters.EquippedCreature)
    }
    // ... and has protection from instants and from sorceries.
    staticAbility {
        ability = GrantProtectionFromCardType(CardType.INSTANT, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantProtectionFromCardType(CardType.SORCERY, Filters.EquippedCreature)
    }

    // Whenever equipped creature deals combat damage to a player, create a Treasure token,
    // then set up the "copy your next instant/sorcery this turn" delayed trigger.
    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            binding = TriggerBinding.ATTACHED
        )
        effect = Effects.Composite(
            listOf(
                Effects.CreateTreasure(1),
                Effects.CopyNextSpellCast()
            )
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "26"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed9e5041-3c05-4a8a-9f00-081b01685d0c.jpg?1770090444"
    }
}
