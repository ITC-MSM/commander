package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mirkwood Channeler
 * {3}{G}
 * Creature — Elf Druid
 * 3/3
 *
 * At the beginning of combat on your turn, target Elf you control gains trample and gets +X/+X
 * until end of turn, where X is the number of Forests you control.
 */
val MirkwoodChanneler = card("Mirkwood Channeler") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 3
    toughness = 3
    oracleText = "At the beginning of combat on your turn, target Elf you control gains trample and gets +X/+X until end of turn, where X is the number of Forests you control."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val elf = target(
            "target Elf you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl.withSubtype(Subtype("Elf")))
        )
        val forestCount = DynamicAmounts.landsWithSubtype(Subtype("Forest"))
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, elf)
            .then(Effects.ModifyStats(forestCount, forestCount, elf))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "828"
        artist = "Irina Nordsol"
        flavorText = "The dark things that were driven out of Mirkwood have returned in greater numbers, and it is again an evil place, save where the Elvish realm is maintained."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca533454-8bea-4e08-ab0b-e2f4affffaef.jpg?1719684210"
    }
}
