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
 * Rage Weaver
 * {1}{R}
 * Creature — Human Wizard
 * 2/1
 * {2}: Target black or green creature gains haste until end of turn.
 */
val RageWeaver = card("Rage Weaver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "{2}: Target black or green creature gains haste until end of turn. " +
        "(It can attack and {T} this turn.)"

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withAnyColor(Color.BLACK, Color.GREEN)))
        )
        effect = Effects.GrantKeyword(Keyword.HASTE, target = t)
        description = "{2}: Target black or green creature gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "John Matson"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a654295d-b63c-4025-bf36-899023a8ba1d.jpg?1562928619"
    }
}
