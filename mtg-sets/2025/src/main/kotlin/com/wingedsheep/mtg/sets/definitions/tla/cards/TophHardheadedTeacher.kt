package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Toph, Hardheaded Teacher
 * {2}{R}{G}
 * Legendary Creature — Human Warrior Ally
 * 3/4
 *
 * When Toph enters, you may discard a card. If you do, return target instant or sorcery
 * card from your graveyard to your hand.
 * Whenever you cast a spell, earthbend 1. If that spell is a Lesson, put an additional
 * +1/+1 counter on that land. (Target land you control becomes a 0/0 creature with haste
 * that's still a land. Put a +1/+1 counter on it. When it dies or is exiled, return it to
 * the battlefield tapped.)
 */
val TophHardheadedTeacher = card("Toph, Hardheaded Teacher") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Human Warrior Ally"
    power = 3
    toughness = 4
    oracleText = "When Toph enters, you may discard a card. If you do, return target instant or sorcery card from your graveyard to your hand.\n" +
        "Whenever you cast a spell, earthbend 1. If that spell is a Lesson, put an additional +1/+1 counter on that land. (Target land you control becomes a 0/0 creature with haste that's still a land. Put a +1/+1 counter on it. When it dies or is exiled, return it to the battlefield tapped.)"

    // ETB: optional discard; if you do, return the targeted instant/sorcery from your graveyard.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val spellCard = target(
            "target instant or sorcery card from your graveyard",
            TargetObject(filter = TargetFilter.InstantOrSorceryInYourGraveyard),
        )
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = Effects.ReturnToHand(spellCard),
            ),
        )
    }

    // Whenever you cast a spell: earthbend 1, with an extra +1/+1 counter on that land if it's a Lesson.
    triggeredAbility {
        trigger = Triggers.youCastSpell()
        val land = target(
            "target land you control",
            TargetObject(filter = TargetFilter.Land.youControl()),
        )
        effect = Effects.Composite(
            Effects.Earthbend(1, land),
            ConditionalEffect(
                Conditions.TriggeringSpellMatches(GameObjectFilter.Any.withSubtype("Lesson")),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, land),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "246"
        artist = "Ruwen Liu"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3ba3395-39db-4330-9801-53def924f253.jpg?1764121814"
    }
}
