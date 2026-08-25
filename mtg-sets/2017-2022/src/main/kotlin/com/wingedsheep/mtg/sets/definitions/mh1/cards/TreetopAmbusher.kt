package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Treetop Ambusher
 * {1}{G}
 * Creature — Elf Berserker
 * 2/1
 * Dash {1}{G} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 * Whenever this creature attacks, target creature you control gets +1/+1 until end of turn.
 *
 * Mardu Strike Leader's shape in green: `dash` is a builder property rather than a keyword constant,
 * and setting it is what adds the `KeywordAbility.Dash` the cast enumerator reads. The printed body
 * is one [Triggers.Attacks] trigger whose target the Elf can pick itself — "target creature you
 * control" is unrestricted, so a dashed Ambusher normally pumps the attacker that paid for it.
 */
val TreetopAmbusher = card("Treetop Ambusher") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Berserker"
    power = 2
    toughness = 1
    oracleText = "Dash {1}{G} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)\n" +
        "Whenever this creature attacks, target creature you control gets +1/+1 until end of turn."

    dash = "{1}{G}"

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(1, 1, t)
        description = "Whenever this creature attacks, target creature you control gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1722a55-850c-4924-be98-45539f4676a2.jpg?1783933088"
    }
}
