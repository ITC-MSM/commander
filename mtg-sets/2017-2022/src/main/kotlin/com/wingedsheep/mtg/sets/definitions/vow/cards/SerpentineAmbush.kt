package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Serpentine Ambush — Innistrad: Crimson Vow #77
 * {1}{U} · Instant
 *
 * Until end of turn, target creature becomes a blue Serpent with base power and toughness 5/5.
 *
 * A plain animate: [Effects.BecomeCreature] sets base power/toughness to 5/5 (Layer 7b
 * SET_VALUES), *replaces* the creature subtypes with Serpent (Layer 4) and *replaces* the colors
 * with blue (Layer 5), all for [Duration.EndOfTurn]. Replacement rather than addition is what the
 * printed wording asks for — "becomes a blue Serpent", with no "in addition to its other types"
 * rider, so the target stops being e.g. a white Human Soldier for the turn (contrast Relic's Roar,
 * whose additive wording needs a separate `AddSubtype`).
 *
 * Because the P/T is *base*, a +1/+1 counter or a pump spell still applies on top in the later
 * layers; only the printed 5/5 is what this sets.
 */
val SerpentineAmbush = card("Serpentine Ambush") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Until end of turn, target creature becomes a blue Serpent with base power and toughness 5/5."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.BecomeCreature(
            target = creature,
            power = 5,
            toughness = 5,
            creatureTypes = setOf("Serpent"),
            colors = setOf(Color.BLUE.name),
            duration = Duration.EndOfTurn,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Jokubas Uogintas"
        flavorText = "The merchant did guarantee the new bait would bring in something big."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8214b1c-29f8-4986-89ac-2d7fc929edf3.jpg?1783924884"
    }
}
