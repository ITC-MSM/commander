package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Ride the Shoopuf — Final Fantasy #197
 * {1}{G} · Enchantment · Uncommon
 *
 * Landfall — Whenever a land you control enters, put a +1/+1 counter on target creature you control.
 * {5}{G}{G}: This enchantment becomes a 7/7 Beast creature in addition to its other types.
 *
 * The landfall trigger uses [Triggers.LandYouControlEnters] (fires for any land you control entering,
 * not just lands you play) and targets a creature you control via [TargetFilter.CreatureYouControl].
 *
 * The animation is a permanent [Effects.BecomeCreature] on the source: it adds the CREATURE type
 * (no `removeTypes`, so "in addition to its other types" — it stays an Enchantment), sets base 7/7,
 * and adds the Beast subtype. `duration = Duration.Permanent` — the ability has no duration, so it
 * remains a creature indefinitely (mirrors Emergent Haunting).
 */
val RideTheShoopuf = card("Ride the Shoopuf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Landfall — Whenever a land you control enters, put a +1/+1 counter on target creature you control.\n" +
        "{5}{G}{G}: This enchantment becomes a 7/7 Beast creature in addition to its other types."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        val t = target("target creature you control", TargetCreature(filter = TargetFilter.CreatureYouControl))
        effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t)
        description = "Landfall — Whenever a land you control enters, put a +1/+1 counter on target creature you control."
    }

    activatedAbility {
        cost = Costs.Mana("{5}{G}{G}")
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = 7,
            toughness = 7,
            creatureTypes = setOf("Beast"),
            duration = Duration.Permanent
        )
        description = "{5}{G}{G}: This enchantment becomes a 7/7 Beast creature in addition to its other types."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Leonardo Santanna"
        flavorText = "\"Ride ze shoopuf? All aboards!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19ad36d6-8bf4-490c-9980-b98a470af892.jpg?1748706498"
    }
}
