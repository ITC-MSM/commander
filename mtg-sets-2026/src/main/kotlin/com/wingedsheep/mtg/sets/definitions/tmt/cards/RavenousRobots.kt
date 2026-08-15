package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern.SpellCastEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ravenous Robots
 * {1}{R}
 * Artifact Creature — Robot
 * 2/1
 *
 * Whenever you cast an artifact spell, create a 1/1 colorless Robot
 * artifact creature token.
 * {R}, {T}: Creature tokens you control gain haste until end of turn.
 */
val RavenousRobots = card("Ravenous Robots") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Robot"
    oracleText = "Whenever you cast an artifact spell, create a 1/1 colorless Robot artifact creature token.\n{R}, {T}: Creature tokens you control gain haste until end of turn."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = TriggerSpec(
            event = SpellCastEvent(spellFilter = GameObjectFilter.Artifact, player = Player.You),
            binding = TriggerBinding.ANY
        )
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(),
            creatureTypes = setOf("Robot"),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/0/8/08497fc5-1c0e-4c3c-a356-bf4b34bd4c45.jpg?1771590585"
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{R}"),
            Costs.Tap
        )
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(
                GameObjectFilter.Creature.copy(
                    cardPredicates = GameObjectFilter.Creature.cardPredicates +
                        CardPredicate.IsToken
                ).youControl()
            ),
            effect = GrantKeywordEffect(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "106"
        artist = "Kevin Sidharta"
        flavorText = "\"The mousers have a new command: to consume everything here . . . all of you will be destroyed!\"\n—Baxter Stockman"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b303ea3-9f4d-4c28-9446-285a23f841a0.jpg?1769006169"
    }
}
