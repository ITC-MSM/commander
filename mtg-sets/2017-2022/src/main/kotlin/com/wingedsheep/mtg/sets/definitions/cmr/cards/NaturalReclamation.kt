package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Natural Reclamation
 * {4}{G}
 * Instant
 * Cascade (When you cast this spell, exile cards from the top of your library until you exile a nonland card that costs less. You may cast it without paying its mana cost. Put the exiled cards on the bottom in a random order.)
 * Destroy target artifact or enchantment.
 *
 * A Disenchant body — [Effects.Destroy] on [Targets.ArtifactOrEnchantment] — priced up to make room
 * for cascade. [Keyword.CASCADE] is display-only, so as on `cmr/cards/AnnoyedAltisaur.kt` the
 * keyword only prints the line and the real work is a [Triggers.WhenYouCastThisSpell] trigger
 * feeding [Effects.Cascade]; cascade fires on the cast, so an instant carries it unchanged.
 */
val NaturalReclamation = card("Natural Reclamation") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Cascade (When you cast this spell, exile cards from the top of your library " +
        "until you exile a nonland card that costs less. You may cast it without paying its mana " +
        "cost. Put the exiled cards on the bottom in a random order.)\n" +
        "Destroy target artifact or enchantment."

    keywords(Keyword.CASCADE)

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    // Cascade — the cast trigger the keyword abbreviates.
    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "245"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e98fb7c-6962-4b57-b7b7-c71899564002.jpg?1783928787"
    }
}
