package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnarlback Rhino (M20 #300)
 * {2}{G}{G}  Creature — Rhino  4/4
 *
 * Trample
 * Whenever you cast a spell that targets this creature, draw a card.
 *
 * The draw trigger uses the [Triggers.youCastSpellTargetingSource] cast-time predicate
 * (`SpellCastPredicate.TargetsSource`) — it fires as the spell is cast, before it resolves,
 * so it still triggers even if the spell is later countered or the Rhino leaves play.
 */
val GnarlbackRhino = card("Gnarlback Rhino") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino"
    power = 4
    toughness = 4
    oracleText = "Trample\n" +
        "Whenever you cast a spell that targets this creature, draw a card."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.youCastSpellTargetingSource()
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "300"
        artist = "YW Tang"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68a69558-aca0-413d-9762-2fa115b44abd.jpg?1782708186"
    }
}
