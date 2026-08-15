package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Skycoach Conductor // All Aboard — Secrets of Strixhaven #67
 * {2}{U} · Creature — Bird Pilot · 2/3
 *
 * Flash
 * Flying, vigilance
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * All Aboard — {U}, Instant: Exile target non-Pilot creature you control, then return that card to
 * the battlefield under its owner's control.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with the PREPARED keyword. Becoming prepared
 * creates a copy of its prepare spell ("All Aboard") in exile that its controller may cast for {U};
 * casting that copy unprepares the creature. Modeled via [com.wingedsheep.sdk.model.CardLayout.PREPARE]
 * + the `prepare(name) { }` DSL.
 *
 * "All Aboard" is a self-blink: exile the chosen non-Pilot creature you control, then move that card
 * back to the battlefield. Routing it through exile makes it a new object (CR 400.7), and a
 * battlefield re-entry with no controllerOverride defaults to the owner — matching "return that card
 * to the battlefield under its owner's control."
 */
val SkycoachConductor = card("Skycoach Conductor") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Pilot"
    power = 2
    toughness = 3
    oracleText = "Flash\n" +
        "Flying, vigilance\n" +
        "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. " +
        "Doing so unprepares it.)"

    keywords(Keyword.FLASH, Keyword.FLYING, Keyword.VIGILANCE)
    keywords(Keyword.PREPARED)

    // All Aboard — the prepare spell. Blink a non-Pilot creature you control.
    prepare("All Aboard") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Exile target non-Pilot creature you control, then return that card to the " +
            "battlefield under its owner's control."
        spell {
            val creature = target(
                "non-Pilot creature you control",
                TargetCreature(
                    filter = TargetFilter(
                        GameObjectFilter.Creature.notSubtype(Subtype("Pilot")).youControl()
                    )
                )
            )
            effect = Effects.Exile(creature)
                .then(Effects.Move(creature, Zone.BATTLEFIELD))
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "67"
        artist = "Christina Kraus"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ecbca71-9a1d-44c5-b709-d6f565941d5e.jpg?1778165128"
    }
}
