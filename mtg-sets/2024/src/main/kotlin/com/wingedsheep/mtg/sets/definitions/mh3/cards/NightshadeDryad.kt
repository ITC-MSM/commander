package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Nightshade Dryad
 * {1}{G}
 * Creature — Dryad
 * 1/2
 *
 * Deathtouch
 * {T}: Add {C}.
 * {T}: Add one mana of any color.
 *
 * Two *separate* mana abilities that share the same {T} cost — not one ability with a choice. Only
 * one can ever be activated per untap, but they're independent, so each is its own
 * `activatedAbility` with `manaAbility = true` / `TimingRule.ManaAbility` (the pair that keeps them
 * out of the stack, CR 605.3a, and available inside the mana-payment window). The any-color half is
 * the shared [Effects.AddManaOfChoice] primitive, whose default `ManaColorSet.AnyColor` is exactly
 * "one mana of any color".
 */
val NightshadeDryad = card("Nightshade Dryad") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad"
    power = 1
    toughness = 2
    oracleText = "Deathtouch\n{T}: Add {C}.\n{T}: Add one mana of any color."

    keywords(Keyword.DEATHTOUCH)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Bastien L. Deharme"
        flavorText = "\"Death is a natural part of life. Without the mulch to feed new sprouts, " +
            "everything withers.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71f9252d-241f-45ea-9d80-663150963b59.jpg?1783911259"
    }
}
