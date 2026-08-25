package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventionScope

/**
 * Lady Evangela
 * {W}{U}{B}
 * Legendary Creature — Human Cleric
 * 1/2
 *
 * {W}{B}, {T}: Prevent all combat damage that would be dealt by target creature this turn.
 */
val LadyEvangela = card("Lady Evangela") {
    manaCost = "{W}{U}{B}"
    colorIdentity = "BUW"
    typeLine = "Legendary Creature — Human Cleric"
    power = 1
    toughness = 2
    oracleText = "{W}{B}, {T}: Prevent all combat damage that would be dealt by target creature this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}{B}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.PreventAllDamageDealtBy(creature, scope = PreventionScope.CombatOnly)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "240"
        artist = "Mark Poole"
        flavorText = "\"When milady was young, the sight of a rainbow would fill her soul with peace. As she " +
            "grew, she learned to share her rapture with others.\" —*Lady Gabriella*"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3e122e9-ffa3-48dd-94d6-8f2886668e59.jpg?1783948036"
    }
}
