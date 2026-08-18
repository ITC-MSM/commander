package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Chrome Mox
 * {0}
 * Artifact
 *
 * Imprint — When this artifact enters, you may exile a nonartifact, nonland card from your hand.
 * {T}: Add one mana of any of the exiled card's colors.
 *
 * The optional hand exile is linked to Chrome Mox. Its mana ability reads the colors of the card
 * still in that linked-exile pile, so declining to exile, exiling a colorless card, or the card
 * later leaving exile leaves the ability unable to produce mana.
 */
val ChromeMox = card("Chrome Mox") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Imprint — When this artifact enters, you may exile a nonartifact, nonland card from your hand.\n" +
        "{T}: Add one mana of any of the exiled card's colors."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Patterns.Hand.revealHandAndExileChosen(
                target = EffectTarget.Controller,
                filter = GameObjectFilter.Nonland.nonartifact(),
                prompt = "Choose a nonartifact, nonland card to exile",
                revealHand = false,
                linkToSource = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfColorAmongLinkedExile()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "152"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a058e68-70af-4a64-859c-c881e5578368.jpg?1783944526"
        ruling(
            "2025-02-07",
            "If no card is exiled with this artifact, it can't add mana. The same is true if the exiled card is colorless."
        )
        ruling(
            "2025-02-07",
            "This artifact's activated ability can never add {C}, even if the exiled card is colorless or has a colorless mana symbol in its mana cost."
        )
        ruling(
            "2025-02-07",
            "If the exiled card is multicolored, you choose one of that card's colors each time you tap this artifact for mana."
        )
    }
}
