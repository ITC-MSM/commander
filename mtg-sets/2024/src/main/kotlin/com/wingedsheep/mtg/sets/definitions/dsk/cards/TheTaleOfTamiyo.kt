package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.RepeatCondition
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The Tale of Tamiyo
 * {2}{U}
 * Legendary Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)
 * I, II, III — Mill two cards. If two cards that share a card type were milled this way, draw a
 *   card and repeat this process.
 * IV — Exile any number of target instant, sorcery, and/or Tamiyo planeswalker cards from your
 *   graveyard. Copy them. You may cast any number of the copies.
 *
 * Chapters I–III are a do-while loop ([Effects.RepeatWhile]): each pass mills two cards (stored
 * under the `milled` collection), and "if two cards that share a card type were milled this way"
 * ([Conditions.CollectionSharesCardType]) both draws a card and repeats. The repeat condition reads
 * the *current* pass's `milled` collection, so the loop ends as soon as a pass mills two cards with
 * no shared card type — or fewer than two cards (CR 205.2a; per the card's rulings supertypes and
 * subtypes are not card types).
 *
 * Chapter IV exiles any number of targeted graveyard cards, copies them as a group
 * ([Effects.CopyCollectionIntoCollection], the copies created in exile), then lets you cast any
 * number of those copies **paying their costs** ([Effects.CastAnyNumberFromCollection] — the
 * wording omits "without paying their mana costs", and an {X} copy prompts for X). Casts happen
 * during this resolution, so timing restrictions are ignored; copies left uncast cease to exist via
 * the Rule 707.10a state-based action.
 */
val TheTaleOfTamiyo = card("The Tale of Tamiyo") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)\n" +
        "I, II, III — Mill two cards. If two cards that share a card type were milled this way, " +
        "draw a card and repeat this process.\n" +
        "IV — Exile any number of target instant, sorcery, and/or Tamiyo planeswalker cards from " +
        "your graveyard. Copy them. You may cast any number of the copies."

    // I, II, III — mill two, and while the two milled cards share a card type, draw and repeat.
    val millRepeat = Effects.RepeatWhile(
        body = Effects.Composite(
            Patterns.Library.mill(2),
            ConditionalEffect(
                condition = Conditions.CollectionSharesCardType("milled"),
                effect = Effects.DrawCards(1),
            ),
        ),
        repeatCondition = RepeatCondition.WhileCondition(
            Conditions.CollectionSharesCardType("milled")
        ),
    )

    sagaChapter(1) { effect = millRepeat }
    sagaChapter(2) { effect = millRepeat }
    sagaChapter(3) { effect = millRepeat }

    sagaChapter(4) {
        target(
            "any number of target instant, sorcery, and/or Tamiyo planeswalker cards from your graveyard",
            TargetObject(
                unlimited = true,
                filter = TargetFilter(
                    GameObjectFilter.InstantOrSorcery.ownedByYou()
                        .or(GameObjectFilter.Planeswalker.withSubtype("Tamiyo").ownedByYou()),
                    zone = Zone.GRAVEYARD,
                ),
            )
        )
        effect = Effects.Composite(
            ForEachTargetEffect(listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE))),
            GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "tamiyoExiled"),
            Effects.CopyCollectionIntoCollection(from = "tamiyoExiled", storeAs = "tamiyoCopies"),
            Effects.CastAnyNumberFromCollection(from = "tamiyoCopies"),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Anna Pavleeva"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aaeba193-05d3-4c2d-a304-bbe7114c2eef.jpg?1726326895"

        ruling(
            "2024-09-20",
            "When chapter I, II, or III resolves, the ability continues until you mill a set of " +
                "two cards that share no card types or you mill one or fewer cards."
        )
        ruling(
            "2024-09-20",
            "The card types are artifact, battle, creature, enchantment, instant, kindred, land, " +
                "planeswalker, and sorcery. Legendary, basic, and snow are supertypes; Horror and " +
                "Room are subtypes — neither counts as a card type."
        )
        ruling(
            "2024-09-20",
            "You cast the copies from chapter IV while that chapter ability is resolving. Because " +
                "you pay the spells' costs, you choose the value of any {X}. Copies you don't cast " +
                "cease to exist the next time state-based actions are checked."
        )
    }
}
