package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Calamity of Cinders
 * {5}{R}{R}
 * Sorcery
 *
 * Convoke
 * Calamity of Cinders deals 6 damage to each untapped creature.
 *
 * "Each untapped creature" is a group sweep, not a target: [Effects.ForEachInGroup] over
 * `Creature.untapped()`, where `EffectTarget.Self` inside the body rebinds to each member.
 */
val CalamityOfCinders = card("Calamity of Cinders") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Calamity of Cinders deals 6 damage to each untapped creature."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.untapped()),
            Effects.DealDamage(6, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "23"
        artist = "Xabi Gaztelua"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97c72d4b-a5d4-4f68-bbcd-b7ed0326fc1c.jpg?1783910732"
    }
}
