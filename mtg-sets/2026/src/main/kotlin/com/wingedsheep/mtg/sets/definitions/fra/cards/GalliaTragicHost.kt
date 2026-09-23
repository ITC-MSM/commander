package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gallia, Tragic Host — a self-recursion from the graveyard whose cost exiles *another* creature
 * card from that graveyard, so Gallia can't pay for herself (`Costs.ExileAnotherFromGraveyard`).
 * The `fromZone` guard skips the return if she has left the graveyard in response.
 */
val GalliaTragicHost = card("Gallia, Tragic Host") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Zombie Satyr"
    power = 2
    toughness = 1
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "{4}{B}, Exile another creature card from your graveyard: Return this card from your " +
        "graveyard to the battlefield tapped with a +1/+1 counter on her."

    keywords(Keyword.MENACE)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}{B}"),
            Costs.ExileAnotherFromGraveyard(1, GameObjectFilter.Creature),
        )
        activateFromZone = Zone.GRAVEYARD
        effect = Effects.Move(
            EffectTarget.Self,
            Zone.BATTLEFIELD,
            placement = ZonePlacement.Tapped,
            fromZone = Zone.GRAVEYARD,
            addCounterType = CounterType.PLUS_ONE_PLUS_ONE,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "228"
        artist = "Andrea Piparo"
        flavorText = "The party never ends, but the guests will never arrive."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/498fa810-8522-4020-b773-52ad404c9f65.jpg?1789385697"
        inBooster = false
    }
}
