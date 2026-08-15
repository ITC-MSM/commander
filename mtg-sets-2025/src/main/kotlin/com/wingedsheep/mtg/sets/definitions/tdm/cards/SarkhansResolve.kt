package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sarkhan's Resolve — Tarkir: Dragonstorm #158
 * {1}{G} · Instant · Common
 *
 * Choose one —
 * • Target creature gets +3/+3 until end of turn.
 * • Destroy target creature with flying.
 */
val SarkhansResolve = card("Sarkhan's Resolve") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target creature gets +3/+3 until end of turn.\n" +
        "• Destroy target creature with flying."

    spell {
        modal(chooseCount = 1) {
            mode("Target creature gets +3/+3 until end of turn") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(3, 3, t)
            }
            mode("Destroy target creature with flying") {
                val t = target("target creature with flying", Targets.CreatureWithKeyword(Keyword.FLYING))
                effect = Effects.Destroy(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Billy Christian"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/cae56fef-b661-4bc5-b9a1-3871ae06e491.jpg?1743204600"
    }
}
