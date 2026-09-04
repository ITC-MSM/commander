package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Demonic Appetite
 * {B}
 * Enchantment — Aura
 *
 * Enchant creature you control
 * Enchanted creature gets +3/+3.
 * At the beginning of your upkeep, sacrifice a creature.
 *
 * Modeling notes:
 *  - The enchant clause reads "creature **you control**", not "creature", so `auraTarget` is
 *    `Targets.CreatureYouControl` rather than `Targets.Creature`. That is a real restriction, not
 *    flavour: it is why this can't be handed to an opponent's creature as a drawback.
 *  - The pump is one `staticAbility { ModifyStats(3, 3, Filters.EnchantedCreature) }` — the
 *    attached-creature group filter, the Aura counterpart of the Equipment scope.
 *  - The upkeep trigger is `Triggers.YourUpkeep` (`StepEvent(UPKEEP, Player.You)`), the printed
 *    "your upkeep" and not "each upkeep".
 *  - Its effect is `Effects.SacrificeOwn(GameObjectFilter.Creature)`, deliberately three things at
 *    once. It is **not targeted**: "sacrifice a creature" names no target, so nothing is chosen on
 *    announcement and nothing can be made illegal in response. It is **not scoped to the enchanted
 *    creature**: the Aura's controller picks any creature they control, which is the whole tension
 *    of the card — you may feed it something other than the buffed creature, or you may have to
 *    feed it the buffed creature itself. And it names **no player**, so it is `SacrificeOwn` and
 *    not `Effects.Sacrifice`, whose `target` parameter defaults to `Player.TargetOpponent` and
 *    would make the opponent sacrifice instead.
 */
val DemonicAppetite = card("Demonic Appetite") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\n" +
            "Enchanted creature gets +3/+3.\n" +
            "At the beginning of your upkeep, sacrifice a creature."

    auraTarget = Targets.CreatureYouControl

    staticAbility {
        ability = ModifyStats(3, 3, Filters.EnchantedCreature)
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Igor Kieryluk"
        flavorText = "\"Morality is just shorthand for the constraints of being powerless.\"\n—Ob Nixilis"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca51786a-d58c-455f-910d-01efa5ef8470.jpg?1783941986"
    }
}
