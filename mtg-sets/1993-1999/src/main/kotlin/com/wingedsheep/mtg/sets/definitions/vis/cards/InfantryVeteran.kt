package com.wingedsheep.mtg.sets.definitions.vis.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Infantry Veteran
 * {W}
 * Creature — Human Soldier
 * 1/1
 * {T}: Target attacking creature gets +1/+1 until end of turn.
 */
val InfantryVeteran = card("Infantry Veteran") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "{T}: Target attacking creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target attacking creature", TargetCreature(filter = TargetFilter.AttackingCreature))
        effect = Effects.ModifyStats(1, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Christopher Rush"
        flavorText = "\"The true dishonor for a soldier is surviving the war.\" —Telim'Tor"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0350470b-feea-4e15-bdf0-850b71dbeea6.jpg?1783947006"
        ruling("2010-08-15", "An “attacking creature” is one that has been declared as an attacker this combat, or one that was put onto the battlefield attacking this combat. Unless that creature leaves combat, it continues to be an attacking creature through the end of combat step, even if the player it was attacking has left the game, or the planeswalker it was attacking has left combat.")
    }
}
