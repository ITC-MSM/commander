package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Start from Scratch — Strixhaven: School of Mages #114 (canonical printing)
 * {2}{R} · Sorcery — Lesson
 *
 * Choose one —
 * • Start from Scratch deals 1 damage to any target.
 * • Destroy target artifact.
 *
 * An ordinary "choose one" `modal` with per-mode targeting: the first mode is [Effects.DealDamage]
 * at [Targets.Any], the second [Effects.Destroy] at [Targets.Artifact]. Lesson is only a subtype.
 */
val StartFromScratch = card("Start from Scratch") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Choose one —\n" +
        "• Start from Scratch deals 1 damage to any target.\n" +
        "• Destroy target artifact."

    spell {
        modal {
            mode("Start from Scratch deals 1 damage to any target") {
                val victim = target("target", Targets.Any)
                effect = Effects.DealDamage(1, victim)
            }
            mode("Destroy target artifact") {
                val artifact = target("target", Targets.Artifact)
                effect = Effects.Destroy(artifact)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "114"
        artist = "Bayard Wu"
        flavorText = "\"I don't care what they think!\" Rootha snapped. \"I know I can do better.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55c99486-ae64-4293-81fb-a4b02e8fcae6.jpg?1783927350"
    }
}
