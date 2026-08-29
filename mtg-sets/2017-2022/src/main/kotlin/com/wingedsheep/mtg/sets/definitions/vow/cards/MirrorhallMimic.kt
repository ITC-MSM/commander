package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersAsCopy
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mirrorhall Mimic // Ghastly Mimicry (Innistrad: Crimson Vow #68)
 * {3}{U} · Creature — Spirit 0/0 // Enchantment — Aura
 *
 * Front — Mirrorhall Mimic
 *   You may have this creature enter as a copy of any creature on the battlefield, except it's a
 *   Spirit in addition to its other types.
 *   Disturb {3}{U}{U}
 *
 * Back — Ghastly Mimicry (Enchantment — Aura, blue color indicator)
 *   Enchant creature
 *   At the beginning of your upkeep, create a token that's a copy of enchanted creature, except it's
 *   a Spirit in addition to its other types.
 *   If Ghastly Mimicry would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: a disturb card (CR 702.146) in the shape of [TwinbladeGeist]. The front is the
 * Clone shape — [EntersAsCopy] with `additionalSubtypes = ["Spirit"]` as the copy exception
 * (CR 707.9b), so declining leaves a printed 0/0 that dies to state-based actions. The back's upkeep
 * trigger is [Effects.CreateTokenCopyOfTarget] aimed at [EffectTarget.EnchantedCreature] with the
 * same Spirit exception on the token.
 */
private val MirrorhallMimicFront = card("Mirrorhall Mimic") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 0
    toughness = 0
    oracleText = "You may have this creature enter as a copy of any creature on the battlefield, " +
        "except it's a Spirit in addition to its other types.\n" +
        "Disturb {3}{U}{U} (You may cast this card from your graveyard transformed for its disturb cost.)"

    replacementEffect(
        EntersAsCopy(
            optional = true,
            additionalSubtypes = listOf("Spirit"),
        )
    )

    disturb("{3}{U}{U}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "68"
        artist = "Justine Cruz"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/823ad188-bd56-476d-9853-bed90bfad582.jpg?1783924898"
        ruling(
            "2021-11-19",
            "Mirrorhall Mimic copies exactly what was printed on the original creature (unless that " +
                "creature is copying something else or is a token), except that it's also a Spirit. It " +
                "doesn't copy whether that creature is tapped or untapped, whether it has any counters " +
                "on it or any Auras and Equipment attached to it, or any non-copy effects that have " +
                "changed its power, toughness, types, color, or so on."
        )
        ruling(
            "2021-11-19",
            "If the chosen creature is copying something else (for example, if the chosen creature is " +
                "another Mirrorhall Mimic), then Mirrorhall Mimic enters the battlefield as whatever the " +
                "chosen creature copied."
        )
        ruling(
            "2021-11-19",
            "If another creature becomes a copy of Mirrorhall Mimic, that creature is also a Spirit."
        )
        ruling(
            "2021-11-19",
            "Any enters-the-battlefield abilities of the copied creature will trigger when Mirrorhall " +
                "Mimic enters the battlefield. Any \"as [this creature] enters the battlefield\" or " +
                "\"[this creature] enters the battlefield with\" abilities of the chosen creature will " +
                "also work."
        )
        ruling(
            "2021-11-19",
            "If Mirrorhall Mimic somehow enters the battlefield at the same time as another creature, " +
                "it can't become a copy of that creature. You may choose only a creature that's already " +
                "on the battlefield."
        )
    }
}

private val GhastlyMimicry = card("Ghastly Mimicry") {
    manaCost = ""
    colorIdentity = "U"
    colorIndicator = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "At the beginning of your upkeep, create a token that's a copy of enchanted creature, except " +
        "it's a Spirit in addition to its other types.\n" +
        "If Ghastly Mimicry would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.CreateTokenCopyOfTarget(
            target = EffectTarget.EnchantedCreature,
            addedSubtypes = setOf(Subtype.SPIRIT),
        )
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "68"
        artist = "Justine Cruz"
        imageUri = "https://cards.scryfall.io/normal/back/8/2/823ad188-bd56-476d-9853-bed90bfad582.jpg?1783924898"
    }
}

val MirrorhallMimic: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = MirrorhallMimicFront,
    backFace = GhastlyMimicry,
)
