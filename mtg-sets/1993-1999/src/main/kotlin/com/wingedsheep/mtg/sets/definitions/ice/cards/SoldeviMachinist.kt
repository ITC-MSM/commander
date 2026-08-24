package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Soldevi Machinist
 * {1}{U}
 * Creature — Human Wizard Artificer
 * 1/1
 *
 * {T}: Add {C}{C}. Spend this mana only to activate abilities of artifacts.
 *
 * The spend restriction is the axis, not a second effect: [ManaRestriction.CardTypeSpellsOrAbilitiesOnly]
 * with `allowSpells = false` is exactly "abilities of artifacts and nothing else" — the narrower half
 * of the same value Cargo Ship uses with both halves allowed. The mana itself is one
 * [Effects.AddColorlessMana] of 2, and `manaAbility = true` keeps it activatable mid-payment.
 */
val SoldeviMachinist = card("Soldevi Machinist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard Artificer"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {C}{C}. Spend this mana only to activate abilities of artifacts."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(
            2,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.ARTIFACT,
                allowSpells = false,
                allowAbilities = true,
            ),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}{C}. Spend this mana only to activate abilities of artifacts."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Jeff A. Menges"
        flavorText = "\"Perhaps this time the power of the artificers shall be used wisely.\"\n—Arcum Dagsson, Soldevi Machinist"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f0999df-2f94-499e-b9af-fe377d515400.jpg"
    }
}
