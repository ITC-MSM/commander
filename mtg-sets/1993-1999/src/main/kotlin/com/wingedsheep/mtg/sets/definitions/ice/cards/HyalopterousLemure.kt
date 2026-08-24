package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hyalopterous Lemure
 * {4}{B}
 * Creature — Spirit
 * 4/3
 *
 * {0}: This creature gets -1/-0 and gains flying until end of turn.
 *
 * The two printed halves are one `Effects.Composite` — the stat shrink and the keyword grant both
 * point at `EffectTarget.Self` and both take the facade default `Duration.EndOfTurn`. Same shape as
 * Hopping Automaton.
 */
val HyalopterousLemure = card("Hyalopterous Lemure") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 4
    toughness = 3
    oracleText = "{0}: This creature gets -1/-0 and gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{0}")
        effect = Effects.Composite(
            Effects.ModifyStats(-1, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Richard Thomas"
        flavorText = "\"The Lemures looked harmless, until they descended on my troops. Within moments, only bones remained.\"\n—Lucilde Fiksdotter, Leader of the Order of the White Shield"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2c9e037-f4d5-46fd-b439-56bee6fb2ad3.jpg"
    }
}
