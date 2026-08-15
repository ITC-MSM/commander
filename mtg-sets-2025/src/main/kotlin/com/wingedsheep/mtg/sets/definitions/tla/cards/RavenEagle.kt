package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Raven Eagle
 * {2}{B}
 * Creature — Bird Assassin
 * 2/3
 *
 * Flying
 * Whenever this creature enters or attacks, exile up to one target card from a graveyard.
 * If a creature card is exiled this way, create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 * Whenever you draw your second card each turn, each opponent loses 1 life and you gain 1 life.
 *
 * Implementation notes:
 *  - "enters or attacks" is two triggered abilities ([Triggers.EntersBattlefield] +
 *    [Triggers.Attacks]), each exiling an optional ("up to one") graveyard card.
 *  - "If a creature card is exiled this way" is a [ConditionalEffect] gated on
 *    [Conditions.TargetIsCreatureCard], which reads the exiled card's printed type in exile —
 *    correctly false when no target was chosen.
 *  - "your second card each turn" is [Triggers.NthCardDrawn], draining each opponent for 1.
 */
val RavenEagle = card("Raven Eagle") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bird Assassin"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever this creature enters or attacks, exile up to one target card from a graveyard. " +
        "If a creature card is exiled this way, create a Clue token. (It's an artifact with " +
        "\"{2}, Sacrifice this token: Draw a card.\")\n" +
        "Whenever you draw your second card each turn, each opponent loses 1 life and you gain 1 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val exiled = target(
            "card from a graveyard",
            TargetObject(optional = true, filter = TargetFilter.CardInGraveyard)
        )
        effect = Effects.Composite(
            Effects.Exile(exiled),
            ConditionalEffect(
                condition = Conditions.TargetIsCreatureCard(0),
                effect = Effects.CreateClue()
            )
        )
        description = "Whenever this creature enters, exile up to one target card from a graveyard. " +
            "If a creature card is exiled this way, create a Clue token."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val exiled = target(
            "card from a graveyard",
            TargetObject(optional = true, filter = TargetFilter.CardInGraveyard)
        )
        effect = Effects.Composite(
            Effects.Exile(exiled),
            ConditionalEffect(
                condition = Conditions.TargetIsCreatureCard(0),
                effect = Effects.CreateClue()
            )
        )
        description = "Whenever this creature attacks, exile up to one target card from a graveyard. " +
            "If a creature card is exiled this way, create a Clue token."
    }

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1)
        )
        description = "Whenever you draw your second card each turn, each opponent loses 1 life and you gain 1 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "116"
        artist = "Robin Olausson"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/2262f24b-db6b-4f86-96d0-47a20fc015ab.jpg?1764120797"
    }
}
