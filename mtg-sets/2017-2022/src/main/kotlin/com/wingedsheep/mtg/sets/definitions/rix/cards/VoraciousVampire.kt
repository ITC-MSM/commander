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
 * Voracious Vampire
 * {2}{B}
 * Creature — Vampire Knight
 * 2/2
 * Menace
 * When this creature enters, target Vampire you control gets +1/+1 and gains menace until end of turn.
 *
 * "Target Vampire you control" is the bare tribal noun, so the filter is
 * `GameObjectFilter.Permanent` — a noncreature Vampire permanent is a legal target too.
 */
val VoraciousVampire = card("Voracious Vampire") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    oracleText = "Menace\n" +
        "When this creature enters, target Vampire you control gets +1/+1 and gains menace " +
        "until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val vampire = target(
            "target Vampire you control",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE).youControl()
                )
            )
        )
        effect = Effects.ModifyStats(1, 1, vampire) then
            Effects.GrantKeyword(Keyword.MENACE, vampire)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Kieran Yanner"
        flavorText = "The purest devotion isn't the Blood Fast itself, but the craving that follows."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d66a9f1b-31c8-4557-888b-a927922d37af.jpg?1783935303"
    }
}
