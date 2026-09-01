package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Mammoth Growth
 * {2}{G}
 * Instant
 * Target creature gets +4/+4 until end of turn.
 * Foretell {G} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A Giant Growth variant whose foretold cost is a single {G}, which is what makes it a real combat
 * trick rather than a three-mana one.
 */
val MammothGrowth = card("Mammoth Growth") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +4/+4 until end of turn.\n" +
        "Foretell {G} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        val recipient = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(4, 4, recipient)
    }

    keywordAbility(KeywordAbility.foretell("{G}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Ilse Gort"
        flavorText = "Thus the longhall became a flathall."
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e55041f-69a5-4bcf-899f-a4b44c208b4d.jpg"
    }
}
