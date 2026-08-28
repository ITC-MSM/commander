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
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.RedirectZoneChange

/**
 * Binding Geist // Spectral Binding (Innistrad: Crimson Vow #48 — the card's earliest printing)
 * {2}{U} · Creature — Spirit 3/1 // Enchantment — Aura
 *
 * Front — Binding Geist ({2}{U}, Creature — Spirit, 3/1)
 *   Whenever this creature attacks, target creature an opponent controls gets -2/-0 until end of
 *   turn.
 *   Disturb {1}{U}
 *
 * Back — Spectral Binding (Enchantment — Aura, blue color indicator)
 *   Enchant creature
 *   Enchanted creature gets -2/-0.
 *   If Spectral Binding would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: the two faces spell the same -2/-0 twice, once as a one-shot and once as a
 * static, which is why they are two different SDK types rather than one shared value — the front is
 * `Effects.ModifyStats` on an attack trigger (its duration defaults to end of turn) and the back is
 * the [ModifyStats] *static* on the enchanted creature. Disturb (CR 702.146) puts the card on the
 * stack back face up (CR 712.8c), so the disturb cast is an Aura spell that chooses what it
 * enchants from `auraTarget`. The exile-instead clause is [RedirectZoneChange] with
 * `selfOnly = true` so it functions in every zone (CR 614.12).
 */
private val BindingGeistFront = card("Binding Geist") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 1
    oracleText = "Whenever this creature attacks, target creature an opponent controls gets " +
        "-2/-0 until end of turn.\n" +
        "Disturb {1}{U} (You may cast this card from your graveyard transformed for its disturb cost.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        val weakened = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-2, 0, weakened)
    }

    disturb("{1}{U}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Campbell White"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/730e4629-dc54-415d-9493-88885788ca19.jpg?1783924907"
    }
}

private val SpectralBinding = card("Spectral Binding") {
    manaCost = ""
    colorIdentity = "U"
    colorIndicator = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets -2/-0.\n" +
        "If Spectral Binding would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(-2, 0)
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Campbell White"
        imageUri = "https://cards.scryfall.io/normal/back/7/3/730e4629-dc54-415d-9493-88885788ca19.jpg?1783924907"
    }
}

val BindingGeist: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = BindingGeistFront,
    backFace = SpectralBinding,
)
