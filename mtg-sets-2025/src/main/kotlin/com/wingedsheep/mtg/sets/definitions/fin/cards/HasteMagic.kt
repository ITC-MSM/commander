package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Haste Magic
 * {1}{R}
 * Instant
 * Target creature gets +3/+1 and gains haste until end of turn. Exile the top card of
 * your library. You may play it until your next end step.
 *
 * The exile-and-may-play clause is "impulse draw" with an extended permission window
 * ([MayPlayExpiry.UntilNextEndStep] — this turn's end step counts on your own turn).
 */
val HasteMagic = card("Haste Magic") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+1 and gains haste until end of turn. Exile the top card of your library. You may play it until your next end step."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.ModifyStats(3, 1, t),
            Effects.GrantKeyword(Keyword.HASTE, t),
            Patterns.Exile.impulse(count = 1, expiry = MayPlayExpiry.UntilNextEndStep)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "David Astruga"
        flavorText = "\"Double time.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3af9d100-70ee-4c6c-a762-11a0c4f3ef6f.jpg?1748706286"
    }
}
