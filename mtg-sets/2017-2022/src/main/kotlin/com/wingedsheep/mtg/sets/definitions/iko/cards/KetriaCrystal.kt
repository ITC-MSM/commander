package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Ketria Crystal
 * {3}
 * Artifact
 * {T}: Add {G}, {U}, or {R}.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * An artifact has no basic land subtypes to derive mana from, so the "or" between the three colors
 * is spelled as three separate {T} mana abilities.
 */
val KetriaCrystal = card("Ketria Crystal") {
    manaCost = "{3}"
    colorIdentity = "GRU"
    typeLine = "Artifact"
    oracleText = "{T}: Add {G}, {U}, or {R}.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "Yeong-Hao Han"
        flavorText = "The dark of night never truly falls on Ketria, where abundant crystals bathe the land in their glow."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7df435cb-3aeb-490a-8fca-91f3b6936965.jpg"
    }
}
