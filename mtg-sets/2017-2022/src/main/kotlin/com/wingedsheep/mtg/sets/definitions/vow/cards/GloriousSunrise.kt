package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GrantActivatedAbilityEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Glorious Sunrise — Innistrad: Crimson Vow #200
 * {3}{G}{G} · Enchantment · Rare
 * Artist: Andreas Zafiratos
 *
 * At the beginning of combat on your turn, choose one —
 * • Creatures you control get +1/+1 and gain trample until end of turn.
 * • Target land gains "{T}: Add {G}{G}{G}" until end of turn.
 * • Draw a card if you control a creature with power 3 or greater.
 * • You gain 3 life.
 *
 * A [ModalEffect.chooseOne] inside a [Triggers.BeginCombat] triggered ability (same shape as
 * Manifold Mouse's combat modal). Mode breakdown:
 *  1. `noTarget` — two `ForEachInGroup` passes over `Creature.youControl()`, one [ModifyStatsEffect]
 *     (+1/+1) and one [GrantKeywordEffect] (trample), both until end of turn (Kamahl, Fist of Krosa
 *     overrun pattern).
 *  2. `withTarget` — a [TargetPermanent] land; [GrantActivatedAbilityEffect] grants a `{T}: Add {G}{G}{G}`
 *     activated ability until end of turn (Run Wild's grant pattern, land-typed target).
 *  3. `noTarget` — a [ConditionalEffect] gated on `Conditions.YouControl(Creature.powerAtLeast(3))`;
 *     draws a card only if the condition holds (no draw otherwise, matching "Draw a card if …").
 *  4. `noTarget` — plain gain 3 life.
 *
 * Mode 2's granted ability produces mana as three separate {G} pips (AddMana(GREEN, 3)) — the
 * printed reminder-free "{T}: Add {G}{G}{G}".
 */
val GloriousSunrise = card("Glorious Sunrise") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "At the beginning of combat on your turn, choose one —\n" +
        "• Creatures you control get +1/+1 and gain trample until end of turn.\n" +
        "• Target land gains \"{T}: Add {G}{G}{G}\" until end of turn.\n" +
        "• Draw a card if you control a creature with power 3 or greater.\n" +
        "• You gain 3 life."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.Composite(
                    Effects.ForEachInGroup(
                        filter = GroupFilter(GameObjectFilter.Creature.youControl()),
                        effect = ModifyStatsEffect(1, 1, EffectTarget.Self)
                    ),
                    Effects.ForEachInGroup(
                        filter = GroupFilter(GameObjectFilter.Creature.youControl()),
                        effect = GrantKeywordEffect(Keyword.TRAMPLE, EffectTarget.Self)
                    )
                ),
                "Creatures you control get +1/+1 and gain trample until end of turn"
            ),
            Mode.withTarget(
                effect = GrantActivatedAbilityEffect(
                    ability = ActivatedAbility(
                        id = AbilityId.generate(),
                        cost = com.wingedsheep.sdk.scripting.AbilityCost.Tap,
                        effect = Effects.AddMana(Color.GREEN, 3),
                        isManaAbility = true
                    ),
                    target = EffectTarget.ContextTarget(0)
                ),
                target = TargetPermanent(filter = TargetFilter.Land),
                description = "Target land gains \"{T}: Add {G}{G}{G}\" until end of turn"
            ),
            Mode.noTarget(
                ConditionalEffect(
                    condition = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(3)),
                    effect = Effects.DrawCards(1)
                ),
                "Draw a card if you control a creature with power 3 or greater"
            ),
            Mode.noTarget(
                Effects.GainLife(3),
                "You gain 3 life"
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "200"
        artist = "Andreas Zafiratos"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2caf73e-c3eb-4fa8-996a-d3d18b2ffaeb.jpg?1783924813"
    }
}
