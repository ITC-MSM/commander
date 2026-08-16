package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyKeywordAction
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Leader, Super-Genius
 * {2}{U}{U}
 * Legendary Creature — Gamma Scientist Villain
 * 1/3
 *
 * If a creature you control would connive, instead you draw a card, then that creature connives.
 * At the beginning of combat on your turn, target creature you control connives.
 *
 * Implementation note: structurally the same card as Twists and Turns (LCI) with connive in place
 * of explore — a keyword-action prefix replacement plus a recurring source of that action — so both
 * clauses are the shared primitives, not anything card-specific.
 *
 *  - The first clause is a [ModifyKeywordAction] replacement (CR 614) over
 *    [EventPattern.ConnivedEvent]: "if a creature you control would connive, instead <prefix>, then
 *    that creature connives". The prefix is a plain [Effects.DrawCards] of 1. The replacement is
 *    consulted from the battlefield by `ConniveEffectExecutor` whatever the connive's source is —
 *    this card's own trigger, another of your permanents, or an opponent's effect aimed at your
 *    creature — because the filter scopes on the *conniving* permanent, not on the source.
 *  - The extra card is drawn *before* the connive, which is the whole point of the replacement
 *    rather than a "whenever a creature you control connives, draw a card" trigger: you see both
 *    cards before choosing what to discard, and there is no stack object to respond to. A second
 *    Leader adds a second prefix draw (each replacement applies once, CR 614.5).
 *  - The second clause is the ordinary [Effects.Connive] over a cast-time `target(...)`; the
 *    counter recipient is the conniving creature itself (CR 701.50a), so it is not
 *    `ConniveTargeting`, which picks a *different* recipient reflexively at resolution.
 */
val LeaderSuperGenius = card("Leader, Super-Genius") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Gamma Scientist Villain"
    power = 1
    toughness = 3
    oracleText = "If a creature you control would connive, instead you draw a card, then that " +
        "creature connives.\n" +
        "At the beginning of combat on your turn, target creature you control connives. (Draw a " +
        "card, then discard a card. If you discarded a nonland card, put a +1/+1 counter on that " +
        "creature.)"

    // If a creature you control would connive, instead you draw a card, then that creature connives.
    replacementEffect(
        ModifyKeywordAction(
            prefixEffect = Effects.DrawCards(1),
            appliesTo = EventPattern.ConnivedEvent(filter = GameObjectFilter.Creature.youControl()),
        )
    )

    // At the beginning of combat on your turn, target creature you control connives.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target(
            "target creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl)
        )
        effect = Effects.Connive(target = creature)
        description = "At the beginning of combat on your turn, target creature you control connives."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "64"
        artist = "Anthony Devine"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c8aab8d-2dfe-49c8-9aa8-536b0587b467.jpg?1783902955"
    }
}
