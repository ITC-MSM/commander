package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Incandescent Aria
 * {R}{G}{W}
 * Sorcery
 * Incandescent Aria deals 3 damage to each nontoken creature.
 *
 * Slagstorm's sweep with one predicate added: "nontoken" is a card predicate on the group's
 * base filter ([GameObjectFilter.nontoken]), so tokens are simply outside the iterated group.
 */
val IncandescentAria = card("Incandescent Aria") {
    manaCost = "{R}{G}{W}"
    colorIdentity = "GRW"
    typeLine = "Sorcery"
    oracleText = "Incandescent Aria deals 3 damage to each nontoken creature."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.nontoken()),
            Effects.DealDamage(3, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "Randy Gallegos"
        flavorText = "As the assassins converged on Kitt Kanto, she struck a high note and held it, unbroken, for nearly a minute. When the room fell silent, she was surrounded only by rose petals falling gently to the floor."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77e2ed9e-ee1d-440a-94b4-d4b17d30b800.jpg?1783923082"
    }
}
