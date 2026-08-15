package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Oaken Siren
 * {1}{U}
 * Artifact Creature — Siren Pirate
 * 1/2
 * Common — LCI #66
 *
 * Flying, vigilance
 * {T}: Add {U}. Spend this mana only to cast an artifact spell or activate an ability of an
 *      artifact source.
 *
 * Mirrors Ixalli's Lorekeeper's restricted-mana pattern, but adds a fixed {U} (not any color)
 * and keys the restriction to a *card type* instead of a subtype:
 * [ManaRestriction.CardTypeSpellsOrAbilitiesOnly](CardType.ARTIFACT) with both `allowSpells`
 * and `allowAbilities` true, so the mana pays for artifact spells and abilities of artifact
 * sources — matching the printed oracle exactly.
 */
val OakenSiren = card("Oaken Siren") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Siren Pirate"
    power = 1
    toughness = 2
    oracleText = "Flying, vigilance\n{T}: Add {U}. Spend this mana only to cast an artifact spell or activate an ability of an artifact source."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    // {T}: Add {U}. Spend this mana only to cast an artifact spell or activate an ability of
    //      an artifact source.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(
            color = Color.BLUE,
            amount = 1,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.ARTIFACT,
                allowSpells = true,
                allowAbilities = true,
            )
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Lars Grant-West"
        flavorText = "The ship's carpenter adorned the figurehead with a small cosmium gem, never expecting the figurehead to take flight."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7731ef5-da74-4436-8ee7-01c065cbefae.jpg?1782694558"
    }
}
