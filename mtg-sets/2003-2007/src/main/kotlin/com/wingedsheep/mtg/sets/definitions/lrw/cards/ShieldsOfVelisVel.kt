package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shields of Velis Vel
 * {W}
 * Kindred Instant — Shapeshifter
 *
 * Changeling (This card is every creature type.)
 * Creatures target player controls get +0/+1 and gain all creature types until end of turn.
 *
 * The toughness-side twin of Ego Erasure, and built the same way: the player is the only
 * *target*, and the creatures are a group resolved on resolution, so `targetPlayerControls`
 * binds the group's controller predicate to that target rather than to the spell's controller.
 * Both riders land on each member with [EffectTarget.Self] inside the iteration, so a creature
 * that leaves mid-resolution simply drops out.
 *
 * "Gains all creature types" is modelled by granting Changeling — the engine expands that
 * keyword into every creature type (CR 702.73), the same shape Blades of Velis Vel uses.
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val ShieldsOfVelisVel = card("Shields of Velis Vel") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Kindred Instant — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Creatures target player controls get +0/+1 and gain all creature types until end of turn."

    keywords(Keyword.CHANGELING)

    spell {
        val player = target("target player", Targets.Player)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.targetPlayerControls(player)),
            Effects.Composite(
                Effects.ModifyStats(0, 1, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.CHANGELING, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Ralph Horsley"
        flavorText = "Changelings can alter shape based on what the beings around them desire most."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f550f44f-8b8f-4c1d-a583-9fd986d3061c.jpg?1783942908"
    }
}
