package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Defiant Survivor
 * {2}{G}
 * Creature — Human Survivor
 * 3/2
 *
 * Survival — At the beginning of your second main phase, if this creature is tapped, manifest
 * dread. (Look at the top two cards of your library. Put one onto the battlefield face down as a
 * 2/2 creature and the other into your graveyard. Turn it face up any time for its mana cost if
 * it's a creature card.)
 *
 * Survival is an intervening-"if" postcombat-main trigger gated on the source being tapped, like
 * the other DSK Survival creatures. The payoff is [Patterns.Library.manifestDread].
 */
val DefiantSurvivor = card("Defiant Survivor") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Survivor"
    power = 3
    toughness = 2
    oracleText = "Survival — At the beginning of your second main phase, if this creature is " +
        "tapped, manifest dread. (Look at the top two cards of your library. Put one onto the " +
        "battlefield face down as a 2/2 creature and the other into your graveyard. Turn it face " +
        "up any time for its mana cost if it's a creature card.)"

    // Survival — At the beginning of your second main phase, if this creature is tapped, manifest dread.
    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        effect = Patterns.Library.manifestDread()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "Jessica Fong"
        flavorText = "\"The dark should be scared of me!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/2/327772f3-5a87-47af-9308-c1119ad2711d.jpg?1726286512"
    }
}
