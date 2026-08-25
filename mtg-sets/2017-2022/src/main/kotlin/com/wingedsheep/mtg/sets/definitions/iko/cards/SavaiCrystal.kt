package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Savai Crystal
 * {3}
 * Artifact
 * {T}: Add {R}, {W}, or {B}.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The "or" mana line is three separate mana abilities, one per colour — the player picks which
 * ability to activate rather than choosing inside a single one.
 */
val SavaiCrystal = card("Savai Crystal") {
    manaCost = "{3}"
    colorIdentity = "BRW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {R}, {W}, or {B}.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "239"
        artist = "Daniel Ljunggren"
        flavorText = "The same crystals that root deep enough to pierce Savai's catacombs also soar high enough to change its weather."
        imageUri = "https://cards.scryfall.io/normal/front/6/9/6954a5c1-89b1-4edd-814e-8f88fd49cda3.jpg"
    }
}
