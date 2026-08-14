package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DonorCards
import com.wingedsheep.sdk.scripting.HasAllActivatedAbilitiesOfCards

/**
 * Territory Forge — {4}{R} Artifact (The Big Score, mythic).
 *
 * "When this artifact enters, if you cast it, exile target artifact or land.
 *  This artifact has all activated abilities of the exiled card."
 *
 * Implementation:
 *  - ETB trigger gated by an intervening-if [Conditions.WasCast] ("if you cast it"): exile
 *    a target artifact or land, linking it to Territory Forge's `LinkedExileComponent`.
 *  - The static ability [HasAllActivatedAbilitiesOfCards] (`donors = LINKED_EXILE`) reads
 *    that linked-exile pile at activation-legality time and surfaces every activated ability of the
 *    exiled card on Territory Forge itself.
 *
 * Rulings:
 *  - It grants only *activated* abilities — not mana abilities only, not triggered/static ones.
 *  - The exiled card's "this card" references become references to Territory Forge, so a `{T}`
 *    cost taps Territory Forge and a self-sacrifice sacrifices Territory Forge.
 *  - If Territory Forge enters by any means other than being cast, the ETB does nothing (no
 *    card is exiled, so it grants no abilities).
 */
val TerritoryForge = card("Territory Forge") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, if you cast it, exile target artifact or land.\n" +
        "This artifact has all activated abilities of the exiled card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCast
        val exiled = target("target artifact or land", Targets.ArtifactOrLand)
        effect = Effects.ExileLinkedToSource(exiled)
    }

    staticAbility {
        // Default source = LINKED, filter = the source itself ("This artifact has all activated abilities …").
        ability = HasAllActivatedAbilitiesOfCards(donors = DonorCards.LINKED_EXILE)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "15"
        artist = "Mirko Failoni"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71059bc8-f63a-4d9c-9d08-2e995e74cc59.jpg?1739804200"
    }
}
