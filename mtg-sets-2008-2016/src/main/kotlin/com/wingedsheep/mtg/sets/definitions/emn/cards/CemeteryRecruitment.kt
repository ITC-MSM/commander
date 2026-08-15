package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Cemetery Recruitment
 * {1}{B}
 * Sorcery
 *
 * Return target creature card from your graveyard to your hand. If it's a Zombie card, draw a card.
 *
 * Canonical printing lives here in Eldritch Moon (earliest real printing); Foundations gets a
 * [com.wingedsheep.sdk.model.Printing] row.
 *
 * The "if it's a Zombie card" bonus is checked against the target's printed types before it moves
 * (the target is still the graveyard card at resolution start, and its type is invariant across the
 * zone change), so the draw is folded into a [ConditionalEffect] that gates the extra draw.
 */
val CemeteryRecruitment = card("Cemetery Recruitment") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to your hand. If it's a Zombie card, draw a card."

    spell {
        val creature = target(
            "creature card from your graveyard",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard)
        )
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature.withSubtype(Subtype.ZOMBIE)),
            effect = Effects.Move(creature, Zone.HAND).then(Effects.DrawCards(1)),
            elseEffect = Effects.Move(creature, Zone.HAND)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a23adea-9f4a-409c-a37d-323eee781273.jpg?1782711893"
    }
}
