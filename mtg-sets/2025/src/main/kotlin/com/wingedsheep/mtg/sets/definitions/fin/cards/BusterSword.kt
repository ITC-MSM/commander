package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Buster Sword
 * {3}
 * Artifact — Equipment
 * Equipped creature gets +3/+2.
 * Whenever equipped creature deals combat damage to a player, draw a card, then you may
 *   cast a spell from your hand with mana value less than or equal to that damage without
 *   paying its mana cost.
 * Equip {2}
 *
 * Composed from existing primitives (the Glamdring / Press the Enemy free-cast shape):
 *   1. [ModifyStats] +3/+2 on [Filters.EquippedCreature].
 *   2. A [DamageType.Combat] / [RecipientFilter.AnyPlayer] trigger bound to the equipped
 *      creature ([TriggerBinding.ATTACHED]). "That damage" is captured from the triggering
 *      event via [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT] *before* the draw (so the drawn
 *      card can't change the cap), then: draw a card → gather nonland cards from your hand
 *      with mana value ≤ that damage → optionally cast one without paying its mana cost.
 *      "A spell" excludes lands, so the gather filters to [GameObjectFilter.Nonland].
 */
val BusterSword = card("Buster Sword") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +3/+2.\n" +
        "Whenever equipped creature deals combat damage to a player, draw a card, then you " +
        "may cast a spell from your hand with mana value less than or equal to that damage " +
        "without paying its mana cost.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(3, 2, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            binding = TriggerBinding.ATTACHED
        )
        effect = Effects.Composite(
            // Capture "that damage" before drawing, so the drawn card can't alter the cap.
            Effects.StoreNumber(
                "combatDamage",
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)
            ),
            // Draw a card.
            Effects.DrawCards(1),
            // Gather nonland cards from your hand with mana value ≤ that damage.
            GatherCardsEffect(
                source = CardSource.FromZone(
                    zone = Zone.HAND,
                    player = Player.You,
                    filter = GameObjectFilter.Nonland
                ),
                storeAs = "handSpells"
            ),
            FilterCollectionEffect(
                from = "handSpells",
                filter = CollectionFilter.ManaValueAtMost(DynamicAmount.VariableReference("combatDamage")),
                storeMatching = "castable"
            ),
            // You may choose one of the eligible spells (the "you may cast a spell" choice).
            SelectFromCollectionEffect(
                from = "castable",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                chooser = Chooser.Controller,
                storeSelected = "chosen",
                prompt = "You may cast a spell with mana value less than or equal to that damage without paying its mana cost.",
                selectedLabel = "Cast for free",
            ),
            // Cast the chosen spell without paying its mana cost.
            Effects.CastFromCollectionWithoutPayingCost("chosen")
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "255"
        artist = "Douzen"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/374d7383-a1a7-4eea-91f7-290180e14cc9.jpg?1748706745"
    }
}
