package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.RedirectZoneChange

/**
 * Drogskol Infantry // Drogskol Armaments (Innistrad: Crimson Vow #10 — the card's earliest
 * printing)
 * {1}{W} · Creature — Spirit Soldier 2/2 // Enchantment — Aura
 *
 * Front — Drogskol Infantry ({1}{W}, Creature — Spirit Soldier, 2/2)
 *   Disturb {3}{W}
 *
 * Back — Drogskol Armaments (Enchantment — Aura, white color indicator)
 *   Enchant creature
 *   Enchanted creature gets +2/+2.
 *   If Drogskol Armaments would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: the plainest member of the disturb-Aura cycle (CR 702.146) — a vanilla front face
 * whose only ability is disturb, and a back face that is one [ModifyStats] static. The disturb cast
 * puts the card on the stack back face up (CR 712.8c), so it is an Aura spell and what it enchants
 * comes from the back face's `auraTarget`. The exile-instead clause is [RedirectZoneChange] with
 * `selfOnly = true` so it functions in every zone (CR 614.12).
 */
private val DrogskolInfantryFront = card("Drogskol Infantry") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    power = 2
    toughness = 2
    oracleText =
        "Disturb {3}{W} (You may cast this card from your graveyard transformed for its disturb cost.)"

    disturb("{3}{W}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Cristi Balanescu"
        flavorText = "He swore to protect the Moorlands. A little thing like death was no excuse " +
            "to break his oath."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f88e269e-ff3d-4775-8520-5b7a6dddf23d.jpg?1783924937"
    }
}

private val DrogskolArmaments = card("Drogskol Armaments") {
    manaCost = ""
    colorIdentity = "W"
    colorIndicator = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2.\n" +
        "If Drogskol Armaments would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
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
        collectorNumber = "10"
        artist = "Cristi Balanescu"
        flavorText = "When he could no longer carry on, he imparted his strength to the one who " +
            "took up his duty."
        imageUri = "https://cards.scryfall.io/normal/back/f/8/f88e269e-ff3d-4775-8520-5b7a6dddf23d.jpg?1783924937"
    }
}

val DrogskolInfantry: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = DrogskolInfantryFront,
    backFace = DrogskolArmaments,
)
