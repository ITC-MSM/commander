package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Law-Rune Enforcer — War of the Spark #20 (canonical printing)
 * {W}
 * Creature — Human Soldier
 * 1/2
 * {1}, {T}: Tap target creature with mana value 2 or greater.
 *
 * The mana-value floor lives on the target filter, so it is re-checked when the ability resolves:
 * a creature whose mana value drops below 2 in response is no longer a legal target. Nothing
 * about the ability restricts the creature's controller — the enforcer taps any big creature,
 * including your own.
 */
val LawRuneEnforcer = card("Law-Rune Enforcer") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "{1}, {T}: Tap target creature with mana value 2 or greater."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val creature = target("target", TargetCreature(filter = TargetFilter.Creature.manaValueAtLeast(2)))
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Eric Deschamps"
        flavorText = "\"See that no one enters or leaves New Prahv today. And notify me at once of any Planeswalker activity.\"\n—Dovin Baan"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3d39238-e21d-4345-84c8-648ef3a66703.jpg"
    }
}
