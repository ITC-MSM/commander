package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Swift Warden
 * {1}{G}{G}
 * Creature — Merfolk Warrior
 * 3/3
 * Flash
 * When this creature enters, target Merfolk you control gains hexproof until end of turn.
 *
 * "Target Merfolk you control" is the bare tribal noun, so the filter is
 * `GameObjectFilter.Permanent` — a noncreature Merfolk permanent is a legal target too.
 */
val SwiftWarden = card("Swift Warden") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Warrior"
    oracleText = "Flash\n" +
        "When this creature enters, target Merfolk you control gains hexproof until end of turn. " +
        "(It can't be the target of spells or abilities your opponents control.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val merfolk = target(
            "target Merfolk you control",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl()
                )
            )
        )
        effect = Effects.GrantKeyword(Keyword.HEXPROOF, merfolk)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Viktor Titov"
        flavorText = "A warning shout would take too long."
        imageUri = "https://cards.scryfall.io/normal/front/1/5/1531cb47-b0ff-4d66-acf2-ef5bb5f690fc.jpg?1783935281"
    }
}
