package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * East Wind Avatar
 * {3}{W}
 * Creature — Bird Spirit Avatar
 * 2/4
 *
 * Flying, vigilance
 * Alliance — Whenever another creature you control enters, this
 * creature gets +1/+0 until end of turn.
 */
val EastWindAvatar = card("East Wind Avatar") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Spirit Avatar"
    oracleText = "Flying, vigilance\nAlliance — Whenever another creature you control enters, this creature gets +1/+0 until end of turn."
    power = 2
    toughness = 4

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "Alliance — Whenever another creature you control enters, this creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Andrea Tentori Montalto"
        flavorText = "Azrael was born on the wind to guide others to new beginnings."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9d5b56c-ad2a-4958-8783-4eceb8733610.jpg?1771502461"
    }
}
