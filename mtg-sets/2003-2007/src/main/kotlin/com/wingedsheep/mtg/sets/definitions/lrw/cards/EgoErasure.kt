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
 * Ego Erasure
 * {2}{U}
 * Kindred Instant — Shapeshifter
 *
 * Changeling (This card is every creature type.)
 * Creatures target player controls get -2/-0 and lose all creature types until end of turn.
 *
 * The player is the only *target*; the creatures are a group resolved when the spell resolves,
 * so a creature that arrives after the spell was cast is still hit. `targetPlayerControls` binds
 * the group's controller predicate to that target rather than to the spell's controller.
 *
 * Both riders land on each member with [EffectTarget.Self] inside the iteration — the same shape
 * Surge of Thoughtweft uses — so a creature that leaves mid-resolution simply drops out.
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val EgoErasure = card("Ego Erasure") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Kindred Instant — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Creatures target player controls get -2/-0 and lose all creature types until end of turn."

    keywords(Keyword.CHANGELING)

    spell {
        val player = target("target player", Targets.Player)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.targetPlayerControls(player)),
            Effects.Composite(
                Effects.ModifyStats(-2, 0, EffectTarget.Self),
                Effects.LoseAllCreatureTypes(EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Steven Belledin"
        flavorText = "When all is taken away, all are equal."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f577831b-2aa2-44a1-bd6b-ef111bc2e211.jpg?1783942904"
    }
}
