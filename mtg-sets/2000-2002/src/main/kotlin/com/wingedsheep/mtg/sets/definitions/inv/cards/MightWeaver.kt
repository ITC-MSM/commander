package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Might Weaver
 * {1}{G}
 * Creature — Human Wizard
 * 2/1
 * {2}: Target red or white creature gains trample until end of turn.
 */
val MightWeaver = card("Might Weaver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "{2}: Target red or white creature gains trample until end of turn. " +
        "(It can deal excess combat damage to the player or planeswalker it's attacking.)"

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withAnyColor(Color.RED, Color.WHITE)))
        )
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, target = t)
        description = "{2}: Target red or white creature gains trample until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Larry Elmore"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/032a4ec7-82ce-4ea0-b0dd-ebc40823a014.jpg?1562895607"
    }
}
