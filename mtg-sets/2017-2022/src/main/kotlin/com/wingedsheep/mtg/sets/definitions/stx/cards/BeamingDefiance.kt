package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Beaming Defiance — Strixhaven: School of Mages #9 (canonical printing)
 * {1}{W} · Instant
 *
 * Target creature you control gets +2/+2 and gains hexproof until end of turn. (It can't be the target of spells or abilities your opponents control.)
 *
 * One target, two until-end-of-turn effects in printed order: [Effects.ModifyStats] for the +2/+2,
 * then [Effects.GrantKeyword] for hexproof, both bound to the same [Targets.CreatureYouControl].
 */
val BeamingDefiance = card("Beaming Defiance") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Target creature you control gets +2/+2 and gains hexproof until end of turn. (It can't be the target of spells or abilities your opponents control.)"

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(2, 2, creature) then
            Effects.GrantKeyword(Keyword.HEXPROOF, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Manuel Castañón"
        flavorText = "\"I've lived too long in my father's shadow. It's time to find my own light.\"\n—Killian, Silverquill mage-student"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e22411c-11c1-4770-8491-7952dd406e01.jpg?1783927395"
    }
}
