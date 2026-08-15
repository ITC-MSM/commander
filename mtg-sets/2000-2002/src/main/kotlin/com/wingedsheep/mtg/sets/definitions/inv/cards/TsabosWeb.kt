package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Tsabo's Web
 * {2}
 * Artifact
 *
 * When this artifact enters, draw a card.
 * Each land with an activated ability that isn't a mana ability doesn't untap during
 * its controller's untap step.
 *
 * The "non-mana activated ability" check reflects a land's printed abilities only (via
 * the precomputed CardComponent.hasNonManaActivatedAbility flag); abilities granted to a
 * land by another continuous effect are not counted.
 */
val TsabosWeb = card("Tsabo's Web") {
    manaCost = "{2}"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, draw a card.\n" +
        "Each land with an activated ability that isn't a mana ability doesn't untap " +
        "during its controller's untap step."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = GrantKeyword(
            keyword = AbilityFlag.DOESNT_UNTAP.name,
            filter = GroupFilter(
                GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.IsLand,
                        CardPredicate.HasNonManaActivatedAbility
                    )
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "317"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dee69f8-cceb-41b9-a0ee-6b2ac9f4bad9.jpg?1562897866"
    }
}
