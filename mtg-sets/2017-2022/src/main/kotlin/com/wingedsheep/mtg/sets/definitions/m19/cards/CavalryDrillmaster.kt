package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cavalry Drillmaster
 * {1}{W}
 * Creature — Human Knight
 * 2/1
 * When this creature enters, target creature gets +2/+0 and gains first strike until end of turn.
 *
 * One ETB trigger with one target: the pump and the keyword grant both land on the same chosen
 * creature and both default to [com.wingedsheep.sdk.scripting.Duration.EndOfTurn]. Not restricted
 * to creatures you control — any creature is a legal target.
 */
val CavalryDrillmaster = card("Cavalry Drillmaster") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, target creature gets +2/+0 and gains first strike until end of turn. (It deals combat damage before creatures without first strike.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, creature)
            .then(Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature))
        description = "When this creature enters, target creature gets +2/+0 and gains first strike until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62d2e929-7ae3-4560-9cfa-53b89c8a6016.jpg?1783934610"
    }
}
