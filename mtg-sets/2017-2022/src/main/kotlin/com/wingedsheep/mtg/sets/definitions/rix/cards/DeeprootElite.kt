package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Deeproot Elite
 * {1}{G}
 * Creature — Merfolk Warrior
 * 1/1
 * Whenever another Merfolk you control enters, put a +1/+1 counter on target Merfolk you control.
 *
 * The printed "another" is [TriggerBinding.OTHER]; the target is unrestricted beyond
 * "Merfolk you control", so it may be the Merfolk that triggered the ability, or Deeproot Elite
 * itself.
 */
val DeeprootElite = card("Deeproot Elite") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Warrior"
    oracleText = "Whenever another Merfolk you control enters, put a +1/+1 counter on target " +
        "Merfolk you control."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl(),
            TriggerBinding.OTHER
        )
        val merfolk = target(
            "target Merfolk you control",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl()
                )
            )
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, merfolk)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "127"
        artist = "Winona Nelson"
        flavorText = "\"You think I stand alone? I have the forest at my call, the waters at my " +
            "back. I outnumber you.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c8b738ce-a609-448f-97ea-bbf90ba833d7.jpg?1783935290"
        ruling(
            "2018-01-19",
            "Deeproot Elite's ability can target the Merfolk that caused it to trigger. It can " +
                "also target Deeproot Elite itself."
        )
    }
}
