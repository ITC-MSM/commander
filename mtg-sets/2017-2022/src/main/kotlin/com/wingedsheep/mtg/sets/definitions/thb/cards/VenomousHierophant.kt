package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Venomous Hierophant
 * {3}{B}
 * Creature — Gorgon Cleric
 * 3/3
 *
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 * When this creature enters, mill three cards. (Put the top three cards of your library into your graveyard.)
 *
 * A self-mill: [Patterns.Library.mill] with its default controller scope, so the gather and the
 * move both read `Player.You`. The single printed clause means the recipe's own composite is the
 * trigger's whole effect — no outer wrapper.
 */
val VenomousHierophant = card("Venomous Hierophant") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Gorgon Cleric"
    power = 3
    toughness = 3
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)\n" +
        "When this creature enters, mill three cards. (Put the top three cards of your library into your graveyard.)"

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Johannes Voss"
        flavorText = "\"Many have sought snake-twined Pharika's panacea. Do you wish to share their fate?\""
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9dc2b661-2f42-419d-837f-bbf097c1153c.jpg"
    }
}
