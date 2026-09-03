package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Jhessian Balmgiver
 * {1}{W}{U}
 * Creature — Human Cleric
 * 1 / 1
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 * {T}: Target creature can't be blocked this turn.
 *
 * Two separate {T} abilities, so only one can be used per untap. The first is
 * [Effects.PreventNextDamage] with a fixed amount over an [AnyTarget] — the shield's other
 * knobs (scope, direction, source filter, "this turn" duration) all stay on their defaults.
 * "Can't be blocked" is an [AbilityFlag], not a [com.wingedsheep.sdk.core.Keyword]: the blocking
 * rules read the flag, so granting it through [Effects.GrantKeyword]'s flag overload is what
 * actually makes the creature unblockable rather than merely printing a word on it.
 */
val JhessianBalmgiver = card("Jhessian Balmgiver") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to any target this turn.\n" +
        "{T}: Target creature can't be blocked this turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", AnyTarget())
        effect = Effects.PreventNextDamage(1, t)
    }

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "112"
        artist = "David Palumbo"
        flavorText = "\"You have two choices: heed my advice now or need my healing later.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e638e38-15b9-4081-8340-985d4646e3d7.jpg"
    }
}
