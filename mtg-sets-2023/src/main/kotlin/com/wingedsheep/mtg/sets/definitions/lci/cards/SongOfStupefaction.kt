package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Song of Stupefaction
 * {1}{U}
 * Enchantment — Aura
 *
 * Enchant creature or Vehicle
 * When this Aura enters, you may mill two cards.
 * Fathomless descent — Enchanted permanent gets -X/-0, where X is the number of
 * permanent cards in your graveyard.
 *
 * The Aura attaches to a creature or Vehicle ([GameObjectFilter.CreatureOrVehicle], a Vehicle
 * matched by its subtype — same enchant clause as Silken Strength). The enters trigger is an
 * optional (`optional = true`) self-mill of two cards. The static power penalty is a
 * continuously recomputed [GrantDynamicStatsEffect] on the attached permanent: power is reduced
 * by the number of permanent cards in your graveyard (`Count(GRAVEYARD, Permanent)` negated via
 * `Multiply(..., -1)` — the "fathomless descent" count), toughness unchanged. The default
 * [GroupFilter.attachedCreature] scope is AttachedTo of any permanent, so the -X/-0 applies even
 * when the host is a non-creature Vehicle.
 */
val SongOfStupefaction = card("Song of Stupefaction") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature or Vehicle\n" +
        "When this Aura enters, you may mill two cards. (You may put the top two cards of your library into your graveyard.)\n" +
        "Fathomless descent — Enchanted permanent gets -X/-0, where X is the number of permanent cards in your graveyard."

    auraTarget = TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrVehicle))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.mill(2)
    }

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = DynamicAmount.Multiply(
                DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Permanent),
                -1
            ),
            toughnessBonus = DynamicAmount.Fixed(0)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Ernanda Souza"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab8f7a75-df5e-43b3-8c33-328ad0dc3c40.jpg?1782694548"
    }
}
