package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Steady Aim
 * {1}{G}
 * Instant
 * Untap target creature. It gets +1/+4 and gains reach until end of turn.
 *
 * Sentence-shaped nesting: the untap is the first sentence, and the second sentence's "it" is the
 * same bound target for both the pump and the keyword grant.
 */
val SteadyAim = card("Steady Aim") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Untap target creature. It gets +1/+4 and gains reach until end of turn."

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.Untap(t),
            Effects.Composite(
                Effects.ModifyStats(1, 4, t),
                Effects.GrantKeyword(Keyword.REACH, t)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Ralph Horsley"
        flavorText = "The archer's heart broke seeing her compatriots die far below, but she held position until the enemy was in range."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4af6a1b5-a561-4d92-b429-de13b2ccaf81.jpg"
    }
}
