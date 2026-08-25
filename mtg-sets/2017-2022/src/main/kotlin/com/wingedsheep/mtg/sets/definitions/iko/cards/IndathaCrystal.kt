package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Indatha Crystal
 * {3}
 * Artifact
 * {T}: Add {W}, {B}, or {G}.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Add {W}, {B}, or {G}" is three separate mana abilities, one per colour — the player picks
 * which to activate, so there is no resolution-time choice to model.
 */
val IndathaCrystal = card("Indatha Crystal") {
    manaCost = "{3}"
    colorIdentity = "BGW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {W}, {B}, or {G}.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Raoul Vitale"
        flavorText = "Crystals nestle in the roots of Indatha's helica trees as if being nurtured by their embrace."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdace59f-f025-4717-8eb4-3d1e13b31d2b.jpg"
    }
}
