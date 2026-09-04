package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deathless Angel
 * {4}{W}{W}
 * Creature — Angel
 * 5 / 7
 *
 * Flying
 * {W}{W}: Target creature gains indestructible until end of turn.
 *
 * Modeling notes:
 *  - `Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, target)` with the facade's default
 *    `Duration.EndOfTurn`, matching the printed "until end of turn". A keyword grant, not a
 *    damage-prevention shield: the creature shrugs off lethal damage and "destroy" but still dies
 *    to 0 toughness or to sacrifice.
 *  - `Targets.Creature` — the printed line says "target creature", with no "you control"
 *    restriction, so an opponent's creature is a legal target too.
 *  - The ability has no tap symbol and no other cost, so it is `Costs.Mana("{W}{W}")` alone and can
 *    be activated repeatedly, including the turn this enters.
 */
val DeathlessAngel = card("Deathless Angel") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 5
    toughness = 7
    oracleText = "Flying\n" +
            "{W}{W}: Target creature gains indestructible until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{W}{W}")
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Johann Bodin"
        flavorText = "\"I should have died that day, but I suffered not a scratch. I awoke in a lake of blood, none of it apparently my own.\"\n—*The War Diaries*"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/049fb314-184c-4411-9035-f04215659056.jpg?1783942009"
    }
}
