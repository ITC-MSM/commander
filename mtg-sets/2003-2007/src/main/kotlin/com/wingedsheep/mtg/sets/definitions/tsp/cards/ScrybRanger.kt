package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Scryb Ranger
 * {1}{G}
 * Creature — Faerie Ranger
 * 1 / 1
 *
 * Flash
 * Flying, protection from blue
 * Return a Forest you control to its owner's hand: Untap target creature. Activate only once each
 * turn.
 *
 * The Quirion Ranger ability, reprinted on a flying body. The Forest is the whole cost — no mana in
 * it — so [Costs.ReturnToHand] carries the filter, and "a Forest" is the land subtype rather than
 * the card named Forest, hence `Land.withSubtype(Forest)`.
 */
val ScrybRanger = card("Scryb Ranger") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Faerie Ranger"
    power = 1
    toughness = 1
    oracleText = "Flash\n" +
        "Flying, protection from blue\n" +
        "Return a Forest you control to its owner's hand: Untap target creature. Activate only once each turn."

    keywords(Keyword.FLASH, Keyword.FLYING)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLUE)))

    activatedAbility {
        cost = Costs.ReturnToHand(GameObjectFilter.Land.withSubtype(Subtype.FOREST))
        val t = target("target", Targets.Creature)
        effect = Effects.Untap(t)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3aacabde-f5ec-4519-895d-17f5e48746ee.jpg"
    }
}
