package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Living Hive — Mirrodin #124
 * {6}{G}{G} · Creature — Elemental Insect · 6/6 · Rare
 *
 * Trample
 * Whenever this creature deals combat damage to a player, create that many 1/1 green Insect
 * creature tokens.
 *
 * "That many" is the damage in the triggering payload, read with
 * [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT] — the same primitive Broodhatch Nantuko uses. It is the
 * damage *actually dealt*, so a pumped or partially-prevented hit makes the matching number of
 * Insects, and a fully-prevented hit never triggers at all.
 *
 * Trample matters here: only the damage assigned to the *player* counts, since the trigger fires off
 * the player-damage event. A 6/6 trampling over a 2/2 blocker deals 4 to the player and hatches four
 * Insects, not six.
 */
val LivingHive = card("Living Hive") {
    manaCost = "{6}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental Insect"
    power = 6
    toughness = 6
    oracleText = "Trample\n" +
        "Whenever this creature deals combat damage to a player, create that many 1/1 green Insect " +
        "creature tokens."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = CreateTokenEffect(
            count = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Insect"),
            // Mirrodin printed no token cards; this is the Magic Player Rewards 2003 Insect, the
            // contemporary printing.
            imageUri = "https://cards.scryfall.io/normal/front/a/a/aa47df37-f246-4f80-a944-008cdf347dad.jpg?1783944982"
        )
        description = "Whenever this creature deals combat damage to a player, create that many " +
            "1/1 green Insect creature tokens."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "124"
        artist = "Anthony S. Waters"
        flavorText = "In its center is a single red ant, a queen that regulates the hive's movements."
        imageUri = "https://cards.scryfall.io/normal/front/4/0/407dad3c-d721-412a-8b29-bc15be56d2fe.jpg?1783944534"
    }
}
