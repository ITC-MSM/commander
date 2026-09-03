package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Incite Hysteria
 * {2}{R}
 * Sorcery
 *
 * Radiance — Until end of turn, target creature and each other creature that shares a color with
 * it gain "This creature can't block."
 *
 * Radiance: the target is restricted directly; every *other* creature sharing a color with it
 * (`sharingColorWith(EntityReference.Target(0))`, `otherThanTarget()`) is found as the spell
 * resolves and restricted too. The restriction is stamped on the creatures at resolution, so a
 * creature that changes colour before blockers are declared still can't block (2005-11-01
 * ruling), and one that becomes the target's colour afterwards is unaffected. A colorless target
 * shares a color with nothing, so only it is restricted.
 */
val InciteHysteria = card("Incite Hysteria") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Radiance — Until end of turn, target creature and each other creature that " +
        "shares a color with it gain \"This creature can't block.\""

    spell {
        val radiant = target("target creature", Targets.Creature)
        effect = Effects.CantBlock(radiant) then
            Effects.ForEachInGroup(
                GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget(),
                Effects.CantBlock(EffectTarget.Self)
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Paolo Parente"
        flavorText = "\"The Boros say they want to bring order to Ravnica. Funny then, how well " +
            "they use chaos.\"\n—Trigori, Azorius senator"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a31f5060-df9e-4665-bf60-a8e33da55c84.jpg?1783943651"
        ruling(
            "2005-11-01",
            "The targeted creature and the creatures that share a color with it at the time Incite " +
                "Hysteria resolves gain an ability that says they can't block this turn. It doesn't " +
                "matter if those creatures change colors before blockers are declared."
        )
        ruling("2005-10-01", "All creatures that share a color are affected, even your own.")
        ruling(
            "2005-10-01",
            "If it targets a colorless creature, it doesn't affect any other creatures. A colorless " +
                "creature shares a color with nothing, not even other colorless creatures."
        )
        ruling("2005-10-01", "You check which creatures share a color with the target when the spell resolves.")
        ruling(
            "2005-10-01",
            "Only one creature is targeted. If that creature leaves the battlefield or otherwise becomes " +
                "an illegal target, the entire spell doesn't resolve. No other creatures are affected."
        )
    }
}
