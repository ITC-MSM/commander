package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ninja's Blades
 * {2}{B}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +1/+1, is a Ninja in addition to its other types, and has "Whenever
 *   this creature deals combat damage to a player, draw a card, then discard a card. That player
 *   loses life equal to the discarded card's mana value."
 * Mutsunokami — Equip {2}
 *
 * Standard Job-select Equipment shell ([jobSelect]) plus three grants on the equipped creature:
 * a flat [ModifyStats] bump, the Ninja type ([GrantSubtype]), and a granted combat-damage
 * trigger ([GrantTriggeredAbility] over the equipped-creature filter, so "this creature" /
 * [EffectTarget.Self] is the bearer and "you" / [EffectTarget.Controller] is its controller).
 *
 * The granted ability loots (draw 1, then discard 1 — [Patterns.Hand.discardCards] stores the
 * discarded card under the `discarded` collection), then the player dealt the combat damage
 * ([Player.TriggeringPlayer]) loses life equal to that card's mana value
 * ([DynamicAmount.StoredCardManaValue]). The discard is mandatory, so the looter holds at least
 * the freshly drawn card to pitch.
 */
val NinjasBlades = card("Ninja's Blades") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +1/+1, is a Ninja in addition to its other types, and has \"Whenever this creature deals combat damage to a player, draw a card, then discard a card. That player loses life equal to the discarded card's mana value.\"\n" +
        "Mutsunokami — Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Ninja", Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = Effects.Composite(
                    Effects.DrawCards(1),
                    Patterns.Hand.discardCards(1),
                    Effects.LoseLife(
                        DynamicAmount.StoredCardManaValue("discarded"),
                        EffectTarget.PlayerRef(Player.TriggeringPlayer),
                    ),
                )
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "108"
        artist = "Immanuela Crovius"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6b5af82-3646-44f9-ac12-1d7fa698f037.jpg?1748706165"
    }
}
