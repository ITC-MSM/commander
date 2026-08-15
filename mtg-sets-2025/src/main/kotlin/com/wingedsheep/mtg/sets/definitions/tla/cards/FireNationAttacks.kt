package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Fire Nation Attacks
 * {4}{R}
 * Instant
 *
 * Create two 2/2 red Soldier creature tokens with firebending 1. (Whenever a creature with
 * firebending 1 attacks, add {R}. This mana lasts until end of combat.)
 * Flashback {8}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 *
 * The tokens carry plain firebending 1, modeled via the [firebending] DSL on the inline token
 * definition ([firebendingSoldierToken]) — firebending is a display-only keyword backed by an
 * attack-triggered "add {R} (until end of combat)" ability, so each token gets the
 * [Keyword.FIREBENDING] keyword plus that triggered ability (sourced from the token def). One
 * [CreateTokenEffect] with `count = 2` mints both Soldiers. Flashback is the standard
 * [KeywordAbility.flashback] alternative-cast keyword.
 */
private val firebendingSoldierToken = card("Soldier") {
    typeLine = "Token Creature — Soldier"
    colorIdentity = "R"
    power = 2
    toughness = 2
    firebending(1)
}

val FireNationAttacks = card("Fire Nation Attacks") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Create two 2/2 red Soldier creature tokens with firebending 1. " +
        "(Whenever a creature with firebending 1 attacks, add {R}. This mana lasts until end of combat.)\n" +
        "Flashback {8}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(2),
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Soldier"),
            keywords = setOf(Keyword.FIREBENDING),
            triggeredAbilities = firebendingSoldierToken.triggeredAbilities,
            imageUri = "https://cards.scryfall.io/normal/front/2/d/2de43b03-9ac5-4292-ab29-2dc6210ef3d9.jpg?1777982247"
        )
    }

    keywordAbility(KeywordAbility.flashback("{8}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Claudiu-Antoniu Magherusan"
        flavorText = "Everything changed."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cc10845-8989-46ca-a57a-fd728fca0729.jpg?1764120914"
    }
}
