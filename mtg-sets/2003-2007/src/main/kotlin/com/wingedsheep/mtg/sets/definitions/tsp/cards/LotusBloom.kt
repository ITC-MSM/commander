package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Lotus Bloom
 * (no mana cost)
 * Artifact
 * Suspend 3—{0} (Rather than cast this card from your hand, pay {0} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)
 * {T}, Sacrifice this artifact: Add three mana of any one color.
 *
 * Printed with no mana cost — CR 202.1b/118.6: an unpayable cost, so it can only reach the
 * battlefield through suspend (or another free-cast effect), exactly like
 * `tsp/cards/AncestralVision.kt`. Unlike that card it is colorless, so there is no printed color
 * indicator (CR 204) to carry.
 *
 * "Any *one* color" is [Effects.AddAnyColorMana] — one colour chosen, three mana of it — not
 * `AddManaInAnyCombination`, which would colour each mana independently. No target, adds mana,
 * not a loyalty ability, so it is a mana ability (CR 605.1a) and never uses the stack.
 */
val LotusBloom = card("Lotus Bloom") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Suspend 3—{0} (Rather than cast this card from your hand, pay {0} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)\n" +
        "{T}, Sacrifice this artifact: Add three mana of any one color."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.AddAnyColorMana(3)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    keywordAbility(KeywordAbility.suspend("{0}", 3))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73127ee0-9a0c-48fa-9c60-b4c600ace8f7.jpg"
    }
}
