package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Explosive Entry — Kamigawa: Neon Dynasty #139 (canonical printing)
 * {1}{R} · Sorcery
 *
 * Destroy up to one target artifact. Put a +1/+1 counter on up to one target creature.
 *
 * Both targets are "up to one", so the spell is castable with an empty board and never fizzles for
 * lack of a legal artifact — that is what lets it be a maindeckable artifact answer in a set full
 * of them.
 */
val ExplosiveEntry = card("Explosive Entry") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy up to one target artifact. Put a +1/+1 counter on up to one target creature."

    spell {
        val artifact = target(
            "artifact to destroy",
            TargetObject(optional = true, filter = TargetFilter.Artifact),
        )
        val creature = target("creature to grow", TargetCreature(optional = true))
        effect = Effects.Destroy(artifact) then Effects.AddCounters("+1/+1", 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Marta Nael"
        flavorText = "Kaito and Tamiyo charged headlong into Jin-Gitaxias's secret lab, unaware " +
            "that the praetor was eagerly awaiting their arrival."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7ba639ff-fe82-4ac3-9fb4-eac168bef053.jpg?1783923869"
    }
}
