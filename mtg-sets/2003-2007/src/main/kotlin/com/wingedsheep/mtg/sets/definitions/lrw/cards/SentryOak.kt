package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val SentryOak = card("Sentry Oak") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Treefolk Warrior"
    power = 3
    toughness = 5
    oracleText = "Defender\nAt the beginning of combat on your turn, you may clash with an opponent. If you win, this creature gets +2/+0 and loses defender until end of turn. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        optional = true
        effect = Patterns.Mechanic.clash(
            Effects.ModifyStats(2, 0, EffectTarget.Self)
                .then(Effects.RemoveKeyword(Keyword.DEFENDER, EffectTarget.Self))
        )
        description = "You may clash with an opponent. If you win, this creature gets +2/+0 and loses defender until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab09d6d7-e2f0-4e78-a9a3-ac37f02f4096.jpg?1783942909"
    }
}
