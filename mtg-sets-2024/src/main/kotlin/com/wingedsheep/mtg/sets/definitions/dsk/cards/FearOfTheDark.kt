package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fear of the Dark
 * {4}{B}
 * Enchantment Creature — Nightmare
 * 5/5
 * Whenever this creature attacks, if defending player controls no Glimmer creatures, it gains
 * menace and deathtouch until end of turn.
 *
 * The intervening-if (CR 603.4) reads the defending player's battlefield via
 * `DynamicAmount.AggregateBattlefield(Player.DefendingPlayer, ...)` — "no Glimmer creatures" is a
 * count of zero. `Player.DefendingPlayer` resolves through the attacking source's combat assignment.
 */
val FearOfTheDark = card("Fear of the Dark") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "Whenever this creature attacks, if defending player controls no Glimmer creatures, " +
        "it gains menace and deathtouch until end of turn. (A creature with menace can't be blocked " +
        "except by two or more creatures.)"
    power = 5
    toughness = 5

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.CompareAmounts(
            DynamicAmount.AggregateBattlefield(
                Player.DefendingPlayer,
                GameObjectFilter.Creature.withSubtype("Glimmer")
            ),
            ComparisonOperator.EQ,
            DynamicAmount.Fixed(0)
        )
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Sam Wolfe Connelly"
        flavorText = "The first thing it kills is the light."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/8700eb8d-1cc0-45ff-b769-c875ee6500ea.jpg?1726286215"
    }
}
