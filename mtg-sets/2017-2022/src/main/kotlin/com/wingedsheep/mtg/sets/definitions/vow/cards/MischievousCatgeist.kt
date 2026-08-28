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

/**
 * Mischievous Catgeist // Catlike Curiosity (Innistrad: Crimson Vow #69 — the card's earliest
 * printing)
 * {1}{U} · Creature — Cat Spirit 1/1 // Enchantment — Aura
 *
 * Front — Mischievous Catgeist ({1}{U}, Creature — Cat Spirit, 1/1)
 *   Whenever this creature deals combat damage to a player, draw a card.
 *   Disturb {2}{U}
 *
 * Back — Catlike Curiosity (Enchantment — Aura, blue color indicator)
 *   Enchant creature
 *   Enchanted creature has "Whenever this creature deals combat damage to a player, draw a card."
 *   If Catlike Curiosity would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: the back face prints the front face's ability in quotation marks, which is the
 * SDK's [GrantTriggeredAbility] — the same ability the front face has as its own, rebuilt as a
 * granted one. `GrantTriggeredAbility`'s filter defaults to the attached creature, so the Aura
 * needs no explicit filter. The granted ability keeps `Triggers.DealsCombatDamageToPlayer`'s SELF
 * binding: "this creature" inside the quotes is the creature that has the ability, i.e. the
 * enchanted creature, not the Aura. Disturb is CR 702.146; the exile-instead clause is
 * [RedirectZoneChange] with `selfOnly = true` so it functions in every zone (CR 614.12).
 */
private val MischievousCatgeistFront = card("Mischievous Catgeist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Cat Spirit"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature deals combat damage to a player, draw a card.\n" +
        "Disturb {2}{U} (You may cast this card from your graveyard transformed for its disturb cost.)"

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
    }

    disturb("{2}{U}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "69"
        artist = "Denman Rooke"
        flavorText = "\"I never get any knitting done, but I don't entirely mind.\"\n" +
            "—Lorn, Lambholt innkeeper"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3ff628a-ef8e-45c4-84e7-a33ec28f025a.jpg?1783924899"
    }
}

private val CatlikeCuriosity = card("Catlike Curiosity") {
    manaCost = ""
    colorIdentity = "U"
    colorIndicator = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"Whenever this creature deals combat damage to a player, " +
        "draw a card.\"\n" +
        "If Catlike Curiosity would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = Effects.DrawCards(1),
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
        collectorNumber = "69"
        artist = "Denman Rooke"
        imageUri = "https://cards.scryfall.io/normal/back/a/3/a3ff628a-ef8e-45c4-84e7-a33ec28f025a.jpg?1783924899"
    }
}

val MischievousCatgeist: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = MischievousCatgeistFront,
    backFace = CatlikeCuriosity,
)
