package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dreadlight Monstrosity
 * {4}{U}{U}
 * Creature — Crab Horror
 * 5/5
 *
 * Ward {2}
 * {3}{U}{U}: This creature can't be blocked this turn. Activate only if you own a card in exile.
 *
 * Ward {2} is the standard [KeywordAbility.ward] keyword ability. The activated ability grants
 * CANT_BE_BLOCKED to itself until end of turn (the default [GrantKeywordEffect] duration = "this
 * turn"), gated by [ActivationRestriction.OnlyIfCondition] on "you own a card in exile" — modeled
 * as `Exists(Player.You, Zone.EXILE)` since exile is owner-keyed (a card in your exile is one you
 * own).
 */
val DreadlightMonstrosity = card("Dreadlight Monstrosity") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab Horror"
    power = 5
    toughness = 5
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an " +
        "opponent controls, counter it unless that player pays {2}.)\n" +
        "{3}{U}{U}: This creature can't be blocked this turn. Activate only if you own a card in exile."

    keywordAbility(KeywordAbility.ward("{2}"))

    activatedAbility {
        cost = Costs.Mana("{3}{U}{U}")
        effect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, EffectTarget.Self)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(Exists(Player.You, Zone.EXILE))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Jason Kang"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16345278-7565-406b-a958-835081082bc8.jpg?1783924894"
    }
}
