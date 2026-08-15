package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sphinx of Forgotten Lore
 * {2}{U}{U}
 * Creature — Sphinx
 * 3/3
 *
 * Flash
 * Flying
 * Whenever this creature attacks, target instant or sorcery card in your graveyard gains
 * flashback until end of turn. The flashback cost is equal to that card's mana cost.
 *
 * The attack trigger grants Flashback (CR 702.34) at runtime via [Effects.GrantFlashback], whose
 * cost defaults to the targeted card's own mana cost (the "flashback cost is equal to that card's
 * mana cost" clause). The cast-from-graveyard enumerator, cast handler, and stack resolver honor
 * the granted flashback exactly like a printed one through the shared `FlashbackGrants` resolver;
 * the grant expires during the cleanup step. Same pattern as Archmage's Newt, keyed off the
 * attack event rather than combat damage.
 */
val SphinxOfForgottenLore = card("Sphinx of Forgotten Lore") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 3
    toughness = 3
    oracleText = "Flash\nFlying\n" +
        "Whenever this creature attacks, target instant or sorcery card in your graveyard gains " +
        "flashback until end of turn. The flashback cost is equal to that card's mana cost. " +
        "(You may cast that card from your graveyard for its flashback cost. Then exile it.)"

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        target = TargetObject(
            filter = TargetFilter.InstantOrSorceryInGraveyard.ownedByYou()
        )
        effect = Effects.GrantFlashback(EffectTarget.ContextTarget(0))
        description = "Whenever this creature attacks, target instant or sorcery card in your " +
            "graveyard gains flashback until end of turn. The flashback cost is equal to that " +
            "card's mana cost."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "51"
        artist = "Dmitry Burmak"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af6e46b8-62ed-4bca-ba38-a821f225b59f.jpg?1782689220"
    }
}
