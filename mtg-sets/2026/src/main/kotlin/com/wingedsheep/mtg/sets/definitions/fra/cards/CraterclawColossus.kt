package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

private const val ARTIFACT_COUNT = "craterclawArtifactCount"

val CraterclawColossus = card("Craterclaw Colossus") {
    manaCost = "{4}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Beast Construct"
    power = 5
    toughness = 5
    oracleText = "Haste\n" +
        "When this creature enters, creatures you control gain trample and get +X/+0 until end of turn, " +
        "where X is the number of artifacts you control."

    keywords(Keyword.HASTE)

    // X is counted once on resolution, then one pass over the group carries both the pump and
    // the trample grant (Overrun's shape — see Patterns.Group.pumpAndGrantToAll).
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.StoreNumber(
                ARTIFACT_COUNT,
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count()
            ),
            Effects.ForEachInGroup(
                GroupFilter.AllCreaturesYouControl,
                Effects.Composite(
                    Effects.ModifyStats(
                        DynamicAmount.VariableReference(ARTIFACT_COUNT),
                        DynamicAmount.Fixed(0),
                        EffectTarget.Self
                    ),
                    Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "78"
        artist = "Mark Zug"
        flavorText = "Some puppetbeasts are monstrous machines. Others, works of art. The best are both."
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47793a51-08c6-4ad2-a7e5-a4484d83a5cd.jpg?1788329298"
        inBooster = false
    }
}
