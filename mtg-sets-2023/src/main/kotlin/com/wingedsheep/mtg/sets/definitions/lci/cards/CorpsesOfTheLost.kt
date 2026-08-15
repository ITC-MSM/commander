package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.PayLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Corpses of the Lost — {2}{B}
 * Enchantment
 * Rare — LCI #98
 *
 * Skeletons you control get +1/+0 and have haste.
 * When this enchantment enters, create a 2/2 black Skeleton Pirate creature token.
 * At the beginning of your end step, if you descended this turn, you may pay 1 life.
 * If you do, return this enchantment to its owner's hand.
 * (You descended if a permanent card was put into your graveyard from anywhere.)
 *
 * The two static abilities are separate layer-6 continuous effects scoped to
 * [Subtype.SKELETON] creatures the controller controls:
 *  1. [ModifyStats] adds +1/+0 (power bonus, layer 7c).
 *  2. [GrantKeyword] grants haste (layer 6).
 *
 * The end-step trigger is a "descend" trigger (CR 700.11) with an intervening-if gate
 * ([Conditions.YouDescendedThisTurn]). On resolution the player is offered an optional
 * life payment ([Gate.MayPay] via [OptionalCostEffect]); if paid, [Effects.ReturnToHand]
 * bounces the source enchantment to its owner's hand.
 */
val CorpsesOfTheLost = card("Corpses of the Lost") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Skeletons you control get +1/+0 and have haste.\n" +
        "When this enchantment enters, create a 2/2 black Skeleton Pirate creature token.\n" +
        "At the beginning of your end step, if you descended this turn, you may pay 1 life. " +
        "If you do, return this enchantment to its owner's hand. " +
        "(You descended if a permanent card was put into your graveyard from anywhere.)"

    // Static 1: Skeletons you control get +1/+0 (layer 7c).
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SKELETON).youControl())
        )
    }

    // Static 2: Skeletons you control have haste (layer 6).
    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.HASTE,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SKELETON).youControl())
        )
    }

    // ETB: create a 2/2 black Skeleton Pirate creature token.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Skeleton", "Pirate"),
            imageUri = "https://cards.scryfall.io/normal/front/2/e/2ec83fe2-173b-4aa6-bfa1-892b092bd1f6.jpg?1783913607",
        )
    }

    // End-step descend trigger: you may pay 1 life; if you do, return this to hand.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouDescendedThisTurn()
        effect = OptionalCostEffect(
            cost = PayLifeEffect(1),
            ifPaid = Effects.ReturnToHand(EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f661095-3645-4e44-ac39-752e417c2174.jpg?1782694532"
        ruling(
            "2023-11-10",
            "Some cards refer to a player who has \"descended this turn.\" This means that a permanent card has been put into that player's graveyard from anywhere this turn."
        )
        ruling(
            "2023-11-10",
            "A permanent card is an artifact, battle, creature, enchantment, land, or planeswalker card. Tokens are not cards, and while tokens are put into the graveyard before ceasing to exist, that action doesn't count as a player having descended."
        )
        ruling(
            "2023-11-10",
            "Abilities that begin with \"At the beginning of your end step, if you descended this turn\" will trigger only once during your end step, no matter how many times you descended this turn. However, if you haven't descended this turn as your end step begins, the ability won't trigger at all. It's not possible to put a permanent card into your graveyard during the end step in time to have the ability trigger."
        )
    }
}
