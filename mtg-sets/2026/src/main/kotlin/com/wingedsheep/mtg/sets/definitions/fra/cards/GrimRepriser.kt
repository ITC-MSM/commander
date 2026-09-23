package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val GrimRepriser = card("Grim Repriser") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Zombie Bard"
    power = 2
    toughness = 2
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "{B}{R}: Return this card from your graveyard to the battlefield with a finality counter on it. " +
        "Activate only if an opponent has been dealt noncombat damage this turn. " +
        "(If a creature with a finality counter on it would die, exile it instead.)"

    keywords(Keyword.PROWESS)

    activatedAbility {
        cost = Costs.Mana("{B}{R}")
        effect = Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            .then(Effects.AddCounters(Counters.FINALITY, 1, EffectTarget.Self))
        activateFromZone = Zone.GRAVEYARD
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(Conditions.OpponentWasDealtNoncombatDamageThisTurn))
        description = "{B}{R}: Return this card from your graveyard to the battlefield with a finality counter on it. " +
            "Activate only if an opponent has been dealt noncombat damage this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "136"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/8295c48c-b4dd-4bc1-a206-04cf12b79bbd.jpg?1789470746"
        inBooster = false
    }
}
