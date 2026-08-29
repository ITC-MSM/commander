package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessDynamicStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Katilda, Dawnhart Martyr // Katilda's Rising Dawn (Innistrad: Crimson Vow #21)
 * {1}{W}{W} · Legendary Creature — Spirit Warlock * / * // Legendary Enchantment — Aura
 *
 * Front — Katilda, Dawnhart Martyr
 *   Flying, lifelink, protection from Vampires
 *   Katilda's power and toughness are each equal to the number of permanents you control that are
 *   Spirits and/or enchantments.
 *   Disturb {3}{W}{W}
 *
 * Back — Katilda's Rising Dawn (Legendary Enchantment — Aura, white color indicator)
 *   Enchant creature
 *   Enchanted creature has flying, lifelink, and protection from Vampires, and it gets +X/+X, where X
 *   is the number of permanents you control that are Spirits and/or enchantments.
 *   If Katilda's Rising Dawn would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: a disturb card (CR 702.146) in the shape of [TwinbladeGeist]. The front's
 * star/star is a [SetBasePowerToughnessDynamicStatic] CDA over the same count the back's
 * [GrantDynamicStatsEffect] adds as a Layer 7c bonus — one shared [spiritsAndEnchantmentsYouControl]
 * amount, a homogeneous `or` of two filters so "Spirits and/or enchantments" counts an enchantment
 * creature Spirit once. The front's protection is the intrinsic
 * [KeywordAbility.protectionFromSubtype]; the Aura grants the same quality as the projected
 * `PROTECTION_FROM_SUBTYPE_VAMPIRE` keyword string, which is the form every engine read site
 * (targeting, blocking, combat damage) already checks for.
 */

/** The number of permanents you control that are Spirits and/or enchantments. */
private val spiritsAndEnchantmentsYouControl: DynamicAmount = DynamicAmount.AggregateBattlefield(
    Player.You,
    GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT) or GameObjectFilter.Enchantment,
)

private const val PROTECTION_FROM_VAMPIRES = "PROTECTION_FROM_SUBTYPE_VAMPIRE"

private val KatildaDawnhartMartyrFront = card("Katilda, Dawnhart Martyr") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Spirit Warlock"
    power = 0
    toughness = 0
    oracleText = "Flying, lifelink, protection from Vampires\n" +
        "Katilda's power and toughness are each equal to the number of permanents you control that " +
        "are Spirits and/or enchantments.\n" +
        "Disturb {3}{W}{W} (You may cast this card from your graveyard transformed for its disturb cost.)"

    keywords(Keyword.FLYING, Keyword.LIFELINK)
    keywordAbility(KeywordAbility.protectionFromSubtype("Vampire"))

    staticAbility {
        ability = SetBasePowerToughnessDynamicStatic(
            power = spiritsAndEnchantmentsYouControl,
            toughness = spiritsAndEnchantmentsYouControl,
        )
    }

    disturb("{3}{W}{W}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Manuel Castañón"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0ef240aa-2a88-4ec4-888a-918466372adb.jpg?1783924922"
        ruling(
            "2021-09-24",
            "When you cast a spell using a card's disturb ability, the card is put onto the stack " +
                "with its back face up. The resulting spell has all the characteristics of that face."
        )
        ruling(
            "2021-09-24",
            "The mana value of a spell cast using disturb is determined by the mana cost on the " +
                "front face of the card, no matter what the total cost to cast the spell was."
        )
        ruling(
            "2021-09-24",
            "The back face of each card with disturb has an ability that instructs its controller " +
                "to exile it if it would be put into a graveyard from anywhere. This includes going " +
                "to the graveyard from the stack, so if the spell is countered after you cast it " +
                "using the disturb ability, it will be put into exile."
        )
    }
}

private val KatildasRisingDawn = card("Katilda's Rising Dawn") {
    manaCost = ""
    colorIdentity = "W"
    colorIndicator = "W"
    typeLine = "Legendary Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has flying, lifelink, and protection from Vampires, and it gets +X/+X, " +
        "where X is the number of permanents you control that are Spirits and/or enchantments.\n" +
        "If Katilda's Rising Dawn would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
    }
    staticAbility {
        ability = GrantKeyword(PROTECTION_FROM_VAMPIRES)
    }
    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = spiritsAndEnchantmentsYouControl,
            toughnessBonus = spiritsAndEnchantmentsYouControl,
        )
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Manuel Castañón"
        imageUri = "https://cards.scryfall.io/normal/back/0/e/0ef240aa-2a88-4ec4-888a-918466372adb.jpg?1783924922"
    }
}

val KatildaDawnhartMartyr: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = KatildaDawnhartMartyrFront,
    backFace = KatildasRisingDawn,
)
