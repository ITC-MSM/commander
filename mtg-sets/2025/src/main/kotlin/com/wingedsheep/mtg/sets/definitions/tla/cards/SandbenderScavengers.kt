package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sandbender Scavengers
 * {W}{B}
 * Creature — Human Rogue
 * 1/1
 *
 * Whenever you sacrifice another permanent, put a +1/+1 counter on this creature.
 * When this creature dies, you may exile it. When you do, return target creature card with
 * mana value less than or equal to this creature's power from your graveyard to the battlefield.
 *
 * Ability 1: modeled with the batching "you sacrifice one or more permanents" trigger (ANY
 * binding) over any permanent. The engine's sacrifice-batch detector only fires ANY-binding
 * triggers; an exact OTHER ("another") binding isn't supported there. The only behavioral gap is
 * the case where this creature is itself sacrificed — the trigger fires and adds a +1/+1 counter
 * to it, but it has already left the battlefield, so that counter is a no-op.
 *
 * Ability 2: a reflexive trigger (Boilerbilges Ripper shape). The "you may exile it" action moves
 * this card from the graveyard to exile; "when you do" then reanimates a target creature card.
 * "This creature's power" is read off the source via last-known information once it is in exile
 * (CR 608.2h), expressed as the [DynamicAmounts.sourcePower] mana-value-vs-power target predicate
 * (Astelli Reclaimer shape). Because the source has been exiled, it can no longer be chosen as the
 * reanimation target.
 */
val SandbenderScavengers = card("Sandbender Scavengers") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Human Rogue"
    power = 1
    toughness = 1
    oracleText = "Whenever you sacrifice another permanent, put a +1/+1 counter on this creature.\n" +
        "When this creature dies, you may exile it. When you do, return target creature card with " +
        "mana value less than or equal to this creature's power from your graveyard to the battlefield."

    // Whenever you sacrifice another permanent, put a +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(GameObjectFilter.Permanent)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you sacrifice another permanent, put a +1/+1 counter on this creature."
    }

    // When this creature dies, you may exile it. When you do, return target creature card with
    // mana value less than or equal to this creature's power from your graveyard to the battlefield.
    triggeredAbility {
        trigger = Triggers.Dies
        effect = ReflexiveTriggerEffect(
            action = Effects.Exile(EffectTarget.Self),
            optional = true,
            reflexiveEffect = Effects.PutOntoBattlefield(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(
                TargetObject(
                    filter = TargetFilter(
                        baseFilter = GameObjectFilter.Creature.ownedByYou()
                            .manaValueAtMostDynamic(DynamicAmounts.sourcePower()),
                        zone = Zone.GRAVEYARD
                    )
                )
            ),
            descriptionOverride = "You may exile this creature. When you do, return target creature " +
                "card with mana value less than or equal to this creature's power from your " +
                "graveyard to the battlefield."
        )
        description = "When this creature dies, you may exile it. When you do, return target creature " +
            "card with mana value less than or equal to this creature's power from your graveyard " +
            "to the battlefield."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "239"
        artist = "Alexander Forssberg"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9bfe0f7c-6dac-41d4-a013-109445158a5e.jpg?1764121769"
    }
}
