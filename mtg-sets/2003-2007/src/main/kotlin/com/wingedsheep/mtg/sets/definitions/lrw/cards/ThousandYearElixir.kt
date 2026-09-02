package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Thousand-Year Elixir
 * {3}
 * Artifact
 * You may activate abilities of creatures you control as though those creatures had haste.
 * {1}, {T}: Untap target creature.
 *
 * The static is deliberately *not* a haste grant (the 2007-10-01 ruling): it lifts only the
 * `{T}`/`{Q}` half of CR 302.6, never the attack half. Shang-Chi, Master of Kung Fu carries the
 * identical permission as [GrantKeyword]([AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY],
 * creatures you control), which only `SummoningSicknessRules` reads.
 */
val ThousandYearElixir = card("Thousand-Year Elixir") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "You may activate abilities of creatures you control as though those creatures " +
        "had haste.\n" +
        "{1}, {T}: Untap target creature."

    staticAbility {
        ability = GrantKeyword(
            AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY.name,
            GroupFilter.AllCreaturesYouControl,
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val t = target("target creature", Targets.Creature)
        effect = Effects.Untap(t)
        description = "Untap target creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "263"
        artist = "Richard Sardinha"
        flavorText = "Paradoxically, to tilt the massive jug for a sip, you'd need the energy of the giant's tonic."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18743fd4-2a15-40a2-ac90-e3f0fef07e37.jpg?1783942851"
        ruling("2007-10-01", "Thousand-Year Elixir doesn't actually grant haste to creatures you control, nor does it let you attack with them as though they had haste.")
    }
}
