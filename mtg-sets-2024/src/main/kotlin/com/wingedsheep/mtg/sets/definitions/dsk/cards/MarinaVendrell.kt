package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Marina Vendrell — Duskmourn: House of Horror #221
 * {W}{U}{B}{R}{G} · Legendary Creature — Human Warlock · 3/5
 *
 * When Marina Vendrell enters, reveal the top seven cards of your library. Put all enchantment
 * cards from among them into your hand and the rest on the bottom of your library in a random order.
 * {T}: Lock or unlock a door of target Room you control. Activate only as a sorcery.
 *
 * The ETB is the new [Patterns.Library.revealTopPutAllMatchingToHand] recipe — a mandatory reveal of
 * the top seven that auto-routes *every* enchantment to hand (a choice-free filter partition, not a
 * "keep up to one" choice) and drops the rest to the bottom in a random order.
 *
 * The activated ability is the established "lock or unlock a door" choice
 * ([Effects.LockOrUnlockDoor], a modal of lock/unlock that prompts which door when the Room has more
 * than one eligible) over a sorcery-speed `{T}` — the same shape as Keys to the House's second
 * ability, minus the mana/sacrifice cost. "Target Room you control" carries no door restriction:
 * any Room you control always has at least one door of one kind, so either choice can do something.
 */
val MarinaVendrell = card("Marina Vendrell") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Human Warlock"
    power = 3
    toughness = 5
    oracleText = "When Marina Vendrell enters, reveal the top seven cards of your library. Put all " +
        "enchantment cards from among them into your hand and the rest on the bottom of your " +
        "library in a random order.\n" +
        "{T}: Lock or unlock a door of target Room you control. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.revealTopPutAllMatchingToHand(
            count = DynamicAmount.Fixed(7),
            filter = GameObjectFilter.Enchantment,
        )
        description = "When Marina Vendrell enters, reveal the top seven cards of your library. Put " +
            "all enchantment cards from among them into your hand and the rest on the bottom of " +
            "your library in a random order."
    }

    activatedAbility {
        cost = Costs.Tap
        timing = TimingRule.SorcerySpeed
        target(
            "target Room",
            TargetObject(filter = TargetFilter(GameObjectFilter.Any.withSubtype(Subtype.ROOM).youControl())),
        )
        effect = Effects.LockOrUnlockDoor(EffectTarget.ContextTarget(0))
        description = "{T}: Lock or unlock a door of target Room you control. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "221"
        artist = "Magali Villeneuve"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6428cddc-2fb6-41af-a643-e83c81dc04f5.jpg?1726298170"
    }
}
