package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hoofprints of the Stag
 * {1}{W}
 * Kindred Enchantment — Elemental
 * Whenever you draw a card, you may put a hoofprint counter on this enchantment.
 * {2}{W}, Remove four hoofprint counters from this enchantment: Create a 4/4 white Elemental
 * creature token with flying. Activate only during your turn.
 *
 * `hoofprint` is a new passive counter type — no inherent rule, purely this card's own tally — so
 * it needs its `CounterType` enum entry alongside the `Counters` constant. Without the enum entry
 * every counter read silently falls back to counting **+1/+1** counters (`resolveCounterType`
 * catches the `valueOf` failure and defaults), which fails open rather than loudly.
 *
 * Two details the wording forces:
 *
 * - The draw trigger fires **per card drawn** (2007-10-01 ruling), which is what
 *   [Triggers.YouDraw] already does — a "draw three cards" spell gives three separate triggers,
 *   each with its own "you may". [MayEffect] is that optionality; declining one doesn't decline
 *   the rest.
 * - "Activate only during your turn" is a timing restriction on the ability, not sorcery speed:
 *   [ActivationRestriction.OnlyDuringYourTurn] permits activation during your own upkeep or in
 *   response to a spell on your turn, which sorcery-speed timing would not.
 *
 * Being a Kindred Elemental with an activated ability, this enchantment is an *Elemental permanent*
 * for cards that count them — activating it triggers Ceaseless Searblades, while the creature-only
 * Incandescent Soulstoke lord does not pump it.
 */
val HoofprintsOfTheStag = card("Hoofprints of the Stag") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Kindred Enchantment — Elemental"
    oracleText = "Whenever you draw a card, you may put a hoofprint counter on this enchantment.\n" +
        "{2}{W}, Remove four hoofprint counters from this enchantment: Create a 4/4 white Elemental " +
        "creature token with flying. Activate only during your turn."

    triggeredAbility {
        trigger = Triggers.YouDraw
        effect = MayEffect(
            AddCountersEffect(
                counterType = Counters.HOOFPRINT,
                count = 1,
                target = EffectTarget.Self
            )
        )
        description = "Whenever you draw a card, you may put a hoofprint counter on this enchantment."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{W}"),
            Costs.RemoveCounterFromSelf(Counters.HOOFPRINT, 4)
        )
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Elemental"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/1/c/1c791044-be86-47d8-8c1b-299f6ab254e6.jpg?1783942839",
        )
        description = "Create a 4/4 white Elemental creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Anthony S. Waters"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d11187c6-f213-45db-9de3-314df1f43589.jpg?1783942914"
        ruling(
            "2007-10-01",
            "If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times."
        )
    }
}
