package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Svogthos, the Restless Tomb
 * Land
 *
 * {T}: Add {C}.
 * {3}{B}{G}: Until end of turn, this land becomes a black and green Plant Zombie creature with
 * "This creature's power and toughness are each equal to the number of creature cards in your
 * graveyard." It's still a land.
 *
 * The granted clause is a **characteristic-defining ability**, not a size frozen at resolution, so
 * the count goes in `dynamicPower`/`dynamicToughness` (re-evaluated at Layer 7b on every
 * projection) rather than in `power`/`toughness`, which serve only as the star-P/T rules-text
 * display. That is what the 2005-10-01 ruling requires: "Svogthos's power and toughness changes
 * each time a creature card enters or leaves its controller's graveyard."
 *
 * Svogthos animates *itself*, so `you` in the CDA is its own controller — the same player the
 * animating ability's controller is — and the two never disagree.
 *
 * "It's still a land" is [Effects.BecomeCreature]'s default: CREATURE and the two subtypes are
 * added without removing LAND, so the animated Svogthos still taps for {C}. Note that it counts
 * *creature cards*, so Svogthos itself in the graveyard (a land card) never adds to the total.
 */
val SvogthosTheRestlessTomb = card("Svogthos, the Restless Tomb") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "{T}: Add {C}.\n" +
        "{3}{B}{G}: Until end of turn, this land becomes a black and green Plant Zombie creature " +
        "with \"This creature's power and toughness are each equal to the number of creature " +
        "cards in your graveyard.\" It's still a land."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{3}{B}{G}")
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = DynamicAmount.Fixed(0),
            toughness = DynamicAmount.Fixed(0),
            creatureTypes = setOf(Subtype.PLANT.value, Subtype.ZOMBIE.value),
            colors = setOf(Color.BLACK.name, Color.GREEN.name),
            duration = Duration.EndOfTurn,
            dynamicPower = DynamicAmounts.creatureCardsInYourGraveyard(),
            dynamicToughness = DynamicAmounts.creatureCardsInYourGraveyard(),
        )
        description = "{3}{B}{G}: Until end of turn, this land becomes a black and green Plant " +
            "Zombie creature with \"This creature's power and toughness are each equal to the " +
            "number of creature cards in your graveyard.\" It's still a land."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "283"
        artist = "Martina Pilcerova"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cf3ba87-93a0-44db-83d9-92e23a677a62.jpg?1783943589"
        ruling(
            "2005-10-01",
            "While animated, Svogthos's power and toughness changes each time a creature card " +
                "enters or leaves its controller's graveyard."
        )
        ruling(
            "2008-08-01",
            "A noncreature permanent that turns into a creature can attack, and its {T} abilities " +
                "can be activated, only if its controller has continuously controlled that " +
                "permanent since the beginning of their most recent turn. It doesn't matter how " +
                "long the permanent has been a creature."
        )
        ruling(
            "2009-10-01",
            "Activating the ability that turns it into a creature while it's already a creature " +
                "will override any effects that set its power and/or toughness to a specific " +
                "number. However, any effect that raises or lowers power and/or toughness (such " +
                "as the effect created by Giant Growth, Glorious Anthem, or a +1/+1 counter) will " +
                "continue to apply."
        )
    }
}
