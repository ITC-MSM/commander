package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Loxodon Hierarch
 * {2}{G}{W}
 * Creature — Elephant Cleric
 * 4/4
 *
 * When this creature enters, you gain 4 life.
 * {G}{W}, Sacrifice this creature: Regenerate each creature you control.
 *
 * "Regenerate each creature you control" is [Effects.ForEachInGroup] over the creatures you
 * control with a body of `RegenerateEffect(EffectTarget.Self)` — `IterationSpace.Group` binds the
 * current permanent as the body's `Self`, so the single-target regeneration facade applies once
 * per creature. That composition is what the ruling below describes: each creature gets its own
 * regeneration shield, spent independently and expiring at end of turn, rather than one shared
 * shield for the board.
 *
 * The Hierarch never regenerates itself. `Costs.SacrificeSelf` is paid on activation, so it is
 * already in the graveyard when the ability resolves and the group snapshot is taken.
 */
val LoxodonHierarch = card("Loxodon Hierarch") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elephant Cleric"
    oracleText = "When this creature enters, you gain 4 life.\n" +
        "{G}{W}, Sacrifice this creature: Regenerate each creature you control."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}{W}"), Costs.SacrificeSelf)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            RegenerateEffect(EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "214"
        artist = "Kev Walker"
        flavorText = "\"I have lived long, and I remember how this city once was. If my death " +
            "serves to bring back the Ravnica in my memory, then so be it.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1d3ccca-8040-4a5f-9f2b-6fdb1a033234.jpg?1783943619"
        ruling(
            "2005-10-01",
            "The second ability sets up individual regeneration shields for each creature you " +
                "control. They are each used up as necessary during the turn and wear off when the " +
                "turn ends."
        )
    }
}
