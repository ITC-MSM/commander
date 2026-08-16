package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Selfless Spirit
 * {1}{W}
 * Creature — Spirit Cleric
 * 2/1
 * Flying
 * Sacrifice this creature: Creatures you control gain indestructible until end of turn.
 */
val SelflessSpirit = card("Selfless Spirit") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Cleric"
    power = 2
    toughness = 1
    oracleText = "Flying\nSacrifice this creature: Creatures you control gain indestructible until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "40"
        artist = "Seb McKinnon"
        flavorText = "\"There is always more to give.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4624976-3773-4a1e-b725-5f6efce147a5.jpg?1783937510"

        ruling("2016-07-13", "The set of creatures affected by Selfless Spirit's last ability is determined as the ability resolves. Creatures you begin to control later in the turn won't gain indestructible.")
    }
}
