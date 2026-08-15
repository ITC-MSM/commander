package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Efreet Weaponmaster
 * {3}{U}{R}{W}
 * Creature — Efreet Monk
 * 4/3
 * First strike
 * When Efreet Weaponmaster enters or is turned face up, another target creature you control gets +3/+0 until end of turn.
 * Morph {2}{U}{R}{W}
 */
val EfreetWeaponmaster = card("Efreet Weaponmaster") {
    manaCost = "{3}{U}{R}{W}"
    colorIdentity = "WUR"
    typeLine = "Creature — Efreet Monk"
    power = 4
    toughness = 3
    oracleText = "First strike\nWhen Efreet Weaponmaster enters or is turned face up, another target creature you control gets +3/+0 until end of turn.\nMorph {2}{U}{R}{W} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)"

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("another creature you control", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.ModifyStats(3, 0, t)
    }

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        val t = target("another creature you control", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.ModifyStats(3, 0, t)
    }

    morph = "{2}{U}{R}{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Ryan Alexander Lee"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/8986cb2e-76e0-41f3-8810-3d11c39a527a.jpg?1562789935"
    }
}
