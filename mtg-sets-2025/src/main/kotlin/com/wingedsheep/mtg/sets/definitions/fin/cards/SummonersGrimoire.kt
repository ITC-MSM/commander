package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Summoner's Grimoire
 * {3}{G}
 * Artifact — Book Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature is a Shaman in addition to its other types and has "Whenever this creature
 *   attacks, you may put a creature card from your hand onto the battlefield. If that card is an
 *   enchantment card, it enters tapped and attacking."
 * Abraxas — Equip {3}
 *
 * Job-select Equipment shell ([jobSelect]) granting the Shaman type plus an attack trigger on the
 * equipped creature ([GrantTriggeredAbility]). The granted ability is a Gather → choose-up-to-one
 * → conditional-placement pipeline:
 *  - gather the controller's creature cards in hand, then `chooseUpTo(1)` models "you may put a
 *    creature card" (declining = choosing zero);
 *  - `filterSplit` on [GameObjectFilter.Enchantment] partitions the chosen card into enchantment
 *    creatures vs. the rest, so the printed conditional ("if that card is an enchantment card, it
 *    enters tapped and attacking") becomes two moves: enchantment creatures enter with
 *    [ZonePlacement.TappedAndAttacking], everything else enters normally. One of the two slots is
 *    always empty, so exactly the chosen card enters.
 */
val SummonersGrimoire = card("Summoner's Grimoire") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Book Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature is a Shaman in addition to its other types and has \"Whenever this creature attacks, you may put a creature card from your hand onto the battlefield. If that card is an enchantment card, it enters tapped and attacking.\"\n" +
        "Abraxas — Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = GrantSubtype("Shaman", Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = Effects.Pipeline {
                    val hand = gather(
                        CardSource.FromZone(Zone.HAND, Player.You, GameObjectFilter.Creature)
                    )
                    val chosen = chooseUpTo(
                        count = 1,
                        from = hand,
                        prompt = "You may put a creature card from your hand onto the battlefield",
                    )
                    val (enchantmentCreatures, rest) = filterSplit(chosen, GameObjectFilter.Enchantment)
                    move(
                        enchantmentCreatures,
                        CardDestination.ToZone(Zone.BATTLEFIELD, Player.You, ZonePlacement.TappedAndAttacking),
                    )
                    move(rest, CardDestination.ToZone(Zone.BATTLEFIELD, Player.You))
                }
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "205"
        artist = "Daniel Correia"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d9fda3fc-569d-49f8-a2ed-e0b1d6668426.jpg?1748706528"
    }
}
