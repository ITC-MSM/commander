package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Faerie Trickery
 * {1}{U}{U}
 * Kindred Instant — Faerie
 * Counter target non-Faerie spell. If that spell is countered this way, exile it instead of
 * putting it into its owner's graveyard.
 *
 * "Non-Faerie" is a targeting restriction, so it is carried by the target filter rather than
 * checked at resolution: a Faerie spell is never a legal target in the first place. The subtype is
 * read off the spell on the stack, which means a changeling spell counts as a Faerie and can't be
 * hit — including Faerie Trickery's own Kindred Faerie siblings.
 */
val FaerieTrickery = card("Faerie Trickery") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Kindred Instant — Faerie"
    oracleText = "Counter target non-Faerie spell. If that spell is countered this way, exile it " +
        "instead of putting it into its owner's graveyard."

    spell {
        target = TargetSpell(filter = TargetFilter.SpellOnStack.notSubtype(Subtype.FAERIE))
        effect = Effects.CounterSpellToExile()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Terese Nielsen"
        flavorText = "The fae are so quick and their life spans so short that it's difficult to get retribution for their pranks."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/defb9f0b-195e-4aeb-92c1-8f827ad6724b.jpg?1783942904"
    }
}
