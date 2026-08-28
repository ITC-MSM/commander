package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Distracting Geist // Clever Distraction (Innistrad: Crimson Vow #9 — the card's earliest
 * printing)
 * {2}{W} · Creature — Spirit 2/1 // Enchantment — Aura
 *
 * Front — Distracting Geist ({2}{W}, Creature — Spirit, 2/1)
 *   Whenever this creature attacks, tap target creature defending player controls.
 *   Disturb {4}{W}
 *
 * Back — Clever Distraction (Enchantment — Aura, white color indicator)
 *   Enchant creature
 *   Enchanted creature has "Whenever this creature attacks, tap target creature defending player
 *   controls."
 *   If Clever Distraction would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: "defending player controls" is modelled as `Targets.CreatureOpponentControls`,
 * the convention every attack-trigger card here uses for the defending player (Spring Splasher,
 * Thunder Lasso, Web-Shooters). The back face prints the front face's ability in quotation marks,
 * so it is a [GrantTriggeredAbility] rebuilding that same ability — the granted ability carries its
 * own `targetRequirement`, and `Effects.Tap` reads it back as `ContextTarget(0)`. It keeps
 * `Triggers.Attacks`' SELF binding: "this creature" inside the quotes is whatever creature has the
 * ability, i.e. the enchanted creature, and `GrantTriggeredAbility` defaults its filter to the
 * attached creature. Disturb is CR 702.146; the disturb cast puts the card on the stack back face
 * up (CR 712.8c) as an Aura spell, and the exile-instead clause is [RedirectZoneChange] with
 * `selfOnly = true` so it functions in every zone (CR 614.12).
 */
private val DistractingGeistFront = card("Distracting Geist") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 1
    oracleText = "Whenever this creature attacks, tap target creature defending player controls.\n" +
        "Disturb {4}{W} (You may cast this card from your graveyard transformed for its disturb cost.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        val tapped = target("creature defending player controls", Targets.CreatureOpponentControls)
        effect = Effects.Tap(tapped)
    }

    disturb("{4}{W}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Andrew Mar"
        flavorText = "\"I never did enjoy studying.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7fc0939-6286-44de-a727-c83bfd3fa752.jpg?1783924930"
        ruling(
            "2021-11-19",
            "Distracting Geist's triggered ability can only target a creature controlled by the " +
                "player you are attacking with Distracting Geist, even if other creatures you " +
                "control are also attacking other players. This is also true for the ability of " +
                "the creature enchanted by Clever Distraction."
        )
    }
}

private val CleverDistraction = card("Clever Distraction") {
    manaCost = ""
    colorIdentity = "W"
    colorIndicator = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"Whenever this creature attacks, tap target creature defending " +
        "player controls.\"\n" +
        "If Clever Distraction would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = Effects.Tap(EffectTarget.ContextTarget(0)),
                targetRequirement = Targets.CreatureOpponentControls,
            )
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
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Andrew Mar"
        flavorText = "\"Me neither.\""
        imageUri = "https://cards.scryfall.io/normal/back/a/7/a7fc0939-6286-44de-a727-c83bfd3fa752.jpg?1783924930"
    }
}

val DistractingGeist: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = DistractingGeistFront,
    backFace = CleverDistraction,
)
