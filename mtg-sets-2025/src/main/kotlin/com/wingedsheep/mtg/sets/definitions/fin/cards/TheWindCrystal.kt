package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyLifeGain
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * The Wind Crystal
 * {2}{W}{W}
 * Legendary Artifact
 *
 * White spells you cast cost {1} less to cast.
 * If you would gain life, you gain twice that much life instead.
 * {4}{W}{W}, {T}: Creatures you control gain flying and lifelink until end of turn.
 *
 * Notes:
 *  - The cost reduction reduces only generic mana in the total cost of white spells you cast
 *    (Scryfall ruling 2025-06-06); modeled with [CostModification.ReduceGeneric].
 *  - "You gain twice that much life instead" is [ModifyLifeGain] with `multiplier = 2`, scoped
 *    to life gained by you ([Player.You]). Two copies multiply by four, three by eight, etc.,
 *    because each is a separate replacement applied in turn (Scryfall ruling 2025-06-06).
 *  - The activated ability grants flying and lifelink to creatures you control (a group, not a
 *    target) until end of turn.
 */
val TheWindCrystal = card("The Wind Crystal") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Artifact"
    oracleText = "White spells you cast cost {1} less to cast.\n" +
        "If you would gain life, you gain twice that much life instead.\n" +
        "{4}{W}{W}, {T}: Creatures you control gain flying and lifelink until end of turn."

    // White spells you cast cost {1} less to cast.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withColor(Color.WHITE)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    // If you would gain life, you gain twice that much life instead.
    replacementEffect(
        ModifyLifeGain(
            multiplier = 2,
            appliesTo = EventPattern.LifeGainEvent(player = Player.You),
        )
    )

    // {4}{W}{W}, {T}: Creatures you control gain flying and lifelink until end of turn.
    // Grant per-creature via ForEachInGroup so each creature you control receives a floating
    // keyword effect (a GroupRef target on GrantKeyword is not expanded per-permanent).
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{W}{W}"), Costs.Tap)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.Composite(
                Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "43"
        artist = "Pablo Mendoza"
        flavorText = "\"I give unto you the last of my light, and with it, the last hope of a fading world.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19bd0885-baaa-40f2-9c59-b1ea53807540.jpg?1748705917"
    }
}
