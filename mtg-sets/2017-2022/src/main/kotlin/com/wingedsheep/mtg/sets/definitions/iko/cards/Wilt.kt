package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Wilt
 * {1}{G}
 * Instant
 *
 * Destroy target artifact or enchantment.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The green Naturalize with a floor: [Targets.ArtifactOrEnchantment] is a single `Or` predicate, so
 * an artifact enchantment satisfies it once and only one target is chosen. Cycling (CR 702.29a) is
 * an activated ability of the card in hand, which is what makes the card castable-or-not a
 * non-decision in an artifact-light matchup.
 */
val Wilt = card("Wilt") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Volkan Baǵa"
        flavorText = "Nothing returns from the nightmare nests of Indatha unscathed."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55987bab-c24f-4261-b97e-f0ad4474b0a0.jpg"
    }
}
