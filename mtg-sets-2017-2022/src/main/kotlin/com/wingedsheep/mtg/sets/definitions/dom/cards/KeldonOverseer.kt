package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked

/**
 * Keldon Overseer
 * {2}{R}
 * Creature — Human Warrior
 * 3/1
 * Kicker {3}{R}
 * Haste
 * When this creature enters, if it was kicked, gain control of target creature
 * until end of turn. Untap that creature. It gains haste until end of turn.
 */
val KeldonOverseer = card("Keldon Overseer") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 1
    oracleText = "Kicker {3}{R} (You may pay an additional {3}{R} as you cast this spell.)\nHaste\nWhen this creature enters, if it was kicked, gain control of target creature until end of turn. Untap that creature. It gains haste until end of turn."

    keywordAbility(KeywordAbility.kicker("{3}{R}"))
    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.GainControl(t, Duration.EndOfTurn),
            Effects.Untap(t),
            Effects.GrantKeyword(Keyword.HASTE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Jason A. Engle"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec66b814-9c47-4d17-8c02-1d9be565c76c.jpg?1562745023"
    }
}
