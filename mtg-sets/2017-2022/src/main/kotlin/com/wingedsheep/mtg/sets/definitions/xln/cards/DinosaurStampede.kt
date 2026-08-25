package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dinosaur Stampede
 * {2}{R}
 * Instant
 *
 * Attacking creatures get +2/+0 until end of turn. Dinosaurs you control gain trample until end
 * of turn.
 */
val DinosaurStampede = card("Dinosaur Stampede") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Attacking creatures get +2/+0 until end of turn. Dinosaurs you control gain " +
        "trample until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.attacking()),
            Effects.ModifyStats(2, 0, EffectTarget.Self)
        ) then Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Permanent.withSubtype("Dinosaur").youControl()),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "140"
        artist = "Bram Sels"
        flavorText = "If you're in the way of a ceratops charge and you're made of mere flesh and bone, then you're not really in the way."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a31bda74-8455-45ab-8142-65fbde2f39c3.jpg"
    }
}
