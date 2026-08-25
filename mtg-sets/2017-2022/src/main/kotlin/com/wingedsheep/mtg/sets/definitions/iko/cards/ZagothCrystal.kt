package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Zagoth Crystal
 * {3}
 * Artifact
 * {T}: Add {B}, {G}, or {U}.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The "or" mana line is three separate mana abilities, one per colour — the player picks which
 * ability to activate rather than choosing inside a single one.
 */
val ZagothCrystal = card("Zagoth Crystal") {
    manaCost = "{3}"
    colorIdentity = "BGU"
    typeLine = "Artifact"
    oracleText = "{T}: Add {B}, {G}, or {U}.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

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

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "242"
        artist = "Raoul Vitale"
        flavorText = "From the fertile muck of Zagoth, everything—animal, botanical, and crystalline—grows with rugged beauty."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/9138a442-8e8b-465f-bb76-b6af7e6dab6f.jpg"
    }
}
