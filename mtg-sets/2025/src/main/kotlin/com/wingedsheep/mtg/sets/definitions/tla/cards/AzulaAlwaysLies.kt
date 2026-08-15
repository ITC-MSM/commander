package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Azula Always Lies
 * {1}{B}
 * Instant — Lesson
 * Choose one or both —
 * • Target creature gets -1/-1 until end of turn.
 * • Put a +1/+1 counter on target creature.
 */
val AzulaAlwaysLies = card("Azula Always Lies") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant — Lesson"
    oracleText = "Choose one or both —\n" +
        "• Target creature gets -1/-1 until end of turn.\n" +
        "• Put a +1/+1 counter on target creature."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Target creature gets -1/-1 until end of turn") {
                val creature = target("target creature", TargetCreature())
                effect = Effects.ModifyStats(-1, -1, creature)
            }
            mode("Put a +1/+1 counter on target creature") {
                val creature = target("target creature", TargetCreature())
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Robin Har"
        flavorText = "Azula is a skilled and deadly warrior, but her true passion is emotional torment."
        imageUri = "https://cards.scryfall.io/normal/front/4/1/416b9207-a2af-44d1-9b87-4943f6e46d42.jpg?1764120582"
    }
}
