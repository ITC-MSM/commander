package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Fade into Antiquity — Theros #157 (canonical printing)
 * {2}{G} · Sorcery
 *
 * Exile target artifact or enchantment.
 *
 * Green's unconditional artifact/enchantment answer, printed again in Kamigawa: Neon Dynasty.
 * Exile rather than destroy, so an indestructible or recursive target stays gone.
 */
val FadeIntoAntiquity = card("Fade into Antiquity") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Exile target artifact or enchantment."

    spell {
        val t = target(
            "artifact or enchantment",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Enchantment),
            ),
        )
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Noah Bradley"
        flavorText = "\"Are the gods angry at our discontent with what they give us, or jealous " +
            "that we made a thing they cannot?\"\n—Kleon the Iron-Booted"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e43e46a6-f7de-482a-a386-73932d1d9002.jpg?1783939747"
    }
}
