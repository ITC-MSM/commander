package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Long Feng, Grand Secretariat
 * {1}{B/G}{B/G}
 * Legendary Creature — Human Advisor
 * 2/3
 *
 * Whenever another creature you control or a land you control is put into a graveyard from the
 * battlefield, put a +1/+1 counter on target creature you control.
 *
 * Modeled as a per-permanent [Triggers.leavesBattlefield] death trigger scoped to your battlefield:
 * filter [GameObjectFilter.CreatureOrLand] restricted to your control, destination
 * [Zone.GRAVEYARD], and [TriggerBinding.OTHER] for the "another" exclusion (Long Feng's own death
 * doesn't trigger). A board wipe fires it once per qualifying permanent. The counter goes on a
 * [TargetPermanent] creature you control — no self-exclusion, so Long Feng may target itself.
 */
val LongFengGrandSecretariat = card("Long Feng, Grand Secretariat") {
    manaCost = "{1}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Human Advisor"
    power = 2
    toughness = 3
    oracleText = "Whenever another creature you control or a land you control is put into a " +
        "graveyard from the battlefield, put a +1/+1 counter on target creature you control."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.CreatureOrLand.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        val target = target(
            "target creature you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature.youControl()))
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, target)
        description = "Whenever another creature you control or a land you control is put into a " +
            "graveyard from the battlefield, put a +1/+1 counter on target creature you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Robin Har"
        flavorText = "\"In silencing talk of conflict, Ba Sing Se remains a peaceful, orderly utopia.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3eb92fe-bc59-4472-9028-f368bd015609.jpg?1764121724"
    }
}
