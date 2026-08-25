package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Smiting Helix — Modern Horizons #109
 * {3}{B} · Sorcery
 *
 * Smiting Helix deals 3 damage to any target and you gain 3 life.
 * Flashback {R}{W}
 *
 * One of the set's off-color flashback cards: a black sorcery whose flashback cost is
 * {R}{W}, so its color identity is all three (Lightning Helix's effect, cast from the
 * graveyard on Boros mana). The flashback cost is not a second face — the card is black in
 * every zone; only the *cost* changes colors.
 *
 * "Any target" is one requirement (CR 115.4) covering creature, player, battle and planeswalker,
 * and the life gain is a separate step in the same [Effects.Composite] — it happens even if the
 * damage target has become illegal, because the whole spell only fizzles when *every* target does.
 * No `damageSource` is passed: the spell is its own source, which is the default.
 */
val SmitingHelix = card("Smiting Helix") {
    manaCost = "{3}{B}"
    colorIdentity = "BRW"
    typeLine = "Sorcery"
    oracleText = "Smiting Helix deals 3 damage to any target and you gain 3 life.\n" +
        "Flashback {R}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    keywordAbility(KeywordAbility.flashback("{R}{W}"))

    spell {
        val victim = target("any target", Targets.Any)
        effect = Effects.Composite(
            Effects.DealDamage(3, victim),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Evan Shipard"
        flavorText = "Malice is appropriate when vengeance is called for."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fa3e132-125a-492f-aab7-c560ea36b779.jpg?1783933120"
    }
}
