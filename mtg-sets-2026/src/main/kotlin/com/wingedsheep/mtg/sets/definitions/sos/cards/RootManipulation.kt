package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Root Manipulation
 * {3}{B}{G}
 * Sorcery
 *
 * Until end of turn, creatures you control get +2/+2 and gain menace and "Whenever this
 * creature attacks, you gain 1 life."
 *
 * Like Overrun, this is a one-shot pump applied to each creature the controller has at
 * resolution (Rule 611.2c — the set of affected creatures is locked in when the spell
 * resolves; creatures entering later in the turn are unaffected). Each affected creature
 * receives +2/+2, the menace keyword, and a granted "Whenever this creature attacks, you
 * gain 1 life" triggered ability ([TriggerBinding.SELF] via [Triggers.Attacks]), all for
 * the duration of the turn.
 */
val RootManipulation = card("Root Manipulation") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Until end of turn, creatures you control get +2/+2 and gain menace and " +
        "\"Whenever this creature attacks, you gain 1 life.\" (A creature with menace can't be " +
        "blocked except by two or more creatures.)"

    spell {
        val attackGainLife = TriggeredAbility.create(
            trigger = Triggers.Attacks.event,
            binding = Triggers.Attacks.binding,
            effect = Effects.GainLife(1),
            descriptionOverride = "Whenever this creature attacks, you gain 1 life.",
        )
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
            effect = Effects.Composite(
                Effects.ModifyStats(2, 2, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self),
                GrantTriggeredAbilityEffect(ability = attackGainLife, target = EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Elizabeth Peiró"
        flavorText = "\"Nature endures and adapts, and so shall we.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5390a79c-bc4b-4edb-a845-0d3514986401.jpg?1775938546"
    }
}
