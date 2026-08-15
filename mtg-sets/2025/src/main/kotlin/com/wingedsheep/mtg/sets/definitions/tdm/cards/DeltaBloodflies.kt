package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Delta Bloodflies — Tarkir: Dragonstorm #77
 * {1}{B} · Creature — Insect · 1/2
 *
 * Flying
 * Whenever this creature attacks, if you control a creature with a counter on it, each opponent
 * loses 1 life.
 *
 * The attack trigger carries an intervening-if condition (Rule 603.4) via `triggerCondition`:
 * [Conditions.YouControl] over `GameObjectFilter.Creature.withAnyCounter()` ("a creature with a
 * counter on it"). The condition is checked both when the ability would trigger and again on
 * resolution, so removing the last counter before resolution fizzles the life loss. The payoff is
 * [Effects.LoseLife] aimed at [Player.EachOpponent].
 */
val DeltaBloodflies = card("Delta Bloodflies") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever this creature attacks, if you control a creature with a counter on it, " +
        "each opponent loses 1 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.YouControl(GameObjectFilter.Creature.withAnyCounter())
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
        description = "Whenever this creature attacks, if you control a creature with a counter on it, " +
            "each opponent loses 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Inkognit"
        flavorText = "With the rising waters, bloodflies were found far from their usual swamps."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/119bb72d-aed9-47dc-9285-7bc836cc3776.jpg?1743204268"
    }
}
