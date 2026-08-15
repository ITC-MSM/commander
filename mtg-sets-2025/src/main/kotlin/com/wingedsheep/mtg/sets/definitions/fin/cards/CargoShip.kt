package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Cargo Ship
 * {1}{U}
 * Artifact — Vehicle
 * 2/3
 * Flying, vigilance
 * {T}: Add {C}. Spend this mana only to cast an artifact spell or activate an ability of
 *   an artifact source.
 * Crew 1
 *
 * Modeling: a tap mana ability producing {C} restricted to artifact spells/abilities
 * ([ManaRestriction.CardTypeSpellsOrAbilitiesOnly] with both spells and abilities allowed),
 * plus the parameterized crew keyword ability.
 */
val CargoShip = card("Cargo Ship") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Vehicle"
    power = 2
    toughness = 3
    oracleText = "Flying, vigilance\n" +
        "{T}: Add {C}. Spend this mana only to cast an artifact spell or activate an ability of an artifact source.\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(
            1,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.ARTIFACT,
                allowSpells = true,
                allowAbilities = true,
            ),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}. Spend this mana only to cast an artifact spell or activate an ability of an artifact source."
    }

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Thanh Tuấn"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/932b865c-bfe7-4bb7-82e9-2403cf0e0522.jpg?1748705931"
    }
}
