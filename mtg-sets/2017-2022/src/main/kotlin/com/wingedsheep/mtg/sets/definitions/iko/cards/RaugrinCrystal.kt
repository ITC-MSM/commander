package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Raugrin Crystal
 * {3}
 * Artifact
 * {T}: Add {U}, {R}, or {W}.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Add {U}, {R}, or {W}" is three separate mana abilities that happen to share a tap cost, not one
 * ability with a choice inside it — the same shape the dual and tri lands use. Each is a
 * [TimingRule.ManaAbility] ability flagged `manaAbility`, so none of them uses the stack (CR 605.1a).
 */
val RaugrinCrystal = card("Raugrin Crystal") {
    manaCost = "{3}"
    colorIdentity = "RUW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {U}, {R}, or {W}.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    keywordAbility(KeywordAbility.cycling("{2}"))

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

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Kirsten Zirngibl"
        flavorText = "The vegetation in Raugrin is dull, but that only makes the towering crystal structures gleam brighter."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5bce0fb-53d6-47ee-8c5a-a99e50f67fc9.jpg"
    }
}
