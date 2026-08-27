package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stabbing Pain
 * {B}
 * Instant
 *
 * Target creature gets -1/-1 until end of turn. Tap that creature.
 *
 * "That creature" is the same target, not a second one, so both halves take the one `target()`
 * handle — the Magic Damper shape ("… gets +1/+1 … Untap it."). [Effects.ModifyStats]'s duration
 * already defaults to `Duration.EndOfTurn`, which is what the printed "until end of turn" says, and
 * the tap is a plain one-shot with no duration of its own.
 */
val StabbingPain = card("Stabbing Pain") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -1/-1 until end of turn. Tap that creature."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(-1, -1, t),
            Effects.Tap(t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"You can take the fight out of nearly any creature if you know where its soft spot is.\"\n" +
            "—Guttor, flesh-warper"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89c31d9d-fd77-4275-b89c-f9b64073c54b.jpg?1783941810"
    }
}
