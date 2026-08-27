package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghost Tactician
 * {4}{W}
 * Creature — Spirit Spellshaper
 * 2/5
 * {W}, {T}, Discard a card: Creatures you control get +1/+0 until end of turn.
 *
 * A group pump is [Effects.ForEachInGroup] over the snapshotted group; `EffectTarget.Self` inside
 * the body is the creature being iterated, not the Tactician.
 */
val GhostTactician = card("Ghost Tactician") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Spellshaper"
    power = 2
    toughness = 5
    oracleText = "{W}, {T}, Discard a card: Creatures you control get +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap, Costs.DiscardCard)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 0, EffectTarget.Self)
        )
        description = "{W}, {T}, Discard a card: Creatures you control get +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Its ethereal hand confers a lifetime of experience with combat and steel."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56ab366c-085b-4b13-b5ad-918965c34d22.jpg"
    }
}
