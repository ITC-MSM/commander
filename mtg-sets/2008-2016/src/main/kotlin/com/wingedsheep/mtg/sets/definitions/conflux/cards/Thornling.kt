package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thornling
 * {3}{G}{G}
 * Creature — Elemental Shapeshifter
 * 4 / 4
 * {G}: This creature gains haste until end of turn.
 * {G}: This creature gains trample until end of turn.
 * {G}: This creature gains indestructible until end of turn.
 * {1}: This creature gets +1/-1 until end of turn.
 * {1}: This creature gets -1/+1 until end of turn.
 *
 * The Morphling shape: five independent, repeatable activated abilities, each with only a mana
 * cost (no {T}, so each can be activated any number of times). Every effect points at
 * [EffectTarget.Self] — "this creature" is the source, not a target, so none of them declares a
 * target requirement. The keyword grants and the stat swings both default to
 * `Duration.EndOfTurn`, which is exactly the printed "until end of turn".
 */
val Thornling = card("Thornling") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental Shapeshifter"
    power = 4
    toughness = 4
    oracleText = "{G}: This creature gains haste until end of turn.\n" +
        "{G}: This creature gains trample until end of turn.\n" +
        "{G}: This creature gains indestructible until end of turn.\n" +
        "{1}: This creature gets +1/-1 until end of turn.\n" +
        "{1}: This creature gets -1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.ModifyStats(-1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "95"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16691f25-8d6f-4edd-84ad-3209e8a74cf3.jpg"
    }
}
