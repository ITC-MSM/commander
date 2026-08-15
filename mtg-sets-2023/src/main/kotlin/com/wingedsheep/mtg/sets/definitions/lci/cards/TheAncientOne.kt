package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttack
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Ancient One
 * {U}{B}
 * Legendary Creature — Spirit God
 * 8/8
 *
 * Descend 8 — The Ancient One can't attack or block unless there are eight or more
 * permanent cards in your graveyard.
 * {2}{U}{B}: Draw a card, then discard a card. When you discard a card this way, target
 * player mills cards equal to its mana value.
 *
 * Implementation notes:
 *  - Descend 8 is modeled as two [ConditionalStaticAbility] gates — a [CantAttack] and a
 *    [CantBlock] on the source — that apply while the restriction is in force, i.e. while you
 *    have FEWER than eight permanent cards in your graveyard
 *    (`Not(CardsInGraveyardMatchingAtLeast(8, Permanent))`). Once eight-or-more permanent cards
 *    are in your graveyard the conditions are false and both restrictions lift. Mirrors Basking
 *    Capybara's Descend gate, but with combat-restriction statics instead of a stat buff.
 *  - The activated ability draws, then runs a discard pipeline whose selected card is stashed in
 *    the `discarded` collection ([Patterns.Hand.discardCards] → `storeSelected = "discarded"`).
 *    "When you discard a card this way" is a [ReflexiveTriggerEffect] (optional = false: the
 *    discard is mandatory) that targets a player after the discard resolves and mills them equal
 *    to the discarded card's mana value. The reflexive effect reads the discarded card's mana
 *    value via [DynamicAmount.StoredCardManaValue] — evaluated by entity id, so it is correct
 *    even though the card has already moved to the graveyard. Milling zero (e.g. a discarded
 *    land) still requires and uses a target player, matching the oracle wording.
 */
val TheAncientOne = card("The Ancient One") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Spirit God"
    power = 8
    toughness = 8
    oracleText = "Descend 8 — The Ancient One can't attack or block unless there are eight or more " +
        "permanent cards in your graveyard.\n" +
        "{2}{U}{B}: Draw a card, then discard a card. When you discard a card this way, target " +
        "player mills cards equal to its mana value."

    // Descend 8 — can't attack unless eight or more permanent cards are in your graveyard.
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantAttack(GroupFilter.source()),
            condition = Conditions.Not(
                Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent)
            )
        )
    }

    // Descend 8 — can't block unless eight or more permanent cards are in your graveyard.
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantBlock(GroupFilter.source()),
            condition = Conditions.Not(
                Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent)
            )
        )
    }

    // {2}{U}{B}: Draw a card, then discard a card. When you discard a card this way, target
    // player mills cards equal to its mana value.
    activatedAbility {
        cost = Costs.Mana("{2}{U}{B}")
        effect = Effects.Composite(
            Effects.DrawCards(1, EffectTarget.Controller),
            ReflexiveTriggerEffect(
                action = Patterns.Hand.discardCards(1),
                optional = false,
                reflexiveEffect = Patterns.Library.mill(
                    DynamicAmount.StoredCardManaValue("discarded"),
                    EffectTarget.ContextTarget(0)
                ),
                reflexiveTargetRequirements = listOf(Targets.Player)
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "222"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66dd43d7-76a7-46ea-b431-097fcea417af.jpg?1782694433"
    }
}
