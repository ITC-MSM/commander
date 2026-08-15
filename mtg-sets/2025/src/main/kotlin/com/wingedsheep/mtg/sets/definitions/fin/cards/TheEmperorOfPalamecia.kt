package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * The Emperor of Palamecia // The Lord Master of Hell — Final Fantasy #219
 * {U}{R} · Legendary Creature — Human Noble Wizard · 2/2 // Legendary Creature — Demon Noble Wizard · 3/3
 *
 * Front — The Emperor of Palamecia:
 *   {T}: Add {U} or {R}. Spend this mana only to cast a noncreature spell.
 *   Whenever you cast a noncreature spell, if at least four mana was spent to cast it, put a
 *   +1/+1 counter on The Emperor of Palamecia. Then if it has three or more +1/+1 counters
 *   on it, transform it.
 *
 * Back — The Lord Master of Hell:
 *   Starfall — Whenever The Lord Master of Hell attacks, it deals X damage to each opponent,
 *   where X is the number of noncreature, nonland cards in your graveyard.
 *
 * The restricted mana ability composes [ManaColorSet.Specific] (pick {U} or {R}) with the
 * negated card-type restriction ("noncreature spells only"). The cast trigger's four-mana
 * gate reads [ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL] — the mana actually paid
 * for the *triggering* spell (the Opus idiom); the amount is fixed history, so checking it
 * at resolution is equivalent to the printed intervening-"if". The "Then if" transform check
 * runs after the counter lands, counting via [Conditions.SourceCounterCountAtLeast].
 */
private val TheLordMasterOfHell = card("The Lord Master of Hell") {
    manaCost = ""
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Demon Noble Wizard"
    oracleText = "Starfall — Whenever The Lord Master of Hell attacks, it deals X damage to " +
        "each opponent, where X is the number of noncreature, nonland cards in your graveyard."
    power = 3
    toughness = 3

    // Starfall — Whenever The Lord Master of Hell attacks, it deals X damage to each opponent,
    // where X is the number of noncreature, nonland cards in your graveyard.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.DealDamage(
            amount = DynamicAmount.Count(
                Player.You,
                Zone.GRAVEYARD,
                GameObjectFilter.Noncreature and GameObjectFilter.Nonland,
            ),
            target = EffectTarget.PlayerRef(Player.EachOpponent),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Heonhwa"
        flavorText = "\"All the world shall fall by my hand.\""
        imageUri = "https://cards.scryfall.io/normal/back/3/d/3d75e8fd-6139-4b10-9ce3-195b47d72e0c.jpg?1782686430"
    }
}

private val TheEmperorOfPalameciaFront = card("The Emperor of Palamecia") {
    manaCost = "{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Noble Wizard"
    oracleText = "{T}: Add {U} or {R}. Spend this mana only to cast a noncreature spell.\n" +
        "Whenever you cast a noncreature spell, if at least four mana was spent to cast it, " +
        "put a +1/+1 counter on The Emperor of Palamecia. Then if it has three or more +1/+1 " +
        "counters on it, transform it."
    power = 2
    toughness = 2

    // {T}: Add {U} or {R}. Spend this mana only to cast a noncreature spell.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(
            colorSet = ManaColorSet.Specific(setOf(Color.BLUE, Color.RED)),
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.CREATURE,
                negated = true,
            ),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {U} or {R}. Spend this mana only to cast a noncreature spell."
    }

    // Whenever you cast a noncreature spell, if at least four mana was spent to cast it, put a
    // +1/+1 counter on The Emperor of Palamecia. Then if it has three or more +1/+1 counters
    // on it, transform it.
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = ConditionalEffect(
            condition = Compare(
                DynamicAmount.ContextProperty(ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL),
                ComparisonOperator.GTE,
                DynamicAmount.Fixed(4),
            ),
            effect = Effects.Composite(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                ConditionalEffect(
                    condition = Conditions.SourceCounterCountAtLeast(Counters.PLUS_ONE_PLUS_ONE, 3),
                    effect = TransformEffect(EffectTarget.Self),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Heonhwa"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d75e8fd-6139-4b10-9ce3-195b47d72e0c.jpg?1782686430"

        ruling(
            "2025-06-06",
            "The Emperor of Palamecia's last ability resolves before the spell that caused it " +
                "to trigger. It resolves even if that spell is countered or otherwise leaves the stack."
        )
        ruling(
            "2025-06-06",
            "The value of X is calculated only once, as The Lord Master of Hell's ability resolves."
        )
    }
}

val TheEmperorOfPalamecia: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = TheEmperorOfPalameciaFront,
    backFace = TheLordMasterOfHell,
)
