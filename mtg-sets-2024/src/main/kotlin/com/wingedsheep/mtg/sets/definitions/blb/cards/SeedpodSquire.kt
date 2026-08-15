package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Seedpod Squire
 * {3}{W/U}
 * Creature — Bird Scout
 * 3/3
 * Flying
 * Whenever this creature attacks, target creature you control without flying
 * gets +1/+1 until end of turn.
 */
val SeedpodSquire = card("Seedpod Squire") {
    manaCost = "{3}{W/U}"
    colorIdentity = "WU"
    typeLine = "Creature — Bird Scout"
    oracleText = "Flying\nWhenever this creature attacks, target creature you control without flying gets +1/+1 until end of turn."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "creature you control without flying",
            TargetCreature(filter = TargetFilter.CreatureYouControl.withoutKeyword(Keyword.FLYING))
        )
        effect = Effects.ModifyStats(1, 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Christina Kraus"
        flavorText = "\"If you think he's loud now, wait until he starts the battle song.\"\n—Clement, pessimistic adventurer"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3684577-51ce-490e-9b59-b19c733be466.jpg?1721427187"
    }
}
