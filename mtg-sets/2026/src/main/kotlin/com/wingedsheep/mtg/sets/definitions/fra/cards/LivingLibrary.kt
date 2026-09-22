package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val LivingLibrary = card("Living Library") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Book Illusion"
    oracleText = "{6}, Sacrifice this creature: Choose target creature or planeswalker an opponent controls. Its owner shuffles it into their library."
    power = 0
    toughness = 4

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.SacrificeSelf)
        val permanent = target(
            "creature or planeswalker",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls()))
        )
        effect = Effects.ShuffleIntoLibrary(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Jarel Threat"
        flavorText = "\"Too many lives have been cut short, too many of their futures untold. I can rewrite them. I can give them a voice.\"\n—The Theorist, Jace Beleren"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d4a8e5f-0024-4da3-a2f5-edb48b12e733.jpg?1789556949"
        inBooster = false
    }
}
