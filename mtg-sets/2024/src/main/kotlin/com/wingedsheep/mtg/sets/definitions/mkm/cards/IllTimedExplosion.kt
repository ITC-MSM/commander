package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ill-Timed Explosion — Murders at Karlov Manor #207
 * {2}{U}{R} · Sorcery · Rare
 *
 * The optional discard is the action of a reflexive trigger, so the damage ability is created only
 * after two cards were actually discarded. The discarded pipeline collection crosses onto that
 * reflexive ability; filtering it to the greatest mana value (keeping ties) lets the ordinary
 * stored-card mana-value amount drive the damage to every creature.
 */
val IllTimedExplosion = card("Ill-Timed Explosion") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Sorcery"
    oracleText = "Draw two cards. Then you may discard two cards. When you do, Ill-Timed " +
        "Explosion deals X damage to each creature, where X is the greatest mana value among " +
        "cards discarded this way."

    spell {
        effect = Effects.Composite(
            Effects.DrawCards(2),
            ReflexiveTriggerEffect(
                action = Patterns.Hand.discardCards(2),
                optional = true,
                reflexiveEffect = Effects.Composite(
                    FilterCollectionEffect(
                        from = "discarded",
                        filter = CollectionFilter.GreatestManaValue,
                        storeMatching = "greatestDiscarded",
                    ),
                    Patterns.Group.dealDamageToAll(
                        amount = DynamicAmount.StoredCardManaValue("greatestDiscarded"),
                        filter = GroupFilter.AllCreatures,
                    ),
                ),
                descriptionOverride = "You may discard two cards. When you do, Ill-Timed " +
                    "Explosion deals damage to each creature equal to the greatest mana value " +
                    "among cards discarded this way.",
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "207"
        artist = "Aaron J. Riley"
        flavorText = "Only too late did the Agency custodians realize the device had not been " +
            "inactive—it was just waiting for the right trigger."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b5cdb01-eaa4-4a0a-b42a-332bcf4d6fff.jpg?1783912847"

        ruling("2024-02-02", "If a card discarded this way has {X} in its mana cost, X is 0.")
    }
}
