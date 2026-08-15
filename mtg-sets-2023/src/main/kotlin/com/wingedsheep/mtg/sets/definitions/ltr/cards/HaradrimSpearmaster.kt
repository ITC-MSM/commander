package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Haradrim Spearmaster
 * {2}{R}
 * Creature — Human Warrior
 * 2/3
 *
 * Reach
 * At the beginning of combat on your turn, another target creature you control gets +1/+0 until end of turn.
 */
val HaradrimSpearmaster = card("Haradrim Spearmaster") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 3
    oracleText = "Reach\nAt the beginning of combat on your turn, another target creature you control gets +1/+0 until end of turn."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        target("another creature you control", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.ModifyStats(1, 0)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Maxim Kostin"
        flavorText = "\"In the South the Haradrim are moving, and fear has fallen on all our coastlands.\"\n—Hirgon, errand-rider of Gondor"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/6050cf98-cdce-4825-9ab8-2294a2b63faf.jpg?1686969027"
    }
}
