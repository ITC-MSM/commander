package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Heritage Reclamation
 * {1}{G}
 * Instant
 *
 * Choose one —
 * • Destroy target artifact.
 * • Destroy target enchantment.
 * • Exile up to one target card from a graveyard. Draw a card.
 */
val HeritageReclamation = card("Heritage Reclamation") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target artifact.\n" +
        "• Destroy target enchantment.\n" +
        "• Exile up to one target card from a graveyard. Draw a card."

    spell {
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.Destroy(EffectTarget.ContextTarget(0)),
                Targets.Artifact,
                "Destroy target artifact"
            ),
            Mode.withTarget(
                Effects.Destroy(EffectTarget.ContextTarget(0)),
                Targets.Enchantment,
                "Destroy target enchantment"
            ),
            // "Exile up to one target card from a graveyard. Draw a card."
            // The exile target is optional (up to one); the draw happens unconditionally.
            Mode(
                effect = Effects.Exile(EffectTarget.ContextTarget(0))
                    .then(Effects.DrawCards(1)),
                targetRequirements = listOf(
                    TargetObject(filter = Targets.Unified.cardInGraveyard, optional = true)
                ),
                description = "Exile up to one target card from a graveyard. Draw a card."
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Konstantin Porubov"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f8fee37-a050-4329-8b10-46d150e7a95e.jpg?1743204546"
    }
}
