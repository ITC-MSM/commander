package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Pit of Offerings
 * Land — Cave
 *
 * This land enters tapped.
 * When this land enters, exile up to three target cards from graveyards.
 * {T}: Add {C}.
 * {T}: Add one mana of any of the exiled cards' colors.
 *
 * The exiled cards are linked to this land via `MoveToZoneEffect(linkToSource = true)`, so the
 * fourth ability's color pool ([Effects.AddManaOfColorAmongLinkedExile] →
 * `ManaColorSet.AmongLinkedExiledCards`) is the union of the base colors of the cards still exiled
 * with this land. Colorless-only or empty piles produce no mana (per the printed rulings). Because
 * the link lives on the source permanent, a new Pit of Offerings (or this land leaving and
 * returning) is a fresh object with no remembered cards.
 */
val PitOfOfferings = card("Pit of Offerings") {
    typeLine = "Land — Cave"
    colorIdentity = ""
    oracleText = "This land enters tapped.\n" +
        "When this land enters, exile up to three target cards from graveyards.\n" +
        "{T}: Add {C}.\n" +
        "{T}: Add one mana of any of the exiled cards' colors."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "cards from graveyards",
            TargetObject(count = 3, optional = true, filter = TargetFilter.CardInGraveyard)
        )
        effect = ForEachTargetEffect(
            listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE, linkToSource = true))
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfColorAmongLinkedExile()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "278"
        artist = "Martin de Diego Sádaba"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc7d3957-b483-4a1f-a244-293c90032f5e.jpg?1782694390"
        ruling(
            "2023-11-10",
            "The five colors are white, blue, black, red, and green. The last ability of Pit of " +
                "Offerings can't produce {C}."
        )
        ruling(
            "2023-11-10",
            "If no cards are exiled with Pit of Offerings, its last ability can't add mana."
        )
    }
}
