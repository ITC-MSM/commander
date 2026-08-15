package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction

/**
 * Rip, Spawn Hunter — Duskmourn: House of Horror #228
 * {2}{G}{W} · Legendary Creature — Human Survivor · 4/4
 *
 * Survival — At the beginning of your second main phase, if Rip is tapped, reveal the top X
 * cards of your library, where X is its power. Put any number of creature and/or Vehicle cards
 * with different powers from among them into your hand. Put the rest on the bottom of your
 * library in a random order.
 *
 * "Survival" is the DSK ability word — mechanically an intervening-"if" postcombat-main trigger
 * gated on the source being tapped (`Triggers.YourPostcombatMain` + `Conditions.SourceIsTapped`),
 * exactly like the other DSK Survival creatures.
 *
 * The payoff is a pure Gather → Select → Move pipeline over existing atoms:
 *  - reveal the top X cards, where X = Rip's power (`CardSource.TopOfLibrary(sourcePower())`);
 *  - the controller keeps any number of creature/Vehicle cards (`GameObjectFilter.CreatureOrVehicle`)
 *    with *different powers* — modeled by the `SelectionRestriction.OnePerPower` restriction on a
 *    `chooseAnyNumberSplit`, which caps the selection at one card per distinct printed power and
 *    rejects any duplicate-power picks server-side;
 *  - the kept cards go to hand; the remainder (unpicked cards plus every non-creature/non-Vehicle
 *    card revealed) goes to the bottom of the library in a random order (`CardOrder.Random`).
 *
 * No new pipeline step is needed: `CardOrder.Random` to the library bottom and a dynamic
 * `TopOfLibrary` count already exist; only `OnePerPower` is new SDK vocabulary.
 */
val RipSpawnHunter = card("Rip, Spawn Hunter") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Survivor"
    power = 4
    toughness = 4
    oracleText = "Survival — At the beginning of your second main phase, if Rip is tapped, " +
        "reveal the top X cards of your library, where X is its power. Put any number of " +
        "creature and/or Vehicle cards with different powers from among them into your hand. " +
        "Put the rest on the bottom of your library in a random order."

    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        effect = Effects.Pipeline {
            val revealed = gather(
                source = CardSource.TopOfLibrary(DynamicAmounts.sourcePower()),
                revealed = true,
            )
            val split = chooseAnyNumberSplit(
                from = revealed,
                filter = GameObjectFilter.CreatureOrVehicle,
                restrictions = listOf(SelectionRestriction.OnePerPower),
                prompt = "Put any number of creature and/or Vehicle cards with different powers into your hand",
                selectedLabel = "Into your hand",
                remainderLabel = "Bottom of library (random order)",
            )
            toHand(split.selected)
            toLibraryBottom(split.remainder, order = CardOrder.Random)
        }
        description = "Survival — At the beginning of your second main phase, if Rip is tapped, " +
            "reveal the top X cards of your library, where X is its power. Put any number of " +
            "creature and/or Vehicle cards with different powers from among them into your hand. " +
            "Put the rest on the bottom of your library in a random order."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Justine Cruz"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f12e3b3-a260-4913-b13a-7fbb753dd702.jpg"
    }
}
