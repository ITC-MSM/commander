package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Woodwraith Corrupter
 * {3}{B}{B}{G}
 * Creature — Elemental Horror
 * 3/6
 *
 * {1}{B}{G}, {T}: Target Forest becomes a 4/4 black and green Elemental Horror creature.
 * It's still a land.
 *
 * The animation has **no stated duration**, so it is [Duration.Permanent] — the 2005-10-01 ruling
 * spells it out: "a land turned into a creature this way continues being a creature as long as the
 * land is on the battlefield". Corrupting the same Forest twice is harmless; the later timestamp
 * simply re-applies the identical Layer 4/5/7b effect.
 *
 * "It's still a land" is [Effects.BecomeCreature]'s default — CREATURE and the two subtypes are
 * *added* without removing LAND or the Forest subtype, so the animated Forest still taps for {G}.
 * Any Forest is a legal target, including an opponent's, and any land with the Forest subtype
 * qualifies (a Stomping Ground, a Murmuring Bosk), not only the basic.
 *
 * Setting `colors` overwrites the land's colors in Layer 5 rather than adding to them — which is
 * what "becomes a black and green … creature" says. The animated Forest can then be hit by a
 * "destroy target black creature" and, being a creature that entered this turn, cannot attack or
 * pay a {T} cost until its controller's next turn (the 2008-08-01 summoning-sickness ruling).
 */
val WoodwraithCorrupter = card("Woodwraith Corrupter") {
    manaCost = "{3}{B}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elemental Horror"
    power = 3
    toughness = 6
    oracleText = "{1}{B}{G}, {T}: Target Forest becomes a 4/4 black and green Elemental Horror " +
        "creature. It's still a land."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}{G}"), Costs.Tap)
        val forest = target(
            "target Forest",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.withSubtype(Subtype.FOREST)))
        )
        effect = Effects.BecomeCreature(
            target = forest,
            power = 4,
            toughness = 4,
            creatureTypes = setOf(Subtype.ELEMENTAL.value, Subtype.HORROR.value),
            colors = setOf(Color.BLACK.name, Color.GREEN.name),
            duration = Duration.Permanent,
        )
        description = "{1}{B}{G}, {T}: Target Forest becomes a 4/4 black and green Elemental " +
            "Horror creature. It's still a land."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "240"
        artist = "Greg Staples"
        flavorText = "\"Its darkened sap penetrates natural fibers and spreads its own genes. " +
            "Emulation experiments are underway.\"\n—Simic research notes"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1ee1ceb-61dd-4c98-bc48-a0763315a14f.jpg?1783943608"
        ruling(
            "2005-10-01",
            "The effect has no stated duration, so a land turned into a creature this way " +
                "continues being a creature as long as the land is on the battlefield."
        )
        ruling(
            "2008-08-01",
            "A noncreature permanent that turns into a creature can attack, and its {T} abilities " +
                "can be activated, only if its controller has continuously controlled that " +
                "permanent since the beginning of their most recent turn. It doesn't matter how " +
                "long the permanent has been a creature."
        )
    }
}
