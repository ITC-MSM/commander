package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fire-Belly Changeling
 * {1}{R}
 * Creature — Shapeshifter
 * 1/1
 * Changeling (This card is every creature type.)
 * {R}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn.
 *
 * The printed cap is [ActivationRestriction.MaxPerTurn] — the counted sibling of `OncePerTurn`,
 * enforced by the activation-legality check rather than by the effect, so the third activation is
 * never offered instead of being offered and fizzling.
 */
val FireBellyChangeling = card("Fire-Belly Changeling") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)\n" +
        "{R}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.MaxPerTurn(2))
        description = "{R}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Randy Gallegos"
        flavorText = "\"My ears say it hisses. My fingers say it burns.\"\n—Auntie Wort"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4732342-71a4-4079-b549-f4454945273a.jpg?1783942877"
    }
}
