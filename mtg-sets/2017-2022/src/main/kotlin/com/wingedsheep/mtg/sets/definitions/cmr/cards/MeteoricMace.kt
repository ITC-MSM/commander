package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Meteoric Mace
 * {4}{R}{R}
 * Artifact — Equipment
 *
 * Equipped creature gets +4/+0 and has trample.
 * Equip {4}
 * Cascade
 *
 * Modeling notes:
 *  - Cascade (CR 702.85a) is itself a "when you cast this spell" triggered ability, so it is
 *    modelled the way Quandrix, the Proof models its own half: the [Keyword.CASCADE] keyword for
 *    the printed line plus a [Triggers.WhenYouCastThisSpell] trigger feeding [Effects.Cascade],
 *    which reads the triggering spell's mana value to set the "costs less" threshold. It fires on
 *    a noncreature permanent spell exactly as it does on a creature spell — cascade cares about
 *    the cast, not the card type.
 *  - The equip half is the ordinary Equipment shape: two static abilities scoped to
 *    [Filters.EquippedCreature] plus `equipAbility`, which sets `equipCost` and lowers the
 *    activated ability in one place.
 */
val MeteoricMace = card("Meteoric Mace") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +4/+0 and has trample.\n" +
        "Equip {4}\n" +
        "Cascade (When you cast this spell, exile cards from the top of your library until you " +
        "exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom in a random order.)"

    keywords(Keyword.CASCADE)

    staticAbility {
        ability = ModifyStats(+4, 0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }

    equipAbility("{4}")

    // Cascade — the cast trigger the keyword abbreviates.
    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "192"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28cb7e94-01df-4e80-9b24-bc303a27ffd6.jpg?1783928808"

        ruling(
            "2021-06-18",
            "A spell's mana value is determined only by its mana cost. Ignore any alternative " +
                "costs, additional costs, cost increases, or cost reductions."
        )
        ruling(
            "2021-06-18",
            "Cascade triggers when you cast the spell, meaning that it resolves before that " +
                "spell. If you end up casting the exiled card, it will go on the stack above the " +
                "spell with cascade."
        )
        ruling(
            "2021-06-18",
            "When the cascade ability resolves, you must exile cards. The only optional part of " +
                "the ability is whether or not you cast the last card exiled."
        )
        ruling(
            "2021-06-18",
            "If a spell with cascade is countered, the cascade ability will still resolve normally."
        )
        ruling(
            "2021-06-18",
            "If you cast a card \"without paying its mana cost,\" you can't choose to cast it " +
                "for any alternative costs. You can, however, pay additional costs. If the card " +
                "has any mandatory additional costs, you must pay those to cast the card."
        )
        ruling(
            "2021-06-18",
            "Due to a 2021 rules change to cascade, not only do you stop exiling cards if you " +
                "exile a nonland card with lesser mana value than the spell with cascade, but " +
                "the resulting spell you cast must also have lesser mana value."
        )
    }
}
