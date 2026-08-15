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
 * Sky Weaver
 * {1}{U}
 * Creature — Metathran Wizard
 * 2/1
 * {2}: Target white or black creature gains flying until end of turn.
 */
val SkyWeaver = card("Sky Weaver") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Metathran Wizard"
    power = 2
    toughness = 1
    oracleText = "{2}: Target white or black creature gains flying until end of turn. " +
        "(It can't be blocked except by creatures with flying or reach.)"

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withAnyColor(Color.WHITE, Color.BLACK)))
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, target = t)
        description = "{2}: Target white or black creature gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "74"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04974146-42a8-4f10-b443-67bfeaa54d5d.jpg?1562895878"
    }
}
