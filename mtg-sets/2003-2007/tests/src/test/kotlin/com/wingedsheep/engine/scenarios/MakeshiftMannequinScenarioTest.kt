package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.MakeshiftMannequin
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Makeshift Mannequin (LRW #124) — "Return target creature card from your graveyard to the
 * battlefield with a mannequin counter on it. For as long as that creature has a mannequin
 * counter on it, it has 'When this creature becomes the target of a spell or ability, sacrifice
 * it.'"
 *
 * Three claims, in the order they can go wrong:
 *
 *  1. the counter and the granted ability both land on the *reanimated permanent*, not on the
 *     graveyard card — all three effects name one target handle across a zone change;
 *  2. the granted ability actually fires, and on a **friendly** targeting too. That is the whole
 *     drawback, and a `byOpponent` mis-wiring would read right on the card while making the
 *     creature strictly better than printed;
 *  3. the grant is keyed to the counter. Removing the mannequin counter must strip the drawback —
 *     `Duration.Permanent` would pass tests 1 and 2 and be wrong here, which is why this case is
 *     the point of the file.
 */
class MakeshiftMannequinScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(MakeshiftMannequin))
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Reanimate a Centaur Courser out of [me]'s graveyard; returns the permanent's id. */
    fun reanimate(d: GameTestDriver, me: com.wingedsheep.sdk.model.EntityId): com.wingedsheep.sdk.model.EntityId {
        val corpse = d.putCardInGraveyard(me, "Centaur Courser")
        val mannequin = d.putCardInHand(me, "Makeshift Mannequin")
        d.giveMana(me, Color.BLACK, 4)
        d.castSpellWithTargets(me, mannequin, listOf(entityIdToChosenTarget(d.state, corpse)))
            .isSuccess shouldBe true
        d.bothPass()
        return d.findPermanent(me, "Centaur Courser").shouldNotBeNull()
    }

    test("the creature returns with a mannequin counter on it") {
        val d = driver()
        val me = d.activePlayer!!
        val courser = reanimate(d, me)

        withClue("\"with a mannequin counter on it\"") {
            d.state.getEntity(courser)?.get<CountersComponent>()
                ?.getCount(CounterType.MANNEQUIN) shouldBe 1
        }
    }

    test("targeting the returned creature — even with your own spell — sacrifices it") {
        val d = driver()
        val me = d.activePlayer!!
        val courser = reanimate(d, me)

        if (d.priorityPlayer != me) d.passPriority(d.priorityPlayer!!)
        d.giveMana(me, Color.GREEN, 1)
        val growth = d.putCardInHand(me, "Giant Growth")
        d.castSpellWithTargets(me, growth, listOf(ChosenTarget.Permanent(courser)))
            .isSuccess shouldBe true

        withClue("the granted trigger is on the stack above Giant Growth") {
            d.stackSize shouldBe 2
        }
        d.bothPass()

        withClue("\"sacrifice it\" — the creature's own controller sacrifices it") {
            d.findPermanent(me, "Centaur Courser") shouldBe null
            d.getGraveyard(me) shouldContain courser
        }
    }

    test("removing the mannequin counter strips the drawback") {
        val d = driver()
        val me = d.activePlayer!!
        val courser = reanimate(d, me)

        // Hex Parasite's job, done bluntly: take the counter off.
        d.addComponent(courser, CountersComponent(emptyMap()))

        if (d.priorityPlayer != me) d.passPriority(d.priorityPlayer!!)
        d.giveMana(me, Color.GREEN, 1)
        val growth = d.putCardInHand(me, "Giant Growth")
        d.castSpellWithTargets(me, growth, listOf(ChosenTarget.Permanent(courser)))
            .isSuccess shouldBe true

        withClue("no counter, no granted ability, so only Giant Growth is on the stack") {
            d.stackSize shouldBe 1
        }
        d.bothPass()

        withClue("the creature survives and keeps the pump") {
            d.findPermanent(me, "Centaur Courser") shouldBe courser
            d.state.projectedState.getPower(courser) shouldBe 6
        }
    }
})
