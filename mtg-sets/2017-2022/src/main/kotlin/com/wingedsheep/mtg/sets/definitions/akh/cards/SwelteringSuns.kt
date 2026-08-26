package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sweltering Suns
 * {1}{R}{R}
 * Sorcery
 * Sweltering Suns deals 3 damage to each creature.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 *
 * "Each creature" is [Effects.ForEachInGroup] over an unrestricted creature [GroupFilter], with the
 * body pointed at [EffectTarget.Self] — the iterated permanent, not the spell's source.
 */
val SwelteringSuns = card("Sweltering Suns") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Sweltering Suns deals 3 damage to each creature.\n" +
            "Cycling {3} ({3}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature),
            Effects.DealDamage(3, EffectTarget.Self)
        )
    }

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "149"
        artist = "Raymond Swanland"
        flavorText = "The Hekma may repel storms and monsters, but nothing holds back the heat of the suns."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f11cd406-c6ae-4018-ae45-4e5577aa82ae.jpg?1783936482"
    }
}
