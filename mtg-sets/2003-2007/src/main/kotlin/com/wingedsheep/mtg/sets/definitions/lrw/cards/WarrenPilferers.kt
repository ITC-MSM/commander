package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Warren Pilferers
 * {4}{B}
 * Creature — Goblin Rogue
 * 3/3
 * When this creature enters, return target creature card from your graveyard to your hand. If that
 * card is a Goblin card, this creature gains haste until end of turn.
 *
 * The Cemetery Recruitment shape: the "if that card is a Goblin card" rider is read off the *target*
 * while it is still the graveyard card, so it is a [Conditions.TargetMatchesFilter] wrapping the whole
 * resolution rather than a second effect that would have to chase the card into the hand. The haste
 * lands on the Pilferers itself ([EffectTarget.Self]), not on the returned card.
 */
val WarrenPilferers = card("Warren Pilferers") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Rogue"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, return target creature card from your graveyard to your hand. " +
        "If that card is a Goblin card, this creature gains haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creatureCard = target(
            "target creature card from your graveyard",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard)
        )
        val returnToHand = Effects.Move(creatureCard, Zone.HAND)
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature.withSubtype(Subtype.GOBLIN)),
            effect = returnToHand.then(Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)),
            elseEffect = returnToHand
        )
        description = "Return target creature card from your graveyard to your hand. " +
            "If that card is a Goblin card, this creature gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Wayne Reynolds"
        flavorText = "\"What do they need all this stuff for? They're dead. We're alive. Simple enough.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc98177b-34a6-44e1-9abd-6fd8df09fc70.jpg?1783942882"
    }
}
