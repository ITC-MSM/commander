package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Incandescent Soulstoke
 * {2}{R}
 * Creature — Elemental Shaman
 * 2/2
 * Other Elemental creatures you control get +1/+1.
 * {1}{R}, {T}: You may put an Elemental creature card from your hand onto the battlefield. That
 * creature gains haste until end of turn. Sacrifice it at the beginning of the next end step.
 *
 * The cheat-into-play half is the Through the Breach / Meek Attack shape, narrowed to Elementals:
 * `Patterns.Hand.putFromHand` gathers the legal cards, offers a `ChooseUpTo(1)` selection — which
 * is where the "you may" lives, so a player who declines simply selects nothing — and moves what
 * was picked. The rider then runs under [ConditionalOnCollectionEffect] so nothing fires when the
 * selection was empty.
 *
 * Unlike Through the Breach, the haste here **is** bounded: the oracle text says "until end of
 * turn", so it is [Duration.EndOfTurn], not [Duration.Permanent]. In practice the creature is
 * sacrificed at the same end step either way, but a creature that dodges the sacrifice (its
 * controller changes, or it leaves and returns as a new object) must not keep haste.
 *
 * The lord is `excludeSelf = true` — the Soulstoke is itself an Elemental and "other" excludes it —
 * and it pumps an Elemental *creature*, so a Kindred noncreature Elemental such as Hoofprints of
 * the Stag is untouched by it.
 */
val IncandescentSoulstoke = card("Incandescent Soulstoke") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Shaman"
    power = 2
    toughness = 2
    oracleText = "Other Elemental creatures you control get +1/+1.\n" +
        "{1}{R}, {T}: You may put an Elemental creature card from your hand onto the battlefield. " +
        "That creature gains haste until end of turn. Sacrifice it at the beginning of the next end step."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.ELEMENTAL).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap)
        effect = Patterns.Hand.putFromHand(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.ELEMENTAL),
            prompt = "Put an Elemental creature card onto the battlefield"
        ).then(
            ConditionalOnCollectionEffect(
                collection = "putting",
                ifNotEmpty = Effects.Composite(
                    Effects.GrantKeyword(
                        keyword = Keyword.HASTE,
                        target = EffectTarget.PipelineTarget("putting", 0),
                        duration = Duration.EndOfTurn
                    ),
                    CreateDelayedTriggerEffect(
                        step = Step.END,
                        effect = Effects.SacrificeTarget(EffectTarget.PipelineTarget("putting", 0))
                    )
                )
            )
        )
        description = "Put an Elemental creature card from your hand onto the battlefield. It gains " +
            "haste until end of turn. Sacrifice it at the beginning of the next end step."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "178"
        artist = "Todd Lockwood"
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c8829b3c-5267-44c6-b709-c2dfc683b0a8.jpg?1783942873"
    }
}
