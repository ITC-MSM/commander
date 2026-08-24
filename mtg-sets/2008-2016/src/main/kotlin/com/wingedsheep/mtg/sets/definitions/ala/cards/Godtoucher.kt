package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Godtoucher
 * {3}{G}
 * Creature — Elf Cleric
 * 2 / 2
 * {1}{W}, {T}: Prevent all damage that would be dealt to target creature with power 5 or greater this turn.
 *
 * The activated cost is a [Costs.Composite] of mana plus [Costs.Tap]. The power threshold rides the
 * target itself — [TargetFilter.Creature]`.powerAtLeast(5)` — so it is checked on announcement and
 * rechecked on resolution. The shield is [PreventDamageEffect] with every field left at its default:
 * a null `amount` means "prevent all", `PreventionScope.AllDamage` covers non-combat damage too,
 * `PreventionDirection.ToTarget` is the printed "dealt to", and `Duration.EndOfTurn` is "this turn".
 */
val Godtoucher = card("Godtoucher") {
    manaCost = "{3}{G}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Cleric"
    power = 2
    toughness = 2
    oracleText = "{1}{W}, {T}: Prevent all damage that would be dealt to target creature with power 5 or greater this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(5)))
        effect = PreventDamageEffect(target = t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Jesper Ejsing"
        flavorText = "\"Gargantuans die not in moments but in moons. There is still time to aid this noble ancient.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/3/032059cb-c1ad-4cc9-a89d-d68289800312.jpg"
    }
}
