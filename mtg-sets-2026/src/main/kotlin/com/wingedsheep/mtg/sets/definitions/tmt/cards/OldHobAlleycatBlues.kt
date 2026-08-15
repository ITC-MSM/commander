package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Old Hob, Alleycat Blues
 * {4}{R}
 * Legendary Creature — Cat Mutant Rebel
 * 4/4
 *
 * At the beginning of combat on your turn, create a 2/2 red Mutant
 * creature token. It gains haste until end of turn. Destroy it at the
 * beginning of the next end step.
 * {1}{W}: Target attacking creature token gains indestructible until
 * end of turn.
 *
 * The delayed end-step destroy resolves the freshly-created token via
 * `EffectTarget.PipelineTarget(CREATED_TOKENS, 0)` — `CreateTokenEffect`
 * publishes new token ids into the `CREATED_TOKENS` pipeline collection
 * (see `Patterns.Mechanic.incubate` for the established precedent).
 * `byDestruction = true` routes the cleanup through the destroy
 * pipeline so the second ability's UEOT indestructible grant
 * legitimately saves the token (a `sacrificeAtStep` shortcut would
 * silently bypass indestructible).
 */
val OldHobAlleycatBlues = card("Old Hob, Alleycat Blues") {
    manaCost = "{4}{R}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Cat Mutant Rebel"
    oracleText = "At the beginning of combat on your turn, create a 2/2 red Mutant creature token. It gains haste until end of turn. Destroy it at the beginning of the next end step.\n{1}{W}: Target attacking creature token gains indestructible until end of turn."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Mutant"),
            keywords = setOf(Keyword.HASTE),
            imageUri = "https://cards.scryfall.io/normal/front/5/1/51e33613-7a24-461c-8d9f-12680af4b92a.jpg?1771590526"
        ).then(
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.Move(
                    target = EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
                    destination = Zone.GRAVEYARD,
                    byDestruction = true,
                )
            )
        )
        description = "At the beginning of combat on your turn, create a 2/2 red Mutant creature token. It gains haste until end of turn. Destroy it at the beginning of the next end step."
    }

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        val attackingTokenFilter = GameObjectFilter.Creature.attacking().let { base ->
            base.copy(cardPredicates = base.cardPredicates + CardPredicate.IsToken)
        }
        val token = target(
            "target attacking creature token",
            TargetPermanent(filter = TargetFilter(attackingTokenFilter)),
        )
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, token, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Rose Benjamin"
        flavorText = "\"This is us against them, plain and simple. Either you're with us, or . . .\""
        imageUri = "https://cards.scryfall.io/normal/front/1/5/1515eed9-3b21-42f0-ab57-de5df19317af.jpg?1771586941"
    }
}
