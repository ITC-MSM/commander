package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Screaming Swarm — Innistrad: Crimson Vow #75
 * {5}{U} · Creature — Bird Horror · Uncommon · 4/4
 * Artist: Irina Nordsol
 *
 * Flying
 * Whenever you attack with one or more creatures, target player mills that many cards.
 * {2}{U}: Put this card from your graveyard into your library second from the top.
 *
 * The attack trigger is [Triggers.YouAttack] — the batch "whenever you attack" shape that fires
 * once per combat, not once per attacker. "That many" is the size of the batch, read as the
 * attacking creatures you control ([DynamicAmount.AggregateBattlefield] over
 * `Creature.attacking()`, which is evaluated under projected state). The target is a player, and
 * per the Scryfall ruling it may be you.
 *
 * The second ability is activated from the graveyard ([ActivatedAbilityBuilder.activateFromZone]),
 * at instant speed, and puts the card back into its owner's library at index 1 —
 * [Effects.PutIntoLibraryNthFromTop] is 0-indexed, so `positionFromTop = 1` is "second from the
 * top".
 */
val ScreamingSwarm = card("Screaming Swarm") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Horror"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever you attack with one or more creatures, target player mills that many cards. " +
        "(To mill a card, a player puts the top card of their library into their graveyard.)\n" +
        "{2}{U}: Put this card from your graveyard into your library second from the top."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouAttack
        val victim = target("target", Targets.Player)
        effect = Patterns.Library.mill(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.attacking()),
            victim
        )
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.PutIntoLibraryNthFromTop(EffectTarget.Self, positionFromTop = 1)
        activateFromZone = Zone.GRAVEYARD
        description = "{2}{U}: Put this card from your graveyard into your library second from the top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c9faf98-11bb-4407-aec9-d0d43dbaba34.jpg?1783924884"
        ruling("2021-11-19", "You may target yourself with Screaming Swarm's triggered ability.")
    }
}
