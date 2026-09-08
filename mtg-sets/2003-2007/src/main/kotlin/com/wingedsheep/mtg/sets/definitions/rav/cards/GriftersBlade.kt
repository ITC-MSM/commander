package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.OnEnterRunEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grifter's Blade
 * {3}
 * Artifact — Equipment
 * Flash
 * As this Equipment enters, choose a creature you control it could be attached to. If you do, it
 * enters attached to that creature.
 * Equipped creature gets +1/+1.
 * Equip {1}
 *
 * The entry clause is two existing replacements standing in a row rather than a new "enters
 * attached" primitive: [EntersWithChoice] with [ChoiceType.CREATURE_ON_BATTLEFIELD] records the
 * host on the Blade before it enters (its candidate pool is already "creature you control,
 * excluding the entering object", and an empty pool skips the choice — which is precisely the
 * card's "If you do" and its second ruling), and [OnEnterRunEffect] then attaches to
 * [EffectTarget.ChosenCreature] inline with entry.
 *
 * "A creature you control **it could be attached to**" is, for an Equipment with no printed equip
 * restriction, every creature you control; the pool is exact today. A future Equipment that
 * *does* restrict its host would need the choice pool to take a filter.
 *
 * Not a "when this Equipment enters" trigger: a trigger uses the stack, so the Blade would sit
 * unattached while opponents responded and the attach could be answered — the card enters already
 * attached, with nothing to respond to.
 */
val GriftersBlade = card("Grifter's Blade") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Flash\n" +
        "As this Equipment enters, choose a creature you control it could be attached to. If you " +
        "do, it enters attached to that creature.\n" +
        "Equipped creature gets +1/+1.\n" +
        "Equip {1}"

    keywords(Keyword.FLASH)

    replacementEffect(EntersWithChoice(ChoiceType.CREATURE_ON_BATTLEFIELD))
    replacementEffect(OnEnterRunEffect(Effects.AttachEquipment(EffectTarget.ChosenCreature)))

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "263"
        artist = "Alan Pollack"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c411784-fe96-42c0-baaa-36616d2be0b3.jpg?1783943598"

        ruling("2005-10-01", "Grifter's Blade must enter attached to a creature you control, if possible.")
        ruling(
            "2005-10-01",
            "If you don't control a creature Grifter's Blade could be attached to, it simply " +
                "enters unattached."
        )
    }
}
