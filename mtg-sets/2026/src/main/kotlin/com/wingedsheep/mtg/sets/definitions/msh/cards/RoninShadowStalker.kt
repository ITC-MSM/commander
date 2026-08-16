package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Ronin, Shadow Stalker — Marvel Super Heroes #112 (uncommon)
 *
 * {2}{B} · Legendary Creature — Human Rogue Hero · 3/3
 *   Pay 2 life: Add two mana of any one color. Spend this mana only to cast Equipment spells or
 *   activate equip abilities. Activate only once each turn.
 *   {T}, Sacrifice an Equipment attached to Ronin: Target creature gets -4/-4 until end of turn.
 *   Activate only as a sorcery.
 *
 * The first ability is a mana ability on all four of CR 605.1a's criteria — no target, could add
 * mana when it resolves, not a loyalty ability, and neither its cost nor its effect moves a card to
 * or from a library — and so doesn't use the stack (CR 605.3b). Its only
 * cost is a life payment, capped by [ActivationRestriction.OncePerTurn]. Its spend restriction is
 * the *conjunction of two spend contexts*, so it is an [ManaRestriction.AnyOf] of two atoms:
 *
 *  - `SubtypeSpellsOnly(setOf("Equipment"))` for "cast Equipment spells" — spell-only, matching the
 *    printed wording.
 *  - [ManaRestriction.EquipAbilityActivationOnly] for "activate equip abilities" (CR 702.6).
 *
 * The tempting one-liner `SubtypeSpellsOrAbilitiesOnly("Equipment")` is **wrong** here: it admits
 * every activated ability of an Equipment source, so it would also pay for Iron Man Armor's
 * "{2}: … it becomes a 0/0 Construct Hero artifact creature". Only the equip ability itself
 * qualifies, which is the fact `EquipAbilityActivationOnly` tests.
 *
 * The second ability's sacrifice cost is scoped with `.attachedToSource()` — "an Equipment attached
 * to Ronin", not any Equipment you control — and is sorcery-speed ([TimingRule.SorcerySpeed]).
 */
val RoninShadowStalker = card("Ronin, Shadow Stalker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Rogue Hero"
    power = 3
    toughness = 3
    oracleText = "Pay 2 life: Add two mana of any one color. Spend this mana only to cast " +
        "Equipment spells or activate equip abilities. Activate only once each turn.\n" +
        "{T}, Sacrifice an Equipment attached to Ronin: Target creature gets -4/-4 until end of " +
        "turn. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.PayLife(2)
        effect = Effects.AddAnyColorMana(
            2,
            restriction = ManaRestriction.AnyOf(
                listOf(
                    ManaRestriction.SubtypeSpellsOnly(setOf("Equipment")),
                    ManaRestriction.EquipAbilityActivationOnly,
                )
            )
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        description = "Pay 2 life: Add two mana of any one color. Spend this mana only to cast " +
            "Equipment spells or activate equip abilities. Activate only once each turn."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Artifact.withSubtype("Equipment").attachedToSource())
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-4, -4, creature)
        timing = TimingRule.SorcerySpeed
        description = "{T}, Sacrifice an Equipment attached to Ronin: Target creature gets -4/-4 " +
            "until end of turn. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "112"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f74b9794-946e-4ddc-93b2-5a321fc51fd0.jpg?1783902939"
    }
}
