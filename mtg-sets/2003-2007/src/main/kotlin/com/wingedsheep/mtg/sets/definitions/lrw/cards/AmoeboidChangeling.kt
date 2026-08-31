package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Amoeboid Changeling
 * {1}{U}
 * Creature — Shapeshifter
 * 1/1
 *
 * Changeling (This card is every creature type.)
 * {T}: Target creature gains all creature types until end of turn.
 * {T}: Target creature loses all creature types until end of turn.
 *
 * Both halves are Layer 4 type-changing effects; the engine's timestamp ordering decides which
 * wins when both are pointed at the same creature in one turn, which is exactly what the card
 * wants (the 2007-10-01 ruling: the later ability overwrites the earlier one).
 *
 * "Gains all creature types" is a grant of Changeling — the engine expands that keyword into every
 * creature type (CR 702.73). The losing half is the dedicated [Effects.LoseAllCreatureTypes], not
 * a keyword removal, because it must also strip printed subtypes.
 */
val AmoeboidChangeling = card("Amoeboid Changeling") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)\n" +
        "{T}: Target creature gains all creature types until end of turn.\n" +
        "{T}: Target creature loses all creature types until end of turn."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.CHANGELING, creature)
        description = "{T}: Target creature gains all creature types until end of turn."
    }

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.LoseAllCreatureTypes(creature)
        description = "{T}: Target creature loses all creature types until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Nils Hamm"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/068518d2-f061-4061-b208-158e991156b6.jpg?1783942906"
    }
}
