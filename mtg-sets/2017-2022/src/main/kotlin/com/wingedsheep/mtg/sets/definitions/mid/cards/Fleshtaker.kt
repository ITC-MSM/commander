package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fleshtaker
 * {W}{B}
 * Creature — Human Assassin
 * 2/2
 * Whenever you sacrifice another creature, you gain 1 life and scry 1.
 * {1}, Sacrifice another creature: This creature gets +2/+2 until end of turn.
 *
 * The sacrifice trigger uses the batched [Triggers.YouSacrificeOneOrMore] over creatures (the
 * established engine idiom for "whenever you sacrifice another creature" — Rakdos, the Muscle),
 * gaining 1 life then scrying 1. The activated pump costs {1} plus sacrificing another creature
 * ([Costs.SacrificeAnother]).
 */
val Fleshtaker = card("Fleshtaker") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Human Assassin"
    power = 2
    toughness = 2
    oracleText = "Whenever you sacrifice another creature, you gain 1 life and scry 1. (Look at the " +
        "top card of your library. You may put that card on the bottom.)\n" +
        "{1}, Sacrifice another creature: This creature gets +2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(GameObjectFilter.Creature)
        effect = Effects.GainLife(1).then(Effects.Scry(1))
        description = "Whenever you sacrifice another creature, you gain 1 life and scry 1."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "This creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Kev Walker"
        flavorText = "A soulless husk of endless hunger."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2ed8c0ef-2b53-49b5-bc2f-428628cb3975.jpg?1782703584"
    }
}
