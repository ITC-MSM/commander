package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.RedirectZoneChange

/**
 * Kindly Ancestor // Ancestor's Embrace (Innistrad: Crimson Vow #22 — the card's earliest printing)
 * {2}{W} · Creature — Spirit 2/3 // Enchantment — Aura
 *
 * Front — Kindly Ancestor ({2}{W}, Creature — Spirit, 2/3)
 *   Lifelink
 *   Disturb {1}{W}
 *
 * Back — Ancestor's Embrace (Enchantment — Aura, white color indicator)
 *   Enchant creature
 *   Enchanted creature has lifelink.
 *   If Ancestor's Embrace would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: the same shape as [TwinbladeGeist] — a disturb card (CR 702.146) whose back face
 * is an Aura, so the disturb cast is an Aura spell that picks what it enchants from the back face's
 * `auraTarget` (CR 712.8c). The front keyword and the Aura's granted keyword are the same word, so
 * the back face is one [GrantKeyword] static. The "would be put into a graveyard from anywhere,
 * exile it instead" line is [RedirectZoneChange] with `selfOnly = true` so it functions in every
 * zone (CR 614.12) — including from the stack, when the disturb spell is countered.
 */
private val KindlyAncestorFront = card("Kindly Ancestor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 3
    oracleText = "Lifelink\n" +
        "Disturb {1}{W} (You may cast this card from your graveyard transformed for its disturb cost.)"

    keywords(Keyword.LIFELINK)
    disturb("{1}{W}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Justyna Dura"
        flavorText = "\"You look cold, dearie.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25193485-7f41-4b05-9a69-4c112679f97c.jpg?1783924921"
    }
}

private val AncestorsEmbrace = card("Ancestor's Embrace") {
    manaCost = ""
    colorIdentity = "W"
    colorIndicator = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has lifelink.\n" +
        "If Ancestor's Embrace would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
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
        collectorNumber = "22"
        artist = "Justyna Dura"
        flavorText = "\"Thank you, Grandmother. I love you too.\""
        imageUri = "https://cards.scryfall.io/normal/back/2/5/25193485-7f41-4b05-9a69-4c112679f97c.jpg?1783924921"
    }
}

val KindlyAncestor: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = KindlyAncestorFront,
    backFace = AncestorsEmbrace,
)
