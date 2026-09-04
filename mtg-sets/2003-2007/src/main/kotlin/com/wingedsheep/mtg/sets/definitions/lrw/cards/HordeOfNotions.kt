package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.GrantFreeCastTargetFromExileEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Horde of Notions
 * {W}{U}{B}{R}{G}
 * Legendary Creature — Elemental
 * 5/5
 *
 * Vigilance, trample, haste
 * {W}{U}{B}{R}{G}: You may play target Elemental card from your graveyard without paying its
 * mana cost.
 *
 * "Elemental card" is the bare tribal noun, so the target is any card with the subtype — a Kindred
 * noncreature Elemental such as Hoofprints of the Stag qualifies, and narrowing to
 * `CreatureInYourGraveyard` would silently drop it. Ownership, not control, is the axis for a card
 * sitting in a graveyard.
 *
 * The free cast is [GrantFreeCastTargetFromExileEffect] despite its name: the executor is
 * zone-agnostic — it stamps `PlayWithoutPayingCostComponent` on the target and registers a
 * `MayPlayPermission` wherever the card is — and `CastFromZoneEnumerator` already scans graveyards
 * alongside exile for such permissions (the path Malcolm, Alluring Scoundrel opened). So no exile
 * detour and no graveyard sibling effect is needed.
 *
 * The 2007-10-01 ruling that this doesn't let you pay an evoke cost falls out for free: the
 * permission is "without paying its mana cost", and an alternative cost is never offered on top
 * of it.
 */
val HordeOfNotions = card("Horde of Notions") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Elemental"
    power = 5
    toughness = 5
    oracleText = "Vigilance, trample, haste\n" +
        "{W}{U}{B}{R}{G}: You may play target Elemental card from your graveyard without paying " +
        "its mana cost."

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE, Keyword.HASTE)

    activatedAbility {
        cost = Costs.Mana("{W}{U}{B}{R}{G}")
        target = TargetObject(
            filter = TargetFilter.CardInGraveyard.withSubtype(Subtype.ELEMENTAL).ownedByYou(),
            id = "elemental"
        )
        effect = GrantFreeCastTargetFromExileEffect(target = EffectTarget.ContextTarget(0))
        description = "You may play target Elemental card from your graveyard without paying its mana cost."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "249"
        artist = "Adam Rex"
        flavorText = "Even the oldest treefolk was but an acorn when Lorwyn's first mysteries were born."
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80b9ea46-ae9b-4729-b206-0ba3ddc7660d.jpg?1783942856"
        ruling("2007-10-01", "This ability does not allow you to pay the evoke cost of the targeted Elemental card.")
    }
}
