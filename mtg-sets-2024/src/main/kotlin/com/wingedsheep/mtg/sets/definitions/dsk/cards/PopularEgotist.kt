package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Popular Egotist
 * {2}{B}
 * Creature — Human Rogue
 * 3/2
 *
 * {1}{B}, Sacrifice another creature or enchantment: This creature gains indestructible until end
 * of turn. Tap it.
 * Whenever you sacrifice a permanent, target opponent loses 1 life and you gain 1 life.
 *
 * The activated ability's cost is mana + sacrifice another creature or enchantment
 * ([Costs.SacrificeAnother] over [GameObjectFilter.CreatureOrEnchantment]). It grants this creature
 * indestructible until end of turn ([Duration.EndOfTurn]) and taps it.
 *
 * The sacrifice trigger ([Triggers.YouSacrificeOneOrMore] over any permanent) is a batching trigger
 * that fires once per sacrifice event — including the activated ability's own sacrifice cost — and
 * drains a chosen opponent ([Targets.Opponent] loses 1 life, controller gains 1 life).
 */
val PopularEgotist = card("Popular Egotist") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    power = 3
    toughness = 2
    oracleText = "{1}{B}, Sacrifice another creature or enchantment: This creature gains " +
        "indestructible until end of turn. Tap it.\n" +
        "Whenever you sacrifice a permanent, target opponent loses 1 life and you gain 1 life."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{B}"),
            Costs.SacrificeAnother(GameObjectFilter.CreatureOrEnchantment)
        )
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self, Duration.EndOfTurn),
            Effects.Tap(EffectTarget.Self)
        )
        description = "This creature gains indestructible until end of turn. Tap it."
    }

    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(GameObjectFilter.Permanent)
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            Effects.LoseLife(1, opponent),
            GainLifeEffect(1)
        )
        description = "Whenever you sacrifice a permanent, target opponent loses 1 life and you gain 1 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "114"
        artist = "Julia Metzger"
        flavorText = "\"I know you'd want me to live, bestie!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35e64605-8edb-4def-9522-765e90d1f0f3.jpg?1726286273"
    }
}
