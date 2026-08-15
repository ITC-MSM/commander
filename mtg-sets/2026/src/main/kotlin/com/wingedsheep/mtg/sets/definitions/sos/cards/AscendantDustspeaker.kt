package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ascendant Dustspeaker
 * {4}{W}
 * Creature — Orc Cleric
 * 3/4
 *
 * Flying
 * When this creature enters, put a +1/+1 counter on another target creature you control.
 * At the beginning of combat on your turn, exile up to one target card from a graveyard.
 */
val AscendantDustspeaker = card("Ascendant Dustspeaker") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Orc Cleric"
    oracleText = "Flying\n" +
        "When this creature enters, put a +1/+1 counter on another target creature you control.\n" +
        "At the beginning of combat on your turn, exile up to one target card from a graveyard."
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    // When this creature enters, put a +1/+1 counter on another target creature you control.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "another target creature you control",
            TargetCreature(filter = TargetFilter.OtherCreatureYouControl),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    // At the beginning of combat on your turn, exile up to one target card from a graveyard.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target("target card from a graveyard", TargetObject(optional = true, filter = TargetFilter.CardInGraveyard))
        effect = Effects.Move(t, Zone.EXILE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Josiah \"Jo\" Cameron"
        flavorText = "You can always tell who just left Professor Osgir's lecture on practical restoration."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de3de40b-a7ac-455e-add2-4e451b602d17.jpg?1776000359"
    }
}
