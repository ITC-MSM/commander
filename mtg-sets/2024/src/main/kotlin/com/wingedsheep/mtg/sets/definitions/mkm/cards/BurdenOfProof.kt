package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic

/**
 * "as long as it's a Detective you control" — shared by all three of the Aura's statics. Declared
 * ahead of the card because top-level properties initialize in file order.
 */
private val IsDetectiveYouControl = Conditions.EnchantedPermanentMatches(
    GameObjectFilter.Creature.withSubtype(Subtype.DETECTIVE).youControl()
)

/**
 * Burden of Proof — Murders at Karlov Manor #42
 * {1}{U} · Enchantment — Aura
 *
 * Flash
 * Enchant creature
 * Enchanted creature gets +2/+2 as long as it's a Detective you control. Otherwise, it has base
 * power and toughness 1/1 and can't block Detectives.
 *
 * One condition drives all three statics, so it is written once and negated for the "otherwise"
 * branch: [Conditions.EnchantedPermanentMatches] over a Detective-you-control filter. Both branches
 * stay live continuously — the Aura can move, the enchanted creature can gain or lose the Detective
 * type, and control can change; each recheck flips which branch projects.
 *
 * The two stat lines sit in different layers and never fight: `+2/+2` is [ModifyStats] (Layer 7c)
 * and `base power and toughness 1/1` is [SetBasePowerToughnessStatic] (Layer 7b). Because the
 * conditions are exact complements, only one is ever contributing.
 *
 * "Can't block Detectives" is expressed as its complement, [CanOnlyBlockCreaturesWith] over
 * non-Detective creatures. The two are the same restriction — every creature is either a Detective
 * or not — and this shape scopes the restriction to the enchanted creature itself, where the oracle
 * text puts it, rather than making it a property of every Detective on the battlefield. Per the
 * ruling, this is checked when blockers are declared: attaching the Aura to a creature that has
 * *already* blocked a Detective doesn't undo the block.
 */
val BurdenOfProof = card("Burden of Proof") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\n" +
        "Enchant creature\n" +
        "Enchanted creature gets +2/+2 as long as it's a Detective you control. Otherwise, it has " +
        "base power and toughness 1/1 and can't block Detectives."

    keywords(Keyword.FLASH)
    auraTarget = Targets.Creature

    staticAbility {
        condition = IsDetectiveYouControl
        ability = ModifyStats(+2, +2, Filters.EnchantedCreature)
    }

    staticAbility {
        condition = Conditions.Not(IsDetectiveYouControl)
        ability = SetBasePowerToughnessStatic(1, 1, Filters.EnchantedCreature)
    }

    staticAbility {
        condition = Conditions.Not(IsDetectiveYouControl)
        ability = CanOnlyBlockCreaturesWith(
            blockerFilter = GameObjectFilter.Creature.notSubtype(Subtype.DETECTIVE),
            filter = Filters.EnchantedCreature
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Deruchenko Alexander"
        flavorText = "\"You must have known all those lies would catch up to you eventually.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ea29c34-4b55-4170-9120-0a8dda61f2eb.jpg?1783912915"
        ruling(
            "2024-02-02",
            "Once a Detective has been blocked, attaching Burden of Proof to a creature blocking it " +
                "won't cause that Detective to become unblocked."
        )
    }
}
