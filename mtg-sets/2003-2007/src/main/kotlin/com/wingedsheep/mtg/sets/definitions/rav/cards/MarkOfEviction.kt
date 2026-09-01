package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mark of Eviction
 * {U}
 * Enchantment — Aura
 *
 * Enchant creature
 * At the beginning of your upkeep, return enchanted creature and all Auras attached to that
 * creature to their owners' hands.
 *
 * Mark of Eviction is itself an Aura attached to that creature, so it bounces *itself* along
 * with the host — that recursion is the card, not an accident of the script.
 *
 * Hence the gather-first shape. The Auras are collected into a pipeline collection while
 * everything is still attached; only then does the host move, and the collection (entity ids,
 * not a live attachment read) moves after it. Returning the Auras first instead would leave the
 * later steps reading "enchanted creature" off an Aura that is already in its owner's hand.
 * Nothing dies in between: an Aura attached to nothing is put into a graveyard by a state-based
 * action (CR 704.5m), and state-based actions are not checked in the middle of a resolution.
 *
 * Equipment attached to the host is deliberately left alone — the printed text names Auras, and
 * an unattached Equipment simply stays on the battlefield.
 */
val MarkOfEviction = card("Mark of Eviction") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "At the beginning of your upkeep, return enchanted creature and all Auras attached to " +
        "that creature to their owners' hands."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.AttachedTo(
                    host = EffectTarget.EnchantedCreature,
                    filter = GameObjectFilter.Permanent.withSubtype(Subtype.AURA)
                ),
                storeAs = "evictedAuras"
            ),
            Effects.ReturnToHand(EffectTarget.EnchantedCreature),
            MoveCollectionEffect(
                from = "evictedAuras",
                destination = CardDestination.ToZone(Zone.HAND)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Tsutomu Kawade"
        flavorText = "Tharashk the Bold suddenly wasn't himself. Or anyone else, for that matter."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae826236-e591-4afb-817b-0017a11010ad.jpg?1783943682"
    }
}
