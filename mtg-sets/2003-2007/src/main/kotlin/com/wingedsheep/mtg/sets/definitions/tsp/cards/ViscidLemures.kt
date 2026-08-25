package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Viscid Lemures
 * {4}{B}
 * Creature — Spirit
 * 4 / 3
 *
 * {0}: This creature gets -1/-0 and gains swampwalk until end of turn. (It can't be blocked as long
 * as defending player controls a Swamp.)
 *
 * Both halves of the ability last until end of turn, so the shrink and the grant are one composite:
 * the bare [Keyword.SWAMPWALK] grant is enough — the engine's landwalk handling synthesizes the
 * blocking restriction.
 */
val ViscidLemures = card("Viscid Lemures") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 4
    toughness = 3
    oracleText = "{0}: This creature gets -1/-0 and gains swampwalk until end of turn. (It can't be blocked as long as defending player controls a Swamp.)"

    activatedAbility {
        cost = Costs.Mana("{0}")
        effect = Effects.Composite(
            Effects.ModifyStats(-1, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.SWAMPWALK, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Drew Tucker"
        flavorText = "\"Lemurs? Is that all? Finally, something harmless . . .\"\n—Norin the Wary"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/863f04d7-da34-41e3-9153-97891a428889.jpg"
    }
}
