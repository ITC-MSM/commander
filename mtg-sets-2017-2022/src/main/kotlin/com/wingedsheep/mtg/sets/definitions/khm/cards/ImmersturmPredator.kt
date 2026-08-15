package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Immersturm Predator
 * {2}{B}{R}
 * Creature — Vampire Dragon
 * 3/3
 *
 * Flying
 * Whenever this creature becomes tapped, exile up to one target card from a graveyard and
 * put a +1/+1 counter on this creature.
 * Sacrifice another creature: This creature gains indestructible until end of turn. Tap it.
 *
 * The "becomes tapped" trigger fires for *any* tapping — attacking, an opponent's tap effect,
 * or the sacrifice ability's own "Tap it" — so sacrificing a creature both protects Immersturm
 * Predator and grows it (Rule 603.2 puts the triggered ability on the stack after the ability
 * that tapped it resolves). The graveyard exile is "up to one target", so it can resolve with
 * no target chosen; the +1/+1 counter is always placed regardless.
 */
val ImmersturmPredator = card("Immersturm Predator") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Vampire Dragon"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever this creature becomes tapped, exile up to one target card from a graveyard and put a +1/+1 counter on this creature.\n" +
        "Sacrifice another creature: This creature gains indestructible until end of turn. Tap it."

    keywords(Keyword.FLYING)

    // Whenever this creature becomes tapped, exile up to one target card from a graveyard and
    // put a +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.BecomesTapped
        val exiled = target("target card in a graveyard", TargetObject(optional = true, filter = TargetFilter.CardInGraveyard))
        effect = Effects.Composite(
            Effects.Move(exiled, Zone.EXILE),
            Effects.AddCounters("+1/+1", 1, EffectTarget.Self)
        )
    }

    // Sacrifice another creature: This creature gains indestructible until end of turn. Tap it.
    activatedAbility {
        cost = Costs.SacrificeAnother(GameObjectFilter.Creature)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self),
            Effects.Tap(EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "214"
        artist = "Nicholas Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d83d2d9-b9d0-47f5-989b-f2c726401ade.jpg?1783928197"
    }
}
