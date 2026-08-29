package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gutter Skulker // Gutter Shortcut (Innistrad: Crimson Vow #62 — the card's earliest printing)
 * {3}{U} · Creature — Spirit 3/3 // Enchantment — Aura
 *
 * Front — Gutter Skulker ({3}{U}, Creature — Spirit, 3/3)
 *   This creature can't be blocked as long as it's attacking alone.
 *   Disturb {3}{U}
 *
 * Back — Gutter Shortcut (Enchantment — Aura, blue color indicator)
 *   Enchant creature
 *   Enchanted creature can't be blocked as long as it's attacking alone.
 *   If Gutter Shortcut would be put into a graveyard from anywhere, exile it instead.
 *
 * Both faces print the same evasion clause about two different creatures, which is the whole of
 * the modelling decision. [CantBeBlocked] carries the *affected* creature in its `filter`
 * (`GroupFilter.source()` on the creature face, `GroupFilter.attachedCreature()` on the Aura — the
 * type's KDoc is explicit that an attachment omitting it reads as a restriction on the Aura, which
 * never blocks or is blocked, and is therefore silently inert), and the "as long as" clause is the
 * `condition` on the same [com.wingedsheep.sdk.scripting.ConditionalStaticAbility]: the source on
 * the front, the enchanted permanent on the back. Both condition roles are dual-mode, so they are
 * answerable during static-ability projection, which is where a continuous effect is applied.
 *
 * "Attacking alone" is CR 506.5 — [com.wingedsheep.sdk.scripting.predicates.StatePredicate
 * .IsAttackingAlone], a live read of the current attacking set with no last-known fallback. It is
 * re-checked continuously, so a second attacker joining the attack (or the enchanted creature
 * ceasing to attack) turns the evasion off on its own.
 *
 * Disturb is CR 702.146; the exile-instead clause is [RedirectZoneChange] with `selfOnly = true` so
 * it functions from every zone (CR 614.12).
 */
private val GutterSkulkerFront = card("Gutter Skulker") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 3
    oracleText = "This creature can't be blocked as long as it's attacking alone.\n" +
        "Disturb {3}{U} (You may cast this card from your graveyard transformed for its disturb cost.)"

    staticAbility {
        ability = CantBeBlocked(filter = GroupFilter.source())
        condition = Conditions.SourceMatches(GameObjectFilter.Any.attackingAlone())
    }

    disturb("{3}{U}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Viko Menezes"
        flavorText = "Geists have a knack for showing up wherever they're least expected."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ceb84515-7b8f-444d-b6a9-61231621f9b7.jpg?1783924900"
    }
}

private val GutterShortcut = card("Gutter Shortcut") {
    manaCost = ""
    colorIdentity = "U"
    colorIndicator = "U" // Transformed back face, no mana cost (CR 204).
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature can't be blocked as long as it's attacking alone.\n" +
        "If Gutter Shortcut would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = CantBeBlocked(filter = GroupFilter.attachedCreature())
        condition = Conditions.EnchantedPermanentMatches(GameObjectFilter.Any.attackingAlone())
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Viko Menezes"
        flavorText = "Some people take that as inspiration."
        imageUri = "https://cards.scryfall.io/normal/back/c/e/ceb84515-7b8f-444d-b6a9-61231621f9b7.jpg?1783924900"
    }
}

val GutterSkulker: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = GutterSkulkerFront,
    backFace = GutterShortcut,
)
