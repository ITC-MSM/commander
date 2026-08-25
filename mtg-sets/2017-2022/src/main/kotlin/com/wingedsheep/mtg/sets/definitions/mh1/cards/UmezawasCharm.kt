package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Umezawa's Charm — Modern Horizons #111
 * {1}{B} · Instant
 *
 * Choose one —
 * • Target creature gets +2/+2 until end of turn.
 * • Target creature gets -1/-1 until end of turn.
 * • You gain 2 life.
 *
 * Each pump mode carries its own target requirement, so the target is chosen for the mode that
 * was picked; the life-gain mode has none.
 */
val UmezawasCharm = card("Umezawa's Charm") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target creature gets +2/+2 until end of turn.\n" +
        "• Target creature gets -1/-1 until end of turn.\n" +
        "• You gain 2 life."

    spell {
        modal(chooseCount = 1) {
            mode("Target creature gets +2/+2 until end of turn") {
                val t = target("target", TargetCreature())
                effect = Effects.ModifyStats(2, 2, t)
            }
            mode("Target creature gets -1/-1 until end of turn") {
                val t = target("target", TargetCreature())
                effect = Effects.ModifyStats(-1, -1, t)
            }
            mode("You gain 2 life") {
                effect = Effects.GainLife(2)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Eric Deschamps"
        flavorText = "\"Be ever loyal to your own best interests.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afbbb742-0317-4266-bcf5-cfea8d21f108.jpg?1783933118"
    }
}
