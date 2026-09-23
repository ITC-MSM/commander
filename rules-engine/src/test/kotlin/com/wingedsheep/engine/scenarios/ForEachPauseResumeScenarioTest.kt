package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.engine.core.Outcome

/**
 * Pause/resume safety for every [com.wingedsheep.sdk.scripting.effects.ForEachEffect]
 * iteration space (the unified executor pre-pushes a `ForEachContinuation`, so a body
 * that pauses for a decision mid-iteration resumes the remaining iterations).
 *
 * The group and collection spaces are the regression targets: pre-unification, their
 * executors propagated a pause without any continuation for the remaining entities, so
 * everything after the first pausing iteration was silently dropped.
 */
class ForEachPauseResumeScenarioTest : FunSpec({

    // "For each creature you control, you may draw a card." — the body pauses with a
    // yes/no decision on every iteration (group space).
    val groupMayDraw = card("Group May Draw") {
        manaCost = "{G}"
        colorIdentity = "G"
        typeLine = "Sorcery"
        oracleText = "For each creature you control, you may draw a card."
        spell {
            effect = Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.youControl()),
                effect = MayEffect(Effects.DrawCards(1))
            )
        }
    }

    // Same shape over a gathered pipeline collection (collection space).
    val collectionMayDraw = card("Collection May Draw") {
        manaCost = "{G}"
        colorIdentity = "G"
        typeLine = "Sorcery"
        oracleText = "For each creature you control, you may draw a card."
        spell {
            effect = GatherCardsEffect(
                source = CardSource.ControlledPermanents(
                    player = Player.You,
                    filter = GameObjectFilter.Creature
                ),
                storeAs = "creatures"
            ).then(
                ForEachInCollectionEffect(
                    collection = "creatures",
                    effect = MayEffect(Effects.DrawCards(1))
                )
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(groupMayDraw, collectionMayDraw))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castAndResolve(player: com.wingedsheep.sdk.model.EntityId, cardName: String) {
        giveMana(player, Color.GREEN, 1)
        val spell = putCardInHand(player, cardName)
        castSpell(player, spell).outcome shouldBe Outcome.Done
        bothPass() // resolve the sorcery; first iteration's decision pauses resolution
    }

    listOf(
        "Group May Draw" to "group space",
        "Collection May Draw" to "collection space",
    ).forEach { (cardName, space) ->

        test("$space: a body pausing mid-iteration resumes the remaining iterations") {
            val driver = createDriver()
            val me = driver.activePlayer!!

            repeat(3) { driver.putCreatureOnBattlefield(me, "Grizzly Bears") }
            val handBefore = driver.getHandSize(me)

            driver.castAndResolve(me, cardName)

            // One yes/no decision per creature — not just the first. Answering yes
            // draws, then the next iteration pauses again with its own decision.
            repeat(3) { iteration ->
                driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
                driver.submitYesNo(me, true)
                driver.getHandSize(me) shouldBe handBefore + iteration + 1
            }
            driver.pendingDecision.shouldBeNull()
        }

        test("$space: declining one iteration still offers the remaining ones") {
            val driver = createDriver()
            val me = driver.activePlayer!!

            repeat(3) { driver.putCreatureOnBattlefield(me, "Grizzly Bears") }
            val handBefore = driver.getHandSize(me)

            driver.castAndResolve(me, cardName)

            driver.submitYesNo(me, true)
            driver.submitYesNo(me, false)
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            driver.submitYesNo(me, true)

            driver.pendingDecision.shouldBeNull()
            driver.getHandSize(me) shouldBe handBefore + 2
        }
    }
})
