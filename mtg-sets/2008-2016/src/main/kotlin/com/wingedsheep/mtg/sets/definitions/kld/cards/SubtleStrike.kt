package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Subtle Strike
 * {1}{B}
 * Instant
 *
 * Choose one or both —
 * • Target creature gets -1/-1 until end of turn.
 * • Put a +1/+1 counter on target creature.
 *
 * "Choose one or both" is the modal *count*, not a third mode: `chooseCount = 2` with
 * `minChooseCount = 1` (CR 700.2). Each mode carries its own target, so picking both asks for two
 * creatures — which may be the same creature twice, since the requirements are independent.
 */
val SubtleStrike = card("Subtle Strike") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Target creature gets -1/-1 until end of turn.\n" +
        "• Put a +1/+1 counter on target creature."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Target creature gets -1/-1 until end of turn") {
                val t = target("target", TargetCreature())
                effect = Effects.ModifyStats(-1, -1, t)
            }
            mode("Put a +1/+1 counter on target creature") {
                val t = target("target", TargetCreature())
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "David Palumbo"
        flavorText = "Renegades do some of their best work right under the Consulate's nose."
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61a8cb98-7ee7-4f90-bfba-0a406a5e6d6b.jpg?1783937200"
    }
}
