package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * Moseo, Vein's New Dean — Secrets of Strixhaven #91
 * {2}{B} · Legendary Creature — Bird Skeleton Warlock · 2/1
 *
 * Flying
 * When Moseo enters, create a 1/1 black and green Pest creature token with
 * "Whenever this token attacks, you gain 1 life."
 * Infusion — At the beginning of your end step, if you gained life this turn, return up to one
 * target creature card with mana value X or less from your graveyard to the battlefield, where X
 * is the amount of life you gained this turn.
 *
 * The ETB Pest is the set-standard token (its own self-attack life-gain trigger via
 * [CreateTokenEffect.triggeredAbilities]), shared with Essenceknit Scholar / Send in the Pest.
 *
 * Infusion is an ability word (no rules meaning) flavoring the end-step trigger. It is gated by an
 * intervening-if on [Conditions.YouGainedLifeThisTurn] (CR 603.4 — checked both when it would trigger
 * and again on resolution). The reanimation is an "up to one target" ([TargetObject] with
 * `optional = true`) creature card in **your** graveyard whose mana value is at most the amount of
 * life you gained this turn — the dynamic cap `manaValueAtMostDynamic(TurnTracking(You, LIFE_GAINED))`.
 * [Effects.PutOntoBattlefield] reanimates the chosen card under the controller's control.
 */
val MoseoVeinsNewDean = card("Moseo, Vein's New Dean") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Bird Skeleton Warlock"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "When Moseo enters, create a 1/1 black and green Pest creature token with \"Whenever " +
        "this token attacks, you gain 1 life.\"\n" +
        "Infusion — At the beginning of your end step, if you gained life this turn, return up to " +
        "one target creature card with mana value X or less from your graveyard to the battlefield, " +
        "where X is the amount of life you gained this turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK, Color.GREEN),
            creatureTypes = setOf("Pest"),
            triggeredAbilities = listOf(
                TriggeredAbility.create(
                    trigger = Triggers.Attacks.event,
                    binding = Triggers.Attacks.binding,
                    effect = Effects.GainLife(1)
                )
            ),
            imageUri = "https://cards.scryfall.io/normal/front/b/a/ba854032-6ad2-4654-990a-64006e7f92fd.jpg?1777982237"
        )
        description = "When Moseo enters, create a 1/1 black and green Pest creature token with " +
            "\"Whenever this token attacks, you gain 1 life.\""
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouGainedLifeThisTurn
        val t = target(
            "up to one target creature card with mana value X or less from your graveyard",
            TargetObject(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.Creature.ownedByYou()
                        .manaValueAtMostDynamic(
                            DynamicAmount.TurnTracking(Player.You, TurnTracker.LIFE_GAINED)
                        ),
                    zone = Zone.GRAVEYARD,
                ),
            )
        )
        effect = Effects.PutOntoBattlefield(t)
        description = "Infusion — At the beginning of your end step, if you gained life this turn, " +
            "return up to one target creature card with mana value X or less from your graveyard to " +
            "the battlefield, where X is the amount of life you gained this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "91"
        artist = "Abz J Harding"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/6877180c-22a1-4c4d-9178-316f4c34661b.jpg?1775937545"
    }
}
