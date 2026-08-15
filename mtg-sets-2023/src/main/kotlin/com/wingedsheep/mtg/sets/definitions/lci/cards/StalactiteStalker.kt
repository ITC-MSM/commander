package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Stalactite Stalker — {B}
 * Creature — Goblin Rogue
 * 1/1
 *
 * Menace
 * At the beginning of your end step, if you descended this turn, put a +1/+1 counter on this creature.
 * (You descended if a permanent card was put into your graveyard from anywhere.)
 * {2}{B}, Sacrifice this creature: Target creature gets -X/-X until end of turn, where X is this
 * creature's power.
 *
 * "Descended this turn" is CR 700.11 (intervening-if gated end-step trigger, once per end step;
 * won't trigger unless you've already descended when the step begins).
 *
 * The activated ability sacrifices Stalactite Stalker as a cost, then reads its power for X. Because
 * the source is gone by resolution, X is last-known information: `EntityProperty(Source, Power)`
 * resolves via the source's LKI snapshot captured at cost-payment time (CR 113.7a / 608.2h),
 * including any +1/+1 counters it had accrued. (`Source` — not `Sacrificed()` — is the correct
 * reference for a `SacrificeSelf` cost: the self-sacrifice target is implicit, so it never lands in
 * the payment's explicit sacrifice list that `Sacrificed()` reads, whereas the self-sac source
 * snapshot backing `Source` is always captured; this matches Ghitu Fire-Eater.) -X/-X is that amount
 * negated (`Multiply(..., -1)`).
 */
val StalactiteStalker = card("Stalactite Stalker") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "Menace\n" +
        "At the beginning of your end step, if you descended this turn, put a +1/+1 counter on this creature. " +
        "(You descended if a permanent card was put into your graveyard from anywhere.)\n" +
        "{2}{B}, Sacrifice this creature: Target creature gets -X/-X until end of turn, where X is this creature's power."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouDescendedThisTurn()
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.SacrificeSelf)
        val creature = target("target", Targets.Creature)
        val negativePower = DynamicAmount.Multiply(
            DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
            -1
        )
        effect = Effects.ModifyStats(negativePower, negativePower, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "122"
        artist = "Olivier Bernard"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5319c0b1-de54-492a-bdea-85a5a75d693e.jpg?1782694512"
        ruling("2023-11-10", "Some cards refer to a player who has \"descended this turn.\" This means that a permanent card has been put into that player's graveyard from anywhere this turn.")
        ruling("2023-11-10", "A permanent card is an artifact, battle, creature, enchantment, land, or planeswalker card. Tokens are not cards, and while tokens are put into the graveyard before ceasing to exist, that action doesn't count as a player having descended.")
    }
}
