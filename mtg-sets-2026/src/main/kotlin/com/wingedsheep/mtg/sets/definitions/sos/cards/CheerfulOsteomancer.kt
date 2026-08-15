package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cheerful Osteomancer // Raise Dead — Secrets of Strixhaven #76
 * {3}{B} · Creature — Orc Warlock · 4/2
 *
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * Raise Dead — {B}, Sorcery: Return target creature card from your graveyard to your hand.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with the PREPARED keyword. Becoming
 * prepared creates a copy of its prepare spell ("Raise Dead") in exile that its controller may
 * cast for {B}; casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE] +
 * the `prepare(name) { }` DSL.
 */
val CheerfulOsteomancer = card("Cheerful Osteomancer") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Orc Warlock"
    power = 4
    toughness = 2
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.PREPARED)

    // Raise Dead — the prepare spell. Return target creature card from your graveyard to your hand.
    prepare("Raise Dead") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        oracleText = "Return target creature card from your graveyard to your hand."
        spell {
            target = Targets.CreatureCardInYourGraveyard
            effect = Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Diego Gisbert"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c34660c-25e3-4ff5-9b2b-5554ded2bcc3.jpg?1775937441"
    }
}
