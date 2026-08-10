package com.wingedsheep.mtg.sets.definitions.hob.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wizard's Staff — The Hobbit #59
 * {1}{U} · Artifact — Equipment · Rare
 *
 * Equipped creature has prowess.
 * If an ability of equipped creature triggers, that ability triggers an additional time.
 * Equip Wizard {1}
 * Equip {3}
 *
 * Modeling notes:
 *  - The doubling is [AdditionalSourceTriggers] scoped by `attachedToBySource()` — "the permanent
 *    this Equipment is attached to". That's the mirror of Cloud, Midgar Mercenary, which scopes the
 *    same primitive with `attachedToSource()` (the Equipment attached to *it*). `excludeSelf` is
 *    irrelevant here since the Staff can never be attached to itself, but it stays at its default
 *    so the Staff's own future triggers aren't caught by the filter.
 *  - Only *triggered* abilities trigger, so the printed "an ability" is exactly what
 *    [AdditionalSourceTriggers] covers; per its rulings this is one extra firing, not a copy, so
 *    modes and targets are chosen independently for each instance.
 *  - Two equip costs are two separate equip-flagged activated abilities — the Bilbo's Ring shape.
 *    `equipAbility(...)` handles the generic "Equip {3}"; the Wizard-restricted one is hand-rolled
 *    because the facade has no target-filter parameter.
 */
val WizardsStaff = card("Wizard's Staff") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has prowess. (Whenever its controller casts a noncreature " +
        "spell, that creature gets +1/+1 until end of turn.)\n" +
        "If an ability of equipped creature triggers, that ability triggers an additional time.\n" +
        "Equip Wizard {1} ({1}: Attach to target Wizard you control. Equip only as a sorcery.)\n" +
        "Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantKeyword(Keyword.PROWESS, Filters.EquippedCreature)
    }

    staticAbility {
        ability = AdditionalSourceTriggers(
            sourceFilter = GameObjectFilter.Permanent.attachedToBySource(),
            description = "If an ability of equipped creature triggers, that ability triggers " +
                "an additional time"
        )
    }

    // Equip Wizard {1}: Attach to target Wizard you control. Equip only as a sorcery.
    activatedAbility {
        isEquipAbility = true
        cost = Costs.Mana("{1}")
        timing = TimingRule.SorcerySpeed
        val wizard = target(
            "target Wizard you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl.withSubtype(Subtype.WIZARD))
        )
        effect = Effects.AttachEquipment(wizard)
        description = "Equip Wizard {1}"
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "59"
        artist = "Gaboleps"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0de529a7-bdc5-4581-a169-1ad123bc099a.jpg?1785152406"
    }
}
