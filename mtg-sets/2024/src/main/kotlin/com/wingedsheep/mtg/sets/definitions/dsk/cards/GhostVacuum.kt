package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ghost Vacuum
 * {1}
 * Artifact
 *
 * {T}: Exile target card from a graveyard.
 * {6}, {T}, Sacrifice this artifact: Put each creature card exiled with this artifact onto the
 * battlefield under your control with a flying counter on it. Each of them is a 1/1 Spirit in
 * addition to its other types. Activate only as a sorcery.
 *
 * First ability: exiles a target card from any graveyard, linked to this artifact
 * ([MoveToZoneEffect.linkToSource]) so the second ability can find the pile it built up.
 *
 * Second ability follows the proven [com.wingedsheep.mtg.sets.definitions.eoe.cards.PinnacleStarcage]
 * shape: the linked-exile pile is gathered (filtered to creature cards via the move's [filter]) and
 * the artifact is sacrificed *within the resolving effect* via [Effects.SacrificeTarget] rather than
 * as a paid cost. This is functionally identical to "Sacrifice this artifact" as a cost — nothing
 * references the artifact after the gather, and gathering before the sacrifice keeps the
 * `LinkedExileComponent` available (CR 400.7) — while reusing the battle-tested pattern.
 *
 * The returned creatures enter under your control with a flying counter ([MoveCollectionEffect.addCounterType]
 * = [CounterType.FLYING], which the projector maps to the flying keyword), then a per-permanent
 * iteration over the moved collection ([IterationSpace.Collection]) sets each one's base power and
 * toughness to 1/1 and adds the Spirit creature type, both for [Duration.Permanent] (the lasting
 * "is a 1/1 Spirit in addition to its other types" effect, which persists after the artifact is gone).
 */
val GhostVacuum = card("Ghost Vacuum") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Exile target card from a graveyard.\n" +
        "{6}, {T}, Sacrifice this artifact: Put each creature card exiled with this artifact onto " +
        "the battlefield under your control with a flying counter on it. Each of them is a 1/1 " +
        "Spirit in addition to its other types. Activate only as a sorcery."

    // {T}: Exile target card from a graveyard (linked to this artifact).
    activatedAbility {
        cost = Costs.Tap
        val t = target(
            "target card in a graveyard",
            TargetObject(filter = TargetFilter.CardInGraveyard),
        )
        effect = Effects.Move(t, Zone.EXILE, linkToSource = true)
        description = "Exile target card from a graveyard."
    }

    // {6}, {T}, Sacrifice this artifact: Put each creature card exiled with this artifact onto the
    // battlefield under your control with a flying counter; each becomes a 1/1 Spirit in addition
    // to its other types. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        effect = Effects.Composite(
            // Gather the linked-exile pile before the sacrifice (CR 400.7).
            GatherCardsEffect(
                source = CardSource.FromLinkedExile(),
                storeAs = "exiledPile",
            ),
            // Put each creature card from the pile onto the battlefield under your control with a
            // flying counter; non-creature cards stay exiled (filter).
            MoveCollectionEffect(
                from = "exiledPile",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                filter = GameObjectFilter.Creature,
                addCounterType = CounterType.FLYING,
                storeMovedAs = "spirits",
            ),
            // Each of them is a 1/1 Spirit in addition to its other types (lasting).
            ForEachEffect(
                space = IterationSpace.Collection("spirits"),
                body = Effects.Composite(
                    Effects.SetBasePowerAndToughness(
                        power = 1,
                        toughness = 1,
                        target = EffectTarget.Self,
                        duration = com.wingedsheep.sdk.scripting.Duration.Permanent,
                    ),
                    Effects.AddCreatureType("Spirit", EffectTarget.Self),
                ),
            ),
            // Sacrifice this artifact (modeled in-effect; see KDoc).
            Effects.SacrificeTarget(EffectTarget.Self),
        )
        description = "Put each creature card exiled with this artifact onto the battlefield under " +
            "your control with a flying counter on it. Each of them is a 1/1 Spirit in addition to " +
            "its other types. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "248"
        artist = "David Szabo"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ac39c01-127f-4471-bc74-11a90c48e306.jpg?1726286797"
    }
}
