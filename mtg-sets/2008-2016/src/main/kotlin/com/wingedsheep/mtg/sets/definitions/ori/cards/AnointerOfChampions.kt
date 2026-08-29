package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Anointer of Champions
 * {W}
 * Creature — Human Cleric
 * 1/1
 *
 * {T}: Target attacking creature gets +1/+1 until end of turn.
 */
val AnointerOfChampions = card("Anointer of Champions") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "{T}: Target attacking creature gets +1/+1 until end of turn."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        val t = target("target attacking creature", TargetCreature(filter = TargetFilter.Creature.attacking()))
        effect = Effects.ModifyStats(1, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "3"
        artist = "Anna Steinbauer"
        flavorText = "\"Arise. You have been anointed by the light. Go forth and fight without fear, for you shall be victorious.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/6/36d060c9-8385-40af-87eb-b4688ebb8e7c.jpg"
    }
}
