package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Goblin Maskmaker — Murders at Karlov Manor #130
 * {R} · Creature — Goblin Citizen · 1/2
 *
 * Whenever this creature attacks, face-down spells you cast this turn cost {1} less to cast.
 *
 * The discount is created when the attack trigger resolves and lives on the controller for the
 * rest of the turn, so it survives the Maskmaker leaving the battlefield. A face-down spell is
 * identified by the same projected face-down predicate used for disguise and cloak permanents;
 * only generic mana can be reduced.
 */
val GoblinMaskmaker = card("Goblin Maskmaker") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Citizen"
    oracleText = "Whenever this creature attacks, face-down spells you cast this turn cost {1} " +
        "less to cast."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ReduceSpellCostsThisTurn(
            spellFilter = GameObjectFilter.Any.faceDown(),
            amount = DynamicAmount.Fixed(1),
        )
        description = "Whenever this creature attacks, face-down spells you cast this turn cost " +
            "{1} less to cast."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Tomas Duchek"
        flavorText = "\"When you can't show your own face, try one of mine!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/" +
            "6154a991-c602-4fca-91a3-3830060da60e.jpg?1783912880"
    }
}
