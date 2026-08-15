package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Huatli, Poet of Unity // Roar of the Fifth People — The Lost Caverns of Ixalan #189
 * {2}{G} · Legendary Creature — Human Warrior Bard 2/3
 * //  · Enchantment — Saga
 *
 * Front — Huatli, Poet of Unity:
 *   When Huatli enters, search your library for a basic land card, reveal it, put it into your
 *   hand, then shuffle.
 *   {3}{R/W}{R/W}: Exile Huatli, then return her to the battlefield transformed under her owner's
 *   control. Activate only as a sorcery.
 *
 * Back — Roar of the Fifth People (Saga):
 *   (As this Saga enters and after your draw step, add a lore counter.)
 *   I   — Create two 3/3 green Dinosaur creature tokens.
 *   II  — This Saga gains "Creatures you control have '{T}: Add {R}, {G}, or {W}.'"
 *   III — Search your library for a Dinosaur card, reveal it, put it into your hand, then shuffle.
 *   IV  — Dinosaurs you control gain double strike and trample until end of turn.
 *
 * Transform loop mirrors Dion, Bahamut's Dominant / Terra, Magical Adept: the sorcery-speed
 * activated ability exile-returns Huatli transformed, so Roar re-enters the battlefield as a Saga
 * and picks up CR 714.3a's on-enter lore counter (chapter I triggers) via the shared Saga-entry
 * hook — no explicit lore-counter effect is needed. The back is a non-creature enchantment, so the
 * DFC is assembled with [CardDefinition.doubleFacedPermanent] rather than `doubleFacedCreature`.
 *
 * Chapter II grants Roar itself a lasting static ability (Duration.Permanent — it ends when Roar
 * leaves, i.e. the CR 714.4 final-chapter sacrifice), and that static is the Citanul Hierophants
 * "creatures you control have '{T}: Add …'" shape: a [GrantActivatedAbility] handing every creature
 * you control a `{T}` mana ability that produces one mana chosen from {R}/{G}/{W}
 * ([ManaColorSet.Specific]).
 */

private val RoarOfTheFifthPeople = card("Roar of the Fifth People") {
    manaCost = ""
    colorIdentity = "WRG"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter.)\n" +
        "I — Create two 3/3 green Dinosaur creature tokens.\n" +
        "II — This Saga gains \"Creatures you control have '{T}: Add {R}, {G}, or {W}.'\"\n" +
        "III — Search your library for a Dinosaur card, reveal it, put it into your hand, then " +
        "shuffle.\n" +
        "IV — Dinosaurs you control gain double strike and trample until end of turn."

    // I — Create two 3/3 green Dinosaur creature tokens.
    sagaChapter(1) {
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Dinosaur"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/2/b/2bbb7151-cf71-49bc-8d99-b0230d5465e5.jpg?1783913607",
        )
    }

    // II — This Saga gains "Creatures you control have '{T}: Add {R}, {G}, or {W}.'"
    sagaChapter(2) {
        effect = Effects.GrantStaticAbility(
            ability = GrantActivatedAbility(
                ability = ActivatedAbility(
                    id = AbilityId.generate(),
                    cost = Costs.Tap,
                    effect = Effects.AddManaOfChoice(
                        ManaColorSet.Specific(setOf(Color.RED, Color.GREEN, Color.WHITE)),
                    ),
                ),
                filter = GroupFilter(GameObjectFilter.Creature.youControl()),
            ),
            target = EffectTarget.Self,
            duration = Duration.Permanent,
        )
    }

    // III — Search your library for a Dinosaur card, reveal it, put it into your hand, then shuffle.
    sagaChapter(3) {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.withSubtype("Dinosaur"),
            count = 1,
            reveal = true,
        )
    }

    // IV — Dinosaurs you control gain double strike and trample until end of turn.
    sagaChapter(4) {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype("Dinosaur").youControl()),
            Effects.Composite(
                Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "189"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/back/5/7/57df2563-18d4-4526-a8bc-0c114e6fd4d9.jpg?1782694458"
    }
}

private val HuatliPoetOfUnityFront = card("Huatli, Poet of Unity") {
    manaCost = "{2}{G}"
    colorIdentity = "WRG"
    typeLine = "Legendary Creature — Human Warrior Bard"
    oracleText = "When Huatli enters, search your library for a basic land card, reveal it, put it " +
        "into your hand, then shuffle.\n" +
        "{3}{R/W}{R/W}: Exile Huatli, then return her to the battlefield transformed under her " +
        "owner's control. Activate only as a sorcery."
    power = 2
    toughness = 3

    // When Huatli enters, search your library for a basic land card, reveal it, put it into your
    // hand, then shuffle.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            reveal = true,
        )
    }

    // {3}{R/W}{R/W}: Exile Huatli, then return her transformed. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Mana("{3}{R/W}{R/W}")
        timing = TimingRule.SorcerySpeed
        effect = Effects.ExileAndReturnTransformed()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "189"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57df2563-18d4-4526-a8bc-0c114e6fd4d9.jpg?1782694458"
    }
}

val HuatliPoetOfUnity: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = HuatliPoetOfUnityFront,
    backFace = RoarOfTheFifthPeople,
)
