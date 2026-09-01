package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Soulless Revival
 * {1}{B}
 * Instant — Arcane
 * Return target creature card from your graveyard to your hand.
 * Splice onto Arcane {1}{B}
 *
 * [TargetFilter.CreatureInYourGraveyard] carries both halves of the restriction the sentence
 * names: the card must be a creature card, and it must be in *your* graveyard (an ownership
 * predicate, not a controller one — graveyard cards have no controller).
 */
val SoullessRevival = card("Soulless Revival") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant — Arcane"
    oracleText = "Return target creature card from your graveyard to your hand.\n" +
        "Splice onto Arcane {1}{B} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{1}{B}")

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.CreatureInYourGraveyard))
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b36712c-1ccb-4efe-8db8-823b9b80a99f.jpg?1783944306"
    }
}
