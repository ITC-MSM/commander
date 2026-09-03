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
 * Bathe in Light
 * {1}{W}
 * Instant
 *
 * Radiance — Choose a color. Target creature and each other creature that shares a color with it
 * gain protection from the chosen color until end of turn.
 *
 * Two independent ideas that only look entangled. The **colour choice** is
 * [Effects.ChooseColorThen], which pauses for the decision and re-runs its body with the chosen
 * colour on the effect context — every `GrantProtectionFromChosenColor` under it reads that one
 * colour, so the whole radiance group is protected from the same colour (Scryfall ruling: the
 * chosen colour has nothing to do with the colour the creatures share). The **radiance group** is
 * the usual RAV shape: the target is granted directly, and every *other* creature sharing a colour
 * with it (`sharingColorWith(EntityReference.Target(0))`, `otherThanTarget()`) is gathered once at
 * resolution and granted via [Effects.ForEachInGroup] with `EffectTarget.Self` bound to each
 * iterated creature — the documented `ChooseColorThen` + `ForEachInGroup` recipe.
 *
 * A colorless target shares a colour with nothing, not even other colorless creatures, so only it
 * is protected; and because only one creature is targeted, an illegal target fizzles the whole
 * spell and no other creature is affected.
 */
val BatheInLight = card("Bathe in Light") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Radiance — Choose a color. Target creature and each other creature that shares " +
        "a color with it gain protection from the chosen color until end of turn."

    spell {
        val radiant = target("target creature", Targets.Creature)
        effect = Effects.ChooseColorThen(
            Effects.GrantProtectionFromChosenColor(radiant) then
                Effects.ForEachInGroup(
                    filter = GroupFilter(
                        GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                    ).otherThanTarget(),
                    effect = Effects.GrantProtectionFromChosenColor(EffectTarget.Self)
                )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"Truth shines even in darkness. Those who march on the side of truth walk " +
            "always in righteous light.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7bd976e-56c7-482e-8d2f-073b0b589274.jpg?1783943708"
        ruling(
            "2005-10-01",
            "The color you choose as the spell resolves has nothing to do with the color or colors " +
                "shared by the affected creatures."
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
