package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Relic's Roar
 * {U}
 * Instant — Common
 *
 * Until end of turn, target artifact or creature becomes a Dinosaur artifact creature with base
 * power and toughness 4/3 in addition to its other types.
 *
 * "In addition to its other types" means every added type is purely additive — the target keeps its
 * printed card types and subtypes. [Effects.BecomeCreature] adds the CREATURE type (Layer 4), adds
 * the ARTIFACT card type via [addTypes] (also Layer 4), and sets base P/T to 4/3 (Layer 7b), but its
 * `creatureTypes` parameter *replaces* all creature subtypes (SetCreatureSubtypes), which would strip
 * the printed subtypes (Centaur/Warrior). So the Dinosaur subtype is granted additively with a
 * separate [Effects.AddSubtype] (AddSubtype modification) instead. All revert at end of turn
 * (Duration.EndOfTurn). The target filter allows any artifact or creature.
 */
val RelicsRoar = card("Relic's Roar") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Until end of turn, target artifact or creature becomes a Dinosaur artifact creature with base power and toughness 4/3 in addition to its other types."

    spell {
        val t = target(
            "target artifact or creature",
            TargetPermanent(filter = TargetFilter.CreatureOrArtifact)
        )
        effect = Effects.Composite(
            Effects.BecomeCreature(
                target = t,
                power = 4,
                toughness = 3,
                addTypes = setOf("ARTIFACT")
            ),
            Effects.AddSubtype("DINOSAUR", target = t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Nino Vecia"
        flavorText = "\"Well, that's one less share to divide up.\"\n—Dranach, Dire Fleet boatswain"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35096eb3-ad7b-4b6e-b799-cb4cc447883e.jpg?1782694553"
    }
}
