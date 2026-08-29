package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Archon of Falling Stars
 * {4}{W}{W}
 * Creature — Archon
 * 4/4
 *
 * Flying
 * When this creature dies, you may return target enchantment card from your graveyard to the battlefield.
 *
 * A plain [Triggers.Dies] — battlefield → graveyard, SELF binding, and *no* `triggerZone`: setting
 * one replaces the default `{BATTLEFIELD}` and the dies trigger would never be indexed.
 *
 * The target is an object in the graveyard, so its controller predicate reads **owner**
 * (`ownedByYou()`): a card in a graveyard has no controller. `optional = true` is the DSL's
 * shorthand for the printed "you may" and lowers to exactly `MayEffect(effect)`.
 *
 * [Effects.PutOntoBattlefieldFromGraveyard] keeps its `fromZone = GRAVEYARD` guard even though the
 * target requirement already scopes the graveyard: the requirement decides legality at
 * announcement, the guard skips the move if the card has left the graveyard by resolution.
 */
val ArchonOfFallingStars = card("Archon of Falling Stars") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Archon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature dies, you may return target enchantment card from your graveyard to the battlefield."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        val enchantment = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Enchantment.ownedByYou(), zone = Zone.GRAVEYARD)),
        )
        optional = true
        effect = Effects.PutOntoBattlefieldFromGraveyard(enchantment)
        description = "When this creature dies, you may return target enchantment card from your " +
            "graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Victor Adame Minguez"
        flavorText = "\"The archon fell like a star from the sky, meeting the rising sun at the horizon's edge.\"\n—*The Cosmogony*"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0dd1e40-a514-42df-8cc1-364998c7700c.jpg"
    }
}
