package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Uchbenbak, the Great Mistake
 * {3}{U}{B}
 * Legendary Creature — Skeleton Horror
 * 6/4
 *
 * Vigilance, menace
 * Descend 8 — {4}{U}{B}: Return this card from your graveyard to the battlefield with a finality
 * counter on it. Activate only if there are eight or more permanent cards in your graveyard and
 * only as a sorcery. (If a creature with a finality counter on it would die, exile it instead.)
 *
 * The Descend 8 ability is a graveyard-zone activated ability (`activateFromZone = Zone.GRAVEYARD`),
 * gated on eight-or-more permanent cards in your graveyard
 * ([Conditions.CardsInGraveyardMatchingAtLeast] with [GameObjectFilter.Permanent]) and restricted
 * to sorcery speed ([TimingRule.SorcerySpeed]). It returns this card to the battlefield and drops a
 * finality counter on it, mirroring Balustrade Wurm's Delirium reanimation (Move Self
 * GRAVEYARD→BATTLEFIELD, then [AddCountersEffect] on Self). The finality counter's die-replacement
 * (exile instead) is the engine-wide behavior of [Counters.FINALITY].
 */
val UchbenbakTheGreatMistake = card("Uchbenbak, the Great Mistake") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Skeleton Horror"
    power = 6
    toughness = 4
    oracleText = "Vigilance, menace\n" +
        "Descend 8 — {4}{U}{B}: Return this card from your graveyard to the battlefield with a " +
        "finality counter on it. Activate only if there are eight or more permanent cards in your " +
        "graveyard and only as a sorcery. (If a creature with a finality counter on it would die, " +
        "exile it instead.)"

    keywords(Keyword.VIGILANCE, Keyword.MENACE)

    activatedAbility {
        cost = Costs.Mana("{4}{U}{B}")
        activateFromZone = Zone.GRAVEYARD
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent)
            )
        )
        effect = Effects.Composite(
            Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
            AddCountersEffect(counterType = Counters.FINALITY, count = 1, target = EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "242"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a062202c-f9fb-4dd6-989a-c3083644f1c0.jpg?1782694417"
    }
}
