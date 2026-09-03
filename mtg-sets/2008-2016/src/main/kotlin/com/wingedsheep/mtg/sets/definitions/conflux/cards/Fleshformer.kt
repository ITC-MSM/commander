package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fleshformer
 * {2}{B}
 * Creature — Human Wizard
 * 2 / 2
 * {W}{U}{B}{R}{G}: This creature gets +2/+2 and gains fear until end of turn. Target creature
 * gets -2/-2 until end of turn. Activate only during your turn.
 *
 * One activated ability, not two: the whole printed block is a single {W}{U}{B}{R}{G} activation.
 * The effect keeps the printed sentence structure — the first sentence's two clauses ("gets +2/+2
 * *and* gains fear") are chained together and then composed with the second sentence's -2/-2, so
 * the self-pump and the target's shrink stay one atomic resolution. "This creature" is
 * [EffectTarget.Self]; only the -2/-2 half takes a target. "Activate only during your turn" is
 * [ActivationRestriction.OnlyDuringYourTurn], which the activation-legality check reads — it is
 * not expressible as a timing rule.
 */
val Fleshformer = card("Fleshformer") {
    manaCost = "{2}{B}"
    colorIdentity = "BGRUW"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{W}{U}{B}{R}{G}: This creature gets +2/+2 and gains fear until end of turn. " +
        "Target creature gets -2/-2 until end of turn. Activate only during your turn. " +
        "(A creature with fear can't be blocked except by artifact creatures and/or black creatures.)"

    activatedAbility {
        cost = Costs.Mana("{W}{U}{B}{R}{G}")
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, EffectTarget.Self)
                .then(Effects.GrantKeyword(Keyword.FEAR, EffectTarget.Self)),
            Effects.ModifyStats(-2, -2, t)
        )
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Dave Kendall"
        flavorText = "Necromancers who discovered the new sources of mana were quick to dream up new nightmares with them."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00c57090-c1fe-4100-a03c-95607074280e.jpg"
    }
}
