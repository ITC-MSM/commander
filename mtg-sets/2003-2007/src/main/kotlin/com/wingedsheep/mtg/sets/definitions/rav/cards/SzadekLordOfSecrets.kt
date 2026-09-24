package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ReplaceDamageWithCounters
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Szadek, Lord of Secrets — Ravnica: City of Guilds #234
 * {3}{U}{U}{B}{B} · Legendary Creature — Vampire · 5/5
 *
 * Flying
 * If Szadek would deal combat damage to a player, instead put that many +1/+1 counters on Szadek
 * and that player mills that many cards.
 *
 * One [ReplaceDamageWithCounters] carrying both results: counters on its host (Szadek) and, via
 * `damagedPlayerMills`, a mill of the player the damage was headed for. Two separate replacements
 * could not model it — whichever applied first would consume the damage event and the other would
 * never see it. The pattern is Szadek's own combat damage (`SourceFilter.Self`, `DamageType.Combat`)
 * to any player, so noncombat damage and combat damage to creatures or planeswalkers are dealt
 * normally. The damage is replaced, never dealt: no life is lost and "deals combat damage to a
 * player" triggers don't fire. The count is the damage Szadek would actually deal after other
 * modifications (e.g. doubling).
 */
val SzadekLordOfSecrets = card("Szadek, Lord of Secrets") {
    manaCost = "{3}{U}{U}{B}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Vampire"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "If Szadek would deal combat damage to a player, instead put that many +1/+1 counters on Szadek " +
        "and that player mills that many cards."

    keywords(Keyword.FLYING)

    replacementEffect(
        ReplaceDamageWithCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.AnyPlayer,
                source = SourceFilter.Self,
                damageType = DamageType.Combat,
            ),
            damagedPlayerMills = true,
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "234"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/2882793e-5d7d-47f0-9308-652a5e036c2c.jpg?1783943610"
        ruling("2005-10-01", "If another effect would prevent Szadek's combat damage from being dealt to the defending player or replace it with something else, that player chooses which effect applies first. For example, the player can choose to first have Mending Hands prevent 4 of the damage and then apply Szadek's ability so that it gets only one counter.")
    }
}
