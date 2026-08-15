package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Meathook Massacre
 * {X}{B}{B}
 * Legendary Enchantment
 *
 * When The Meathook Massacre enters, each creature gets -X/-X until end of turn.
 * Whenever a creature you control dies, each opponent loses 1 life.
 * Whenever a creature an opponent controls dies, you gain 1 life.
 *
 *  - **ETB** — [DynamicAmount.CastX] reads the {X} value off the cast enchantment and rides it onto
 *    the permanent, so every creature gets -X/-X until end of turn (negated via
 *    [DynamicAmount.Multiply] by -1, the Terror Tide idiom).
 *  - The two death abilities are ordinary per-creature triggered abilities of the enchantment: each
 *    individual death drains/gains 1 life (a board wipe fires them once per creature).
 */
val TheMeathookMassacre = card("The Meathook Massacre") {
    manaCost = "{X}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Enchantment"
    oracleText = "When The Meathook Massacre enters, each creature gets -X/-X until end of turn.\n" +
        "Whenever a creature you control dies, each opponent loses 1 life.\n" +
        "Whenever a creature an opponent controls dies, you gain 1 life."

    // When The Meathook Massacre enters, each creature gets -X/-X until end of turn.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            filter = GroupFilter.AllCreatures,
            effect = Effects.ModifyStats(
                DynamicAmount.Multiply(DynamicAmount.CastX, -1),
                DynamicAmount.Multiply(DynamicAmount.CastX, -1),
                EffectTarget.Self
            )
        )
    }

    // Whenever a creature you control dies, each opponent loses 1 life.
    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    // Whenever a creature an opponent controls dies, you gain 1 life.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "112"
        artist = "Chris Seaman"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08950015-eee5-4327-888c-82dfd13bb9ad.jpg?1782703660"
    }
}
