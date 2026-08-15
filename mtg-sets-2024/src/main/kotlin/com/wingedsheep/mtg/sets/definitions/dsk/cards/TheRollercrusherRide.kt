package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DoubleDamage
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.SourceFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Rollercrusher Ride — Duskmourn: House of Horror #155
 * {X}{2}{R} · Legendary Enchantment
 *
 * Delirium — If a source you control would deal noncombat damage to a permanent or player while
 * there are four or more card types among cards in your graveyard, it deals double that damage
 * instead.
 * When The Rollercrusher Ride enters, it deals X damage to each of up to X target creatures.
 *
 * **Delirium doubling** — a damage-amount replacement ([DoubleDamage], CR 616) gated on the new
 * `restrictions` list: it only doubles while [Conditions.Delirium] (four or more card types among
 * cards in your graveyard) holds, checked each time the source you control would deal *noncombat*
 * damage (`DamageType.NonCombat`) to any permanent or player. The source filter is
 * `GameObjectFilter.Any.youControl()` — "a source you control", not just creatures. The doubled
 * damage stays attributed to the original source (printed ruling 2024-09-20), which the engine's
 * amplification pass already does (it scales the amount in place without re-attributing).
 *
 * **ETB** — [DynamicAmount.CastX] reads the `{X}` paid to cast the enchantment and rides it onto
 * the permanent, so the enters trigger targets up to X creatures
 * (`TargetCreature.dynamicMaxCount = CastX`, snapshotted when the trigger goes on the stack) and
 * deals X to each via [ForEachTargetEffect] + [DealDamageEffect]. Leaving `damageSource` null
 * attributes the damage to the trigger's source (The Rollercrusher Ride), matching "it deals X
 * damage". X = 0 puts the trigger on the stack with no targets and deals nothing.
 *
 * The two abilities interact as printed: an X-damage ETB during delirium is itself noncombat
 * damage from a source you control, so each instance is doubled before being dealt.
 */
val TheRollercrusherRide = card("The Rollercrusher Ride") {
    manaCost = "{X}{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Enchantment"
    oracleText = "Delirium — If a source you control would deal noncombat damage to a permanent or " +
        "player while there are four or more card types among cards in your graveyard, it deals " +
        "double that damage instead.\n" +
        "When The Rollercrusher Ride enters, it deals X damage to each of up to X target creatures."

    // Delirium — double noncombat damage from sources you control while you have delirium.
    replacementEffect(
        DoubleDamage(
            restrictions = listOf(Conditions.Delirium(4)),
            appliesTo = EventPattern.DamageEvent(
                source = SourceFilter.Matching(GameObjectFilter.Any.youControl()),
                damageType = DamageType.NonCombat,
            ),
        )
    )

    // When The Rollercrusher Ride enters, it deals X damage to each of up to X target creatures.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to X target creatures",
            TargetCreature(optional = true, dynamicMaxCount = DynamicAmount.CastX),
        )
        effect = ForEachTargetEffect(
            listOf(DealDamageEffect(DynamicAmount.CastX, EffectTarget.ContextTarget(0)))
        )
        description = "When The Rollercrusher Ride enters, it deals X damage to each of up to X " +
            "target creatures."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "155"
        artist = "Deruchenko Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70019956-fca1-4090-b3b6-6a963528e05b.jpg?1726286431"

        ruling(
            "2024-09-20",
            "The damage is dealt by the same source as the original source of damage. The doubled " +
                "damage isn't dealt by The Rollercrusher Ride unless it was the original source of damage."
        )
        ruling(
            "2024-09-20",
            "If damage dealt by a source you control is being divided among multiple permanents " +
                "and/or players while you have delirium, that damage is divided before doubling."
        )
    }
}
