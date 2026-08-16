package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ambush Paratrooper
 * {1}{W}
 * Creature — Human Soldier
 * 1/2
 * Flash
 * Flying
 * {5}: Creatures you control get +1/+1 until end of turn.
 */
val AmbushParatrooper = card("Ambush Paratrooper") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "Flash\nFlying\n{5}: Creatures you control get +1/+1 until end of turn."

    keywords(Keyword.FLASH, Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{5}")
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Vladimir Krisetskiy"
        flavorText = "\"It would be folly to fight a dragon engine head-on. Let's hit it from above.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfa00c0e-163d-4f59-b8b9-3ee9143d27bb.jpg?1783920135"
    }
}
