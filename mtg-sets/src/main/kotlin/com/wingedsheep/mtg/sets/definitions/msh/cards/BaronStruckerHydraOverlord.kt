package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Baron Strucker, HYDRA Overlord — Marvel Super Heroes #88 (uncommon)
 * {2}{B} · Legendary Creature — Human Villain · 2/2
 *
 * Villain spells you cast cost {1} less to cast.
 * Whenever another Villain you control enters, you may have it connive. Do this only once each
 * turn.
 *
 * Implementation notes:
 * - The cost reduction is the Tombstone, Career Criminal shape: [ModifySpellCost] with
 *   `SpellCostTarget.YouCast(Any.withSubtype(Subtype.VILLAIN))` and
 *   [CostModification.ReduceGeneric] `1`. It reads the *spell's* subtypes, so it catches Villain
 *   artifact creatures and any other card type that happens to carry the Villain subtype, and it
 *   only ever reduces generic mana (CR 601.2f) — a {B} Villain still costs {B}.
 * - The trigger is the set's stock "another Villain you control enters" — a Villain *permanent*
 *   filter (tokens and artifact creatures carry the subtype too) with [TriggerBinding.OTHER] so
 *   Strucker's own arrival never fires it.
 * - "you may have **it** connive" — the conniving permanent is the creature that just entered
 *   ([EffectTarget.TriggeringEntity], the Swordsman, Sharp Scoundrel idiom), not Strucker, so the
 *   +1/+1 counter from a nonland discard lands on the new Villain. The "you may" is a
 *   resolution-time [MayEffect]. If the entering Villain has already left the battlefield when the
 *   trigger resolves, the connive's counter simply has nowhere to go; the draw and discard still
 *   happen.
 * - "Do this only once each turn" is `oncePerTurn` — the same modeling this set uses for that
 *   wording elsewhere (Jennifer Walters, Crossbones). Strictly the printed wording caps the
 *   *effect* rather than the trigger, so the second Villain of a turn would still put an ability on
 *   the stack that does nothing; with an optional effect the two are indistinguishable in play.
 */
val BaronStruckerHydraOverlord = card("Baron Strucker, HYDRA Overlord") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Villain"
    power = 2
    toughness = 2
    oracleText = "Villain spells you cast cost {1} less to cast.\n" +
        "Whenever another Villain you control enters, you may have it connive. Do this only once " +
        "each turn. (Draw a card, then discard a card. If you discarded a nonland card, put a " +
        "+1/+1 counter on that creature.)"

    // Villain spells you cast cost {1} less to cast.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype.VILLAIN)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    // Whenever another Villain you control enters, you may have it connive.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.VILLAIN).youControl(),
            binding = TriggerBinding.OTHER,
        )
        oncePerTurn = true
        effect = MayEffect(Effects.Connive(EffectTarget.TriggeringEntity))
        description = "Whenever another Villain you control enters, you may have it connive. " +
            "Do this only once each turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "InHyuk Lee"
        flavorText = "\"Minions of HYDRA! Behold! Our ultimate destiny is at hand!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eaf2251a-ffaf-4055-9474-7e3d08d89609.jpg?1783902947"
    }
}
