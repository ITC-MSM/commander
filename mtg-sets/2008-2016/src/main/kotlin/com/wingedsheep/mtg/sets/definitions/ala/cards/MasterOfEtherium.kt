package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Master of Etherium
 * {2}{U}
 * Artifact Creature — Vedalken Wizard
 * * / *
 * Master of Etherium's power and toughness are each equal to the number of artifacts you control.
 * Other artifact creatures you control get +1/+1.
 *
 * Two halves that must not be confused. The P/T is a characteristic-defining ability (CR 604.3):
 * `dynamicStats` over [DynamicAmount.AggregateBattlefield], no printed base P/T, and it counts
 * *artifacts* — the Master itself included, since it is an artifact on the battlefield. The lord is
 * an ordinary layer-7c static: [ModifyStats] over a [GroupFilter] of
 * `GameObjectFilter.ArtifactCreature.youControl()` with `excludeSelf = true` for the printed
 * "other", so the Master never pumps itself on top of its own CDA.
 */
val MasterOfEtherium = card("Master of Etherium") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Vedalken Wizard"
    oracleText = "Master of Etherium's power and toughness are each equal to the number of artifacts you control.\n" +
        "Other artifact creatures you control get +1/+1."

    dynamicStats(
        DynamicAmount.AggregateBattlefield(
            Player.You,
            GameObjectFilter.Artifact
        )
    )

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.ArtifactCreature.youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "49"
        artist = "Matt Cavotta"
        flavorText = "\"Only a mind unfettered with the concerns of the flesh can see the world as it truly is.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/2/322cdf67-5b36-43f9-99b3-f0e24d423314.jpg"
    }
}
